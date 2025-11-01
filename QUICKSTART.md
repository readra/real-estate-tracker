# 🚀 빠른 시작 가이드

## 1. 백엔드 실행

### IntelliJ IDEA에서 실행
1. IntelliJ에서 프로젝트 열기
2. `RealEstateTrackerApplication.java` 찾기
3. 메인 메서드 옆의 실행 버튼 클릭
4. 또는 `Shift + F10` 단축키 사용

### 터미널에서 실행
```bash
./gradlew bootRun
```

서버가 `http://localhost:8080`에서 실행됩니다.

### 확인
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (비워두기)

- Swagger UI: http://localhost:8080/swagger-ui.html

## 2. 프론트엔드 실행

```bash
cd frontend
npm install
npm start
```

브라우저가 자동으로 `http://localhost:3000`을 엽니다.

## 3. 기능 테스트

### 아파트 실거래 검색

1. **지역 코드 입력**
   - 서울 종로구: `11110`
   - 서울 강남구: `11680`
   - 경기 성남시 분당구: `41135`

2. **기간 선택** (선택사항)
   - 시작 년월: `2024-01`
   - 종료 년월: `2024-12`

3. **거래 금액 범위** (선택사항)
   - 최소 금액: `50000` (5억)
   - 최대 금액: `100000` (10억)

4. **검색 버튼 클릭**

### 주의사항
- 첫 검색 시 Open API에서 데이터를 가져오므로 시간이 걸릴 수 있습니다
- Open API 키가 설정되지 않은 경우 더미 데이터로 테스트됩니다
- 메모리 DB를 사용하므로 서버 재시작 시 데이터가 초기화됩니다

## 4. Open API 키 설정 (선택사항)

실제 데이터를 받으려면 환경변수로 API 키를 설정하세요:

```bash
# Windows
set OPENAPI_KEY=your_api_key_here

# Linux/Mac
export OPENAPI_KEY=your_api_key_here
```

또는 `application.yml`에 직접 설정:
```yaml
openapi:
  key: your_api_key_here
```

## 5. 개발 팁

### 백엔드 핫 리로드
- Spring Boot DevTools가 활성화되어 있어 코드 변경 시 자동으로 재시작됩니다

### 프론트엔드 핫 리로드
- 코드 저장 시 자동으로 브라우저가 새로고침됩니다

### API 테스트
- Swagger UI를 사용하여 API를 직접 테스트할 수 있습니다
- http://localhost:8080/swagger-ui.html

## 6. 문제 해결

### 포트 충돌
- 백엔드: `application.yml`에서 `server.port` 변경
- 프론트엔드: `package.json`의 start 스크립트에 `PORT=3001` 추가

### CORS 에러
- `WebConfig.java`에서 허용된 origin 확인

### 데이터가 없을 때
- 직접 API 호출: `/api/v1/apt-trades/sync` 엔드포인트 사용
