# 공공데이터포털 Open API 설정 가이드

## 📋 API 정보

**사용 API:** 국토교통부_아파트매매 실거래 상세 자료 조회
**제공처:** 공공데이터포털 (https://www.data.go.kr)
**URL:** http://openapi.molit.go.kr/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTradeDev

## 🔑 API 키 발급 방법

### 1. 공공데이터포털 회원가입
1. https://www.data.go.kr 접속
2. 회원가입 (소셜 로그인 가능)

### 2. API 활용 신청
1. 로그인 후 '실거래가' 검색
2. **국토교통부_아파트매매 실거래 상세 자료** 선택
3. **활용신청** 버튼 클릭
4. 다음 정보 입력:
   - 시스템 유형: 일반
   - 활용 목적: 웹 사이트 개발
   - 기능 설명: 부동산 실거래가 검색 서비스
5. **개발계정 신청** (자동승인)

### 3. 운영계정으로 업그레이드 (권장)
개발계정은 일일 트래픽 1,000건으로 제한됩니다.
운영계정은 일일 트래픽 100만건을 제공합니다.

1. 마이페이지 → 오픈API → 개발계정
2. **운영계정 신청** 버튼 클릭
3. 활용사례 작성 후 신청

### 4. 인증키 확인
1. 마이페이지 → 오픈API
2. 승인된 API 클릭
3. **일반 인증키(Encoding)** 또는 **일반 인증키(Decoding)** 확인
   - **Encoding 키 사용 권장** (URL에 바로 사용 가능)

## ⚙️ 프로젝트 설정

### 방법 1: application.yml 직접 설정

`src/main/resources/application.yml` 파일에 API 키를 직접 입력:

```yaml
openapi:
  key: "발급받은_인증키_여기에_붙여넣기"  # Encoding 키 사용
  apt-trade:
    url: http://openapi.molit.go.kr/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTradeDev
```

### 방법 2: 환경변수 사용 (권장)

**Windows:**
```bash
set OPENAPI_KEY=발급받은_인증키
```

**Linux/Mac:**
```bash
export OPENAPI_KEY=발급받은_인증키
```

**IntelliJ IDEA Run Configuration:**
1. Run → Edit Configurations
2. Environment variables 항목에 추가:
   ```
   OPENAPI_KEY=발급받은_인증키
   ```

## 📊 API 스펙

### 요청 파라미터

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| serviceKey | String | 필수 | 인증키 | - |
| LAWD_CD | String | 필수 | 지역코드 (5자리) | 11110 (서울 종로구) |
| DEAL_YMD | String | 필수 | 계약년월 (6자리) | 202501 (2025년 1월) |
| pageNo | Integer | 선택 | 페이지 번호 | 1 |
| numOfRows | Integer | 선택 | 페이지당 결과 수 | 9999 |

### 주요 지역 코드

| 지역 | 코드 |
|------|------|
| 서울 종로구 | 11110 |
| 서울 강남구 | 11680 |
| 경기 성남시 분당구 | 41135 |
| 경기 수원시 장안구 | 41111 |
| 부산 해운대구 | 26350 |

전체 법정동 코드는 [행정표준코드관리시스템](https://www.code.go.kr)에서 확인 가능

### 응답 데이터 (XML)

```xml
<response>
  <header>
    <resultCode>00</resultCode>
    <resultMsg>NORMAL SERVICE.</resultMsg>
  </header>
  <body>
    <items>
      <item>
        <거래금액>82,500</거래금액>
        <건축년도>2008</건축년도>
        <년>2025</년>
        <월>1</월>
        <일>15</일>
        <아파트>래미안</아파트>
        <전용면적>84.9</전용면적>
        <지번>100</지번>
        <층>10</층>
        <법정동>청담동</법정동>
        <해제여부></해제여부>
      </item>
    </items>
  </body>
</response>
```

## 🧪 테스트

### 1. Swagger UI로 테스트
```
http://localhost:8080/swagger-ui.html
```

### 2. API 직접 호출
```bash
curl "http://localhost:8080/api/v1/apt-trades?lawdCode=11110&startYearMonth=2025-01"
```

### 3. 프론트엔드에서 테스트
```
http://localhost:3000
```

## ⚠️ 주의사항

1. **API 키는 절대 Git에 커밋하지 마세요!**
   - `.gitignore`에 `application.yml` 또는 환경변수 파일 추가

2. **트래픽 제한**
   - 개발계정: 1,000건/일
   - 운영계정: 1,000,000건/일
   - 초과 시 API 호출 불가

3. **데이터 제공 범위**
   - 2006년 1월부터 제공
   - 월 단위로만 조회 가능
   - 개인정보 보호를 위해 일부 정보 제한

4. **응답 시간**
   - 첫 조회 시 시간이 다소 소요될 수 있음
   - DB에 캐싱되므로 두 번째 조회부터는 빠름

## 🔧 문제 해결

### "인증키가 유효하지 않습니다"
- 인증키 확인 (Encoding 키 사용)
- 승인 대기 중인지 확인 (보통 즉시 승인)

### "일일 트래픽 초과"
- 운영계정으로 업그레이드
- 또는 24시간 후 재시도

### "해당 데이터가 없습니다"
- 지역코드 5자리 확인
- 계약년월 형식 확인 (YYYYMM)
- 해당 지역/기간에 거래가 없을 수 있음

## 📚 참고 자료

- [공공데이터포털](https://www.data.go.kr)
- [API 상세 페이지](https://www.data.go.kr/data/15057511/openapi.do)
- [행정표준코드 조회](https://www.code.go.kr)
- [국토교통부 실거래가공개시스템](http://rt.molit.go.kr)
