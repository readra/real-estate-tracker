package com.realestate.tracker.domain.property.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 아파트 매매 실거래 검색 조건 DTO
 * 
 * @author Generated from toy-real-estate-backend
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AptTradeSearchCondition {
    
    // 지역 검색 조건
    private String lawdCode;  // 지역코드
    private String city;      // 시
    private String district;  // 구
    private String dong;      // 동
    
    // 기간 검색 조건
    private YearMonth startYearMonth;
    private YearMonth endYearMonth;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // 가격 범위 조건
    private BigDecimal startTransactionAmount;
    private BigDecimal endTransactionAmount;
    
    // 페이징 조건
    private Integer page = 0;
    private Integer size = 20;
    private Integer itemCount = 100;
    
    // 정렬 조건
    private String sortBy = "transactionDate";
    private String sortDirection = "DESC";
    
    // 추가 검색 조건
    private String apartmentName;  // 아파트명
    private Double minExclusiveArea;  // 최소 전용면적
    private Double maxExclusiveArea;  // 최대 전용면적
    private Integer minBuildingYear;  // 최소 건축년도
    private Integer maxBuildingYear;  // 최대 건축년도
    
    /**
     * 검색 조건 유효성 검사
     */
    public boolean isValid() {
        if (startYearMonth != null && endYearMonth != null) {
            if (startYearMonth.isAfter(endYearMonth)) {
                return false;
            }
        }
        
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                return false;
            }
        }
        
        if (startTransactionAmount != null && endTransactionAmount != null) {
            if (startTransactionAmount.compareTo(endTransactionAmount) > 0) {
                return false;
            }
        }
        
        return true;
    }
}
