package com.realestate.tracker.domain.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 지역(시/광역시/특별시) 타입 enum
 *
 * @author Generated from toy-real-estate-backend
 */
@Slf4j
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LawdSiType implements LawdType {
    SEOUL("11", "서울특별시"),
    BUSAN("26", "부산광역시"),
    DAEGU("27", "대구광역시"),
    INCHEON("28", "인천광역시"),
    GWANGJU("29", "광주광역시"),
    DAEJEON("30", "대전광역시"),
    ULSAN("31", "울산광역시"),
    SEJONG("36", "세종특별자치시"),
    GYEONGGI("41", "경기도"),
    GANGWON("42", "강원도"),
    CHUNGBUK("43", "충청북도"),
    CHUNGNAM("44", "충청남도"),
    JEONBUK("45", "전라북도"),
    JEONNAM("46", "전라남도"),
    GYEONGBUK("47", "경상북도"),
    GYEONGNAM("48", "경상남도"),
    JEJU("50", "제주특별자치도");

    // 지역코드(시)
    private final String code;
    // 지역명
    private final String name;

    LawdSiType(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    private static final Map<String, LawdSiType> codeToEnum = new HashMap<>();

    static {
        Arrays.stream(LawdSiType.values()).forEach(lawdSiType -> 
            codeToEnum.put(lawdSiType.getCode(), lawdSiType));
    }

    /**
     * 지역코드(시) 기준 지역 상세 타입을 반환한다.
     *
     * @param code 지역코드(시)
     * @return 지역 상세 타입
     */
    @JsonCreator
    public static LawdSiType codeOf(@JsonProperty("code") final String code) {
        LawdSiType lawdSiType = codeToEnum.get(code);

        if (lawdSiType == null) {
            log.warn("Unsupported lawd si type code: {}", code);
        }

        return lawdSiType;
    }
}
