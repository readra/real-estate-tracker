package com.realestate.tracker.service;

import com.realestate.tracker.domain.property.dto.AptTradeSearchCondition;
import com.realestate.tracker.domain.property.entity.AptTrade;
import com.realestate.tracker.repository.AptTradeRepository;
import com.realestate.tracker.service.external.OpenApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * 아파트 매매 실거래 Service Layer
 *
 * @author Generated from toy-real-estate-backend
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AptTradeService {
    
    private final AptTradeRepository aptTradeRepository;
    private final OpenApiService openApiService;
    
    /**
     * 아파트 매매 실거래 목록 조회
     *
     * @param searchCondition 검색 조건
     * @return 아파트 매매 실거래 목록
     */
    public Page<AptTrade> findAptTrades(AptTradeSearchCondition searchCondition) {
        // 검색 조건 유효성 검사
        if (!searchCondition.isValid()) {
            throw new IllegalArgumentException("Invalid search condition");
        }
        
        // Pageable 생성
        Sort sort = Sort.by(
            searchCondition.getSortDirection().equals("ASC") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC,
            searchCondition.getSortBy()
        );
        Pageable pageable = PageRequest.of(searchCondition.getPage(), searchCondition.getSize(), sort);
        
        // DB에서 먼저 조회
        Page<AptTrade> result = aptTradeRepository.findBySearchCondition(
            searchCondition.getLawdCode(),
            searchCondition.getStartDate(),
            searchCondition.getEndDate(),
            searchCondition.getStartTransactionAmount(),
            searchCondition.getEndTransactionAmount(),
            pageable
        );
        
        // 데이터가 없으면 Open API에서 가져와서 저장
        if (result.isEmpty() && searchCondition.getStartYearMonth() != null) {
            List<AptTrade> apiData = fetchAndSaveFromOpenApi(searchCondition);
            return aptTradeRepository.findBySearchCondition(
                searchCondition.getLawdCode(),
                searchCondition.getStartDate(),
                searchCondition.getEndDate(),
                searchCondition.getStartTransactionAmount(),
                searchCondition.getEndTransactionAmount(),
                pageable
            );
        }
        
        return result;
    }
    
    /**
     * Open API에서 데이터 조회 및 저장
     */
    @Transactional
    public List<AptTrade> fetchAndSaveFromOpenApi(AptTradeSearchCondition searchCondition) {
        List<AptTrade> allTrades = new ArrayList<>();
        
        YearMonth current = searchCondition.getStartYearMonth();
        YearMonth end = searchCondition.getEndYearMonth() != null 
            ? searchCondition.getEndYearMonth() 
            : YearMonth.now();
        
        while (!current.isAfter(end)) {
            try {
                // Open API 호출
                List<AptTrade> monthlyTrades = openApiService.fetchAptTrades(
                    searchCondition.getLawdCode(),
                    current
                );
                
                // DB 저장
                if (!monthlyTrades.isEmpty()) {
                    allTrades.addAll(aptTradeRepository.saveAll(monthlyTrades));
                }
                
                current = current.plusMonths(1);
            } catch (Exception e) {
                log.error("Failed to fetch apt trades for {}: {}", current, e.getMessage());
                current = current.plusMonths(1);
            }
        }
        
        return allTrades;
    }
    
    /**
     * 특정 아파트 거래 이력 조회
     */
    public List<AptTrade> findAptTradeHistory(String apartmentName, String dong) {
        return aptTradeRepository.findByApartmentNameAndDong(apartmentName, dong);
    }
    
    /**
     * 계약 만료 임박 물건 조회 (전세 기능 확장시 사용)
     */
    public List<AptTrade> findExpiringContracts(int daysBeforeExpiry) {
        // 추후 전세 계약 만료 기능 구현시 활용
        log.info("Contract expiry feature will be implemented for lease contracts");
        return new ArrayList<>();
    }
    
    /**
     * 거래 빈도가 높은 위험 물건 탐지
     */
    public List<AptTrade> findFrequentlyTradedApartments(String lawdCode, int months, int minTransactionCount) {
        return aptTradeRepository.findFrequentlyTradedApartments(
            lawdCode, 
            months, 
            minTransactionCount
        );
    }
}
