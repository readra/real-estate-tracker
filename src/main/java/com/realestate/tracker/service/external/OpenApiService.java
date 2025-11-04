package com.realestate.tracker.service.external;

import com.realestate.tracker.domain.property.entity.AptTrade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 공공데이터포털 Open API 연동 Service
 * 국토교통부_아파트매매 실거래 상세 자료 조회
 *
 * @author Generated from toy-real-estate-backend
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {
    
    @Value("${openapi.key:}")
    private String serviceKey;
    
    @Value("${openapi.apt-trade.url}")
    private String aptTradeApiUrl;
    
    private final WebClient webClient = WebClient.builder().build();
    
    /**
     * Open API에서 아파트 매매 실거래 데이터 조회
     *
     * @param lawdCode 지역코드 (5자리 법정동코드)
     * @param yearMonth 조회년월
     * @return 아파트 거래 목록
     */
    public List<AptTrade> fetchAptTrades(String lawdCode, YearMonth yearMonth) {
        List<AptTrade> trades = new ArrayList<>();
        
        if (serviceKey == null || serviceKey.isEmpty()) {
            log.warn("Open API service key is not configured");
            return trades;
        }
        
        try {
            String dealYmd = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
            
            // API URL 생성
            String apiUrl = buildApiUrl(lawdCode, dealYmd);
            log.info("Fetching apt trades from API: {}", apiUrl);
            
            // API 호출
            String response = webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            if (response != null) {
                // XML 파싱
                trades = parseAptTradeXml(response, lawdCode);
                log.info("Fetched {} apt trades for {}/{}", trades.size(), lawdCode, dealYmd);
            }
            
        } catch (Exception e) {
            log.error("Failed to fetch apt trades from Open API for {}/{}: {}", 
                    lawdCode, yearMonth, e.getMessage());
        }
        
        return trades;
    }
    
    /**
     * API URL 생성
     * 공공데이터포털 API 스펙에 맞춰 URL 생성
     */
    private String buildApiUrl(String lawdCode, String dealYmd) {
        StringBuilder urlBuilder = new StringBuilder(aptTradeApiUrl);
        
        urlBuilder.append("?serviceKey=").append(serviceKey);
        urlBuilder.append("&pageNo=1");
        urlBuilder.append("&numOfRows=9999");  // 최대 조회 건수
        urlBuilder.append("&LAWD_CD=").append(lawdCode);
        urlBuilder.append("&DEAL_YMD=").append(dealYmd);
        
        return urlBuilder.toString();
    }
    
    /**
     * XML 응답 파싱
     * 공공데이터포털 XML 응답 구조에 맞춰 파싱
     */
    private List<AptTrade> parseAptTradeXml(String xmlResponse, String lawdCode) {
        List<AptTrade> trades = new ArrayList<>();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8)));
            
            doc.getDocumentElement().normalize();
            
            // 에러 응답 확인
            NodeList resultCode = doc.getElementsByTagName("resultCode");
            if (resultCode.getLength() > 0) {
                String code = resultCode.item(0).getTextContent();
                if (!"00".equals(code)) {
                    NodeList resultMsg = doc.getElementsByTagName("resultMsg");
                    String msg = resultMsg.getLength() > 0 ? resultMsg.item(0).getTextContent() : "Unknown error";
                    log.error("Open API error - code: {}, message: {}", code, msg);
                    return trades;
                }
            }
            
            // item 노드 파싱
            NodeList items = doc.getElementsByTagName("item");
            log.debug("Found {} items in XML response", items.getLength());
            
            for (int i = 0; i < items.getLength(); i++) {
                Node node = items.item(i);
                
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    AptTrade trade = parseAptTradeElement(element, lawdCode);
                    if (trade != null) {
                        trades.add(trade);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse XML response: {}", e.getMessage(), e);
        }
        
        return trades;
    }
    
    /**
     * XML Element를 AptTrade 객체로 변환
     * 공공데이터포털 API 응답 필드명에 맞춰 매핑
     */
    private AptTrade parseAptTradeElement(Element element, String lawdCode) {
        try {
            // 필수 필드 존재 여부 확인
            String amountStr = getTagValue("거래금액", element).trim();
            if (amountStr.isEmpty()) {
                log.warn("Missing required field: 거래금액");
                return null;
            }
            
            // 거래금액 파싱 (쉼표 제거 후 만원 단위)
            String cleanAmount = amountStr.replaceAll(",", "").trim();
            BigDecimal amount = new BigDecimal(cleanAmount);
            
            // 거래일자 파싱
            String yearStr = getTagValue("년", element).trim();
            String monthStr = getTagValue("월", element).trim();
            String dayStr = getTagValue("일", element).trim();
            
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);
            LocalDate transactionDate = LocalDate.of(year, month, day);
            
            // 건축년도 파싱
            String buildYearStr = getTagValue("건축년도", element).trim();
            int buildYear = buildYearStr.isEmpty() ? 0 : Integer.parseInt(buildYearStr);
            
            // 전용면적 파싱
            String areaStr = getTagValue("전용면적", element).trim();
            double area = areaStr.isEmpty() ? 0.0 : Double.parseDouble(areaStr);
            
            // 층수 파싱
            String floorStr = getTagValue("층", element).trim();
            int floor = floorStr.isEmpty() ? 0 : Integer.parseInt(floorStr);
            
            // 아파트명 및 지역 정보
            String apartmentName = getTagValue("아파트", element).trim();
            String dong = getTagValue("법정동", element).trim();
            String jibun = getTagValue("지번", element).trim();
            
            // 해제여부 (해제사유발생시 값이 있음)
            String cancelDeal = getTagValue("해제여부", element).trim();
            boolean isCanceled = cancelDeal != null && !cancelDeal.isEmpty();
            
            return AptTrade.builder()
                    .lawdCode(lawdCode)
                    .apartmentName(apartmentName)
                    .transactionAmount(amount)
                    .buildingYear(buildYear)
                    .transactionDate(transactionDate)
                    .exclusiveArea(area)
                    .floor(floor)
                    .dong(dong)
                    .localNumber(jibun)
                    .legalDong(dong)
                    .isCanceled(isCanceled)
                    .build();
                    
        } catch (NumberFormatException e) {
            log.error("Failed to parse number field in apt trade element: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Failed to parse apt trade element: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * XML Element에서 태그 값 추출
     */
    private String getTagValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag);
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node != null && node.getFirstChild() != null) {
                    String value = node.getFirstChild().getNodeValue();
                    return value != null ? value : "";
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get tag value for {}: {}", tag, e.getMessage());
        }
        return "";
    }
}
