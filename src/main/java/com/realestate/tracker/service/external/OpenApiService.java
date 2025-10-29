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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 공공데이터 Open API 연동 Service
 *
 * @author Generated from toy-real-estate-backend
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {
    
    @Value("${openapi.key:}")
    private String serviceKey;
    
    @Value("${openapi.apt-trade.url:http://openapi.molit.go.kr/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTradeDev}")
    private String aptTradeApiUrl;
    
    private final WebClient webClient = WebClient.builder().build();
    
    /**
     * Open API에서 아파트 매매 실거래 데이터 조회
     *
     * @param lawdCode 지역코드
     * @param yearMonth 조회년월
     * @return 아파트 거래 목록
     */
    public List<AptTrade> fetchAptTrades(String lawdCode, YearMonth yearMonth) {
        List<AptTrade> trades = new ArrayList<>();
        
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
            
            // XML 파싱
            trades = parseAptTradeXml(response, lawdCode);
            log.info("Fetched {} apt trades for {}/{}", trades.size(), lawdCode, dealYmd);
            
        } catch (Exception e) {
            log.error("Failed to fetch apt trades from Open API", e);
        }
        
        return trades;
    }
    
    /**
     * API URL 생성
     */
    private String buildApiUrl(String lawdCode, String dealYmd) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(aptTradeApiUrl);
        
        // Decode service key if it's Base64 encoded
        String decodedServiceKey = serviceKey;
        try {
            decodedServiceKey = new String(Base64.getDecoder().decode(serviceKey));
        } catch (IllegalArgumentException e) {
            // Not Base64 encoded, use as is
        }
        
        urlBuilder.append("?serviceKey=").append(URLEncoder.encode(decodedServiceKey, StandardCharsets.UTF_8));
        urlBuilder.append("&pageNo=1");
        urlBuilder.append("&numOfRows=1000");
        urlBuilder.append("&LAWD_CD=").append(lawdCode);
        urlBuilder.append("&DEAL_YMD=").append(dealYmd);
        
        return urlBuilder.toString();
    }
    
    /**
     * XML 응답 파싱
     */
    private List<AptTrade> parseAptTradeXml(String xmlResponse, String lawdCode) {
        List<AptTrade> trades = new ArrayList<>();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8)));
            
            doc.getDocumentElement().normalize();
            NodeList items = doc.getElementsByTagName("item");
            
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
            log.error("Failed to parse XML response", e);
        }
        
        return trades;
    }
    
    /**
     * XML Element를 AptTrade 객체로 변환
     */
    private AptTrade parseAptTradeElement(Element element, String lawdCode) {
        try {
            // 거래금액 파싱 (쉼표 제거 후 변환)
            String amountStr = getTagValue("거래금액", element)
                .trim()
                .replaceAll(",", "");
            BigDecimal amount = new BigDecimal(amountStr);
            
            // 거래일자 파싱
            int year = Integer.parseInt(getTagValue("년", element));
            int month = Integer.parseInt(getTagValue("월", element));
            int day = Integer.parseInt(getTagValue("일", element));
            LocalDate transactionDate = LocalDate.of(year, month, day);
            
            return AptTrade.builder()
                .transactionAmount(amount)
                .buildingYear(Integer.parseInt(getTagValue("건축년도", element)))
                .transactionDate(transactionDate)
                .legalDong(getTagValue("법정동", element).trim())
                .apartmentName(getTagValue("아파트", element).trim())
                .exclusiveArea(Double.parseDouble(getTagValue("전용면적", element)))
                .localNumber(getTagValue("지번", element))
                .lawdCode(lawdCode)
                .floor(Integer.parseInt(getTagValue("층", element)))
                .dong(getTagValue("법정동", element).trim())
                .isCanceled(false)
                .build();
                
        } catch (Exception e) {
            log.error("Failed to parse apt trade element", e);
            return null;
        }
    }
    
    /**
     * XML Element에서 태그 값 추출
     */
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null && node.getFirstChild() != null) {
                return node.getFirstChild().getNodeValue();
            }
        }
        return "";
    }
}
