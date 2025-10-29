package com.realestate.tracker.controller;

import com.realestate.tracker.domain.common.enums.LawdSiType;
import com.realestate.tracker.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 지역 정보 Controller
 *
 * @author Generated from toy-real-estate-backend
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/lawd")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@Tag(name = "지역 정보", description = "법정동 코드 및 지역 정보 관련 API")
public class LawdController {
    
    /**
     * 시/도 목록 조회
     */
    @GetMapping("/si")
    @Operation(summary = "시/도 목록 조회", description = "전국 시/도 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSiList() {
        log.info("Request si list");
        
        List<Map<String, Object>> siList = Arrays.stream(LawdSiType.values())
            .map(si -> {
                Map<String, Object> map = new HashMap<>();
                map.put("code", si.getCode());
                map.put("name", si.getName());
                return map;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(siList, "시/도 목록 조회 성공"));
    }
    
    /**
     * 구/군 목록 조회 (추후 구현)
     */
    @GetMapping("/gu")
    @Operation(summary = "구/군 목록 조회", description = "선택한 시/도의 구/군 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getGuList(
        @RequestParam String siCode
    ) {
        log.info("Request gu list for si: {}", siCode);
        
        // TODO: 구/군 데이터 구현 필요
        // 임시 데이터
        List<Map<String, Object>> guList = List.of(
            Map.of("code", "11010", "name", "종로구"),
            Map.of("code", "11020", "name", "중구"),
            Map.of("code", "11030", "name", "용산구"),
            Map.of("code", "11040", "name", "성동구"),
            Map.of("code", "11050", "name", "광진구"),
            Map.of("code", "11060", "name", "동대문구"),
            Map.of("code", "11070", "name", "중랑구"),
            Map.of("code", "11080", "name", "성북구"),
            Map.of("code", "11090", "name", "강북구"),
            Map.of("code", "11100", "name", "도봉구"),
            Map.of("code", "11110", "name", "노원구"),
            Map.of("code", "11120", "name", "은평구"),
            Map.of("code", "11130", "name", "서대문구"),
            Map.of("code", "11140", "name", "마포구"),
            Map.of("code", "11150", "name", "양천구"),
            Map.of("code", "11160", "name", "강서구"),
            Map.of("code", "11170", "name", "구로구"),
            Map.of("code", "11180", "name", "금천구"),
            Map.of("code", "11190", "name", "영등포구"),
            Map.of("code", "11200", "name", "동작구"),
            Map.of("code", "11210", "name", "관악구"),
            Map.of("code", "11220", "name", "서초구"),
            Map.of("code", "11230", "name", "강남구"),
            Map.of("code", "11240", "name", "송파구"),
            Map.of("code", "11250", "name", "강동구")
        );
        
        return ResponseEntity.ok(ApiResponse.success(guList, "구/군 목록 조회 성공"));
    }
    
    /**
     * 동/읍/면 목록 조회 (추후 구현)
     */
    @GetMapping("/dong")
    @Operation(summary = "동/읍/면 목록 조회", description = "선택한 구/군의 동/읍/면 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDongList(
        @RequestParam String guCode
    ) {
        log.info("Request dong list for gu: {}", guCode);
        
        // TODO: 동 데이터 구현 필요
        // 임시 데이터 (강남구 예시)
        List<Map<String, Object>> dongList = List.of(
            Map.of("code", "1123010100", "name", "역삼동"),
            Map.of("code", "1123010200", "name", "개포동"),
            Map.of("code", "1123010300", "name", "청담동"),
            Map.of("code", "1123010400", "name", "삼성동"),
            Map.of("code", "1123010500", "name", "대치동"),
            Map.of("code", "1123010600", "name", "신사동"),
            Map.of("code", "1123010700", "name", "논현동"),
            Map.of("code", "1123010800", "name", "압구정동"),
            Map.of("code", "1123010900", "name", "세곡동"),
            Map.of("code", "1123011000", "name", "자곡동"),
            Map.of("code", "1123011100", "name", "율현동"),
            Map.of("code", "1123011200", "name", "일원동"),
            Map.of("code", "1123011300", "name", "수서동"),
            Map.of("code", "1123011400", "name", "도곡동")
        );
        
        return ResponseEntity.ok(ApiResponse.success(dongList, "동/읍/면 목록 조회 성공"));
    }
}
