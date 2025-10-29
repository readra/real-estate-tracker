package com.realestate.tracker.domain.property.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 아파트 매매 실거래 정보 엔티티
 * 
 * @author Generated from toy-real-estate-backend
 */
@Entity
@Table(name = "apt_trades")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class AptTrade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private BigDecimal transactionAmount;  // 거래금액
    
    @Column(nullable = false)
    private Integer buildingYear;  // 건축년도
    
    @Column(nullable = false)
    private LocalDate transactionDate;  // 거래일
    
    @Column(nullable = false, length = 100)
    private String legalDong;  // 법정동
    
    @Column(nullable = false, length = 200)
    private String apartmentName;  // 아파트명
    
    @Column(nullable = false)
    private Double exclusiveArea;  // 전용면적
    
    @Column(length = 50)
    private String localNumber;  // 지번
    
    @Column(nullable = false, length = 10)
    private String lawdCode;  // 지역코드
    
    @Column(nullable = false)
    private Integer floor;  // 층
    
    // 추가 필드
    @Column(length = 50)
    private String city;  // 시
    
    @Column(length = 50)
    private String district;  // 구
    
    @Column(length = 50)
    private String dong;  // 동
    
    @Column
    private Boolean isCanceled;  // 거래취소여부
    
    @Column(length = 50)
    private String dealType;  // 거래유형
    
    @Column
    private LocalDate canceledDate;  // 해제일
    
    @Column(length = 200)
    private String canceledReason;  // 해제사유
    
    /**
     * 거래금액 유효성 검사
     */
    public boolean isValidPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return true;
        }
        
        if (maxPrice == null) {
            return transactionAmount.compareTo(minPrice) >= 0;
        }
        
        if (minPrice == null) {
            return transactionAmount.compareTo(maxPrice) <= 0;
        }
        
        return transactionAmount.compareTo(minPrice) >= 0 
            && transactionAmount.compareTo(maxPrice) <= 0;
    }
}
