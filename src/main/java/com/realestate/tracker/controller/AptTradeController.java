package com.realestate.tracker.controller;

import com.realestate.tracker.domain.property.dto.AptTradeSearchCondition;
import com.realestate.tracker.domain.property.entity.AptTrade;
import com.realestate.tracker.dto.response.ApiResponse;
import com.realestate.tracker.service.AptTradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 아파트 매매 실거래 Controller
 *
 * @author Generated from toy-real-estate-backend
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/apt-trades")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@Tag(name = "아파트 거래", description = "아파트 매매 실거래 관련 API")
public class AptTradeController {
    
    private final AptTradeService aptTradeService;
    
    /**
     * 아파트 매매 실거래 목록 조회
     */
    @GetMapping
    @Operation(summary = "아파트 매매 실거래 목록 조회", 
               description = "지역, 기간, 가격 등의 조건으로 아파트 매매 실거래를 조회합니다.")
    public ResponseEntity<ApiResponse<Page<AptTrade>>> getAptTrades(
        @Parameter(description = "검색 조건") AptTradeSearchCondition searchCondition
    ) {
        log.info("Request apt trades with condition: {}", searchCondition);
        
        try {
            Page<AptTrade> result = aptTradeService.findAptTrades(searchCondition);
            
            return ResponseEntity.ok(ApiResponse.success(
                result,
                "아파트 매매 실거래 조회 성공"
            ));
        } catch (IllegalArgumentException e) {
            log.error("Invalid search condition: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("잘못된 검색 조건입니다: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to get apt trades", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("아파트 매매 실거래 조회 실패"));
        }
    }
    
    /**
     * 특정 아파트 거래 이력 조회
     */
    @GetMapping("/history")
    @Operation(summary = "아파트 거래 이력 조회", 
               description = "특정 아파트의 거래 이력을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AptTrade>>> getAptTradeHistory(
        @RequestParam @Parameter(description = "아파트명", required = true) String apartmentName,
        @RequestParam(required = false) @Parameter(description = "동") String dong
    ) {
        log.info("Request apt trade history - apartment: {}, dong: {}", apartmentName, dong);
        
        try {
            List<AptTrade> history = aptTradeService.findAptTradeHistory(apartmentName, dong);
            
            return ResponseEntity.ok(ApiResponse.success(
                history,
                String.format("%s 거래 이력 조회 성공 (총 %d건)", apartmentName, history.size())
            ));
        } catch (Exception e) {
            log.error("Failed to get apt trade history", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("거래 이력 조회 실패"));
        }
    }
    
    /**
     * 자주 거래되는 위험 아파트 조회
     */
    @GetMapping("/risky")
    @Operation(summary = "위험 신호 아파트 조회", 
               description = "짧은 기간 내 자주 거래된 아파트를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AptTrade>>> getRiskyApartments(
        @RequestParam @Parameter(description = "지역코드", required = true) String lawdCode,
        @RequestParam(defaultValue = "6") @Parameter(description = "조회 기간(개월)") int months,
        @RequestParam(defaultValue = "3") @Parameter(description = "최소 거래 횟수") int minTransactionCount
    ) {
        log.info("Request risky apartments - lawdCode: {}, months: {}, minCount: {}", 
                lawdCode, months, minTransactionCount);
        
        try {
            List<AptTrade> riskyApartments = aptTradeService.findFrequentlyTradedApartments(
                lawdCode, months, minTransactionCount
            );
            
            return ResponseEntity.ok(ApiResponse.success(
                riskyApartments,
                String.format("위험 신호 아파트 %d건 발견", riskyApartments.size())
            ));
        } catch (Exception e) {
            log.error("Failed to get risky apartments", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("위험 아파트 조회 실패"));
        }
    }
    
    /**
     * Open API에서 최신 데이터 동기화
     */
    @PostMapping("/sync")
    @Operation(summary = "데이터 동기화", 
               description = "Open API에서 최신 아파트 거래 데이터를 가져옵니다.")
    public ResponseEntity<ApiResponse<List<AptTrade>>> syncAptTrades(
        @RequestBody @Parameter(description = "동기화 조건") AptTradeSearchCondition searchCondition
    ) {
        log.info("Request data sync with condition: {}", searchCondition);
        
        try {
            List<AptTrade> syncedData = aptTradeService.fetchAndSaveFromOpenApi(searchCondition);
            
            return ResponseEntity.ok(ApiResponse.success(
                syncedData,
                String.format("데이터 동기화 완료 (총 %d건)", syncedData.size())
            ));
        } catch (Exception e) {
            log.error("Failed to sync apt trades", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("데이터 동기화 실패"));
        }
    }
}
