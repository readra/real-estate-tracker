package com.realestate.tracker.repository;

import com.realestate.tracker.domain.property.entity.AptTrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 아파트 매매 실거래 Repository
 */
@Repository
public interface AptTradeRepository extends JpaRepository<AptTrade, Long> {
    
    /**
     * 검색 조건에 따른 아파트 거래 조회
     */
    @Query("SELECT a FROM AptTrade a WHERE " +
           "(:lawdCode IS NULL OR a.lawdCode = :lawdCode) AND " +
           "(:startDate IS NULL OR a.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.transactionDate <= :endDate) AND " +
           "(:minPrice IS NULL OR a.transactionAmount >= :minPrice) AND " +
           "(:maxPrice IS NULL OR a.transactionAmount <= :maxPrice)")
    Page<AptTrade> findBySearchCondition(
        @Param("lawdCode") String lawdCode,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
    
    /**
     * 아파트명과 동으로 거래 이력 조회
     */
    @Query("SELECT a FROM AptTrade a WHERE " +
           "a.apartmentName = :apartmentName AND " +
           "(:dong IS NULL OR a.dong = :dong) " +
           "ORDER BY a.transactionDate DESC")
    List<AptTrade> findByApartmentNameAndDong(
        @Param("apartmentName") String apartmentName,
        @Param("dong") String dong
    );
    
    /**
     * 자주 거래되는 아파트 조회 (위험 신호)
     */
    @Query(value = "SELECT a.* FROM apt_trades a " +
           "WHERE a.lawd_code = :lawdCode " +
           "AND a.transaction_date >= CURRENT_DATE - INTERVAL ':months months' " +
           "GROUP BY a.apartment_name, a.dong " +
           "HAVING COUNT(*) >= :minCount", 
           nativeQuery = true)
    List<AptTrade> findFrequentlyTradedApartments(
        @Param("lawdCode") String lawdCode,
        @Param("months") int months,
        @Param("minCount") int minCount
    );
    
    /**
     * 지역별 평균 거래가 조회
     */
    @Query("SELECT AVG(a.transactionAmount) FROM AptTrade a WHERE " +
           "a.lawdCode = :lawdCode AND " +
           "a.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal findAveragePrice(
        @Param("lawdCode") String lawdCode,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 최근 거래 조회
     */
    List<AptTrade> findTop10ByLawdCodeOrderByTransactionDateDesc(String lawdCode);
}
