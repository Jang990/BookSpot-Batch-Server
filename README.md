# BookSpot-Batch-Server


## Batch 처리 과정
![BookSpot-Batch](https://github.com/user-attachments/assets/b37032c1-e75f-4f87-b2ef-2263906e6eee)


### 도서관 동기화 작업
- beforeJob : 정보나루에서 도서관 Excel 파일 다운로드
- 도서관 동기화 Step
    - 다운로드된 도서관 Excel 파일 정보를 DB에 저장
- 도서관 상세 정보 파싱 Step
    - Reader : [정보나루 도서관 페이지](https://www.data4library.kr/libDataL) 크롤링
    - Writer : 도서관 이름과 주소가 일치하는 도서관의 naru_detail 필드 업데이트
- afterJob : 다운로드 받은 도서관 Excel 파일 제거

### 도서관 소장 도서 파일 다운로드 작업
1월 기준. 1500개의 파일. 17.5GB의 전체 파일 크기

- 파일 다운로드 Step
    - Reader: 도서관 테이블에서 크롤링에 필요한 정보 가져오기
    - Processor: 가장 최근 소장 도서 csv 파일 경로를 크롤링
    - Writer: 도서관 소장 도서 csv 파일 다운로드

### 책 동기화 작업

```
IsbnSet의 내부 구현이 HashSet => LongHashSet으로 변경됨
```

- beforeJob: Book 테이블에서 ISBN13을 읽어와서 `IsbnSet` 초기화
- 책 정보 동기화 Step (파티셔닝 - 멀티 스레딩)
    - Reader: 도서관 소장 도서 csv 파일 읽기
    - Processor
        - 유효하지 않은 ISBN13 필터링
        - `IsbnSet` 필터링
        - 300자를 초과하는 제목을 `...`으로 생략
        - DB에 저장할 타입으로 변환
    - Writer: Book 테이블에 저장
- afterJob: `IsbnSet` `clearAll()`

### 대출 수 동기화 작업

```
Map<Long, AtomicInteger> => long[], AtomicIntegerArray로 변경됨
Map의 map.contains(isbn13)을 Arrays.binarySearch(isbnArray, isbn13)으로 대체됨

효율적인 재시도를 위한 Flow와 JobExecutionDecider 도입 
```

- 집계 성공 Decider
  - `EXECUTE_ALL_STATUS` 반환
    - 이전 작업 시도가 없음.
    - 집계 파일이 성공적으로 생성된 적 없음.
  - `SKIP_AGGREGATION_STATUS` 반환
    - 이전 작업에서 집계 파일을 성공적으로 생성함.


- `EXECUTE_ALL_STATUS` 일 때
  - 집계할 메모리 저장소 초기화 Step
    - DB의 ISBN13을 읽어와서 `long[] isbnArray`에 모두 삽입 후 `Arrays.sort(isbnArray)`로 정렬
    - `AtomicIntegerArray loanArray` 생성
  - 대출 수 읽기 Step (파티셔닝 - 멀티스레딩)
      - Reader: 도서관 소장 도서 csv 파일 읽기
      - Processor
          - 유효하지 않은 ISBN13 필터링
          - `{ISBN13-대출 수}`객체로 변환
          - `Arrays.binarySearch(isbnArray, isbn13)`으로 ISBN13이 있는 인덱스를 찾을 수 없다면 필터링
      - Writer: 메모리 저장소에 있는 `loanArray` 값 증가
  - 대출 수 파일 쓰기 Step
      - 메모리 저장소의 요소들을 읽으며 대출 수 종합 파일 생성
  - 집계할 메모리 저장소 청소 Step
      - `isbnArray`와 `loanArray`에 null로 세팅
  - 대출 수 동기화 Step
      - Reader: 대출 수 종합 파일읽기
      - Writer: Book 테이블에 대출 수 반영


- `SKIP_AGGREGATION_STATUS` 일 때
    - 대출 수 동기화 Step


### 도서관 소장 도서 동기화 작업
```
Insert Step에서 사용하는 Set이 HashSet<Long> => LongHashSet으로 변경됨
Delete 파일 생성 Step에서 사용하는 Map을 Map<Long, Boolean> => LongBooleanHahsMap으로 변경됨
```

- 도서관 소장 도서 데이터 정제 Job
    - Map 초기화
- 소장 도서 동기화 Job (파티셔닝 - 멀티 스레딩)
    - Insert Step
        - BeforeStep: LibraryStock 테이블에서 도서관이 가지고 있는 BookId를 `Set`에 추가
        - Reader: 정제된 csv 파일 읽기
        - Processor: `Set`에 존재하는 BookId를 필터링
        - Writer: 도서관 재고 테이블에 Insert
    - Delete 파일 생성 Step
        - BeforeStep: LibraryStock 테이블에서 도서관이 가지고 있는 BookId를 `Map`에 `{BookId, False}`로 세팅
        - Reader: 정제된 csv 파일 읽기
        - Writer: 등장한 BookId을 Map에서 찾아서 True로 세팅
        - AfterStep: 등장하지 않은(False) BookId를 파일로 저장
    - Delete Step
        - Delete 파일을 읽고 사라진 도서관 소장 도서 정보 Delete

### 데이터 옵션
- [정보나루 API](https://data4library.kr/apiUtilization) : 일일 30,000건 제한
- [알라딘 API](https://blog.aladin.co.kr/openapi) : 일일 5,000건 - 서비스 URL 필요
- [네이버 책 API](https://developers.naver.com/docs/serviceapi/search/book/book.md) : 일일 25,000건
- [카카오 책 API](https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-book) : 일일 총 5만건, API 별 3만건
- [정보나루 최신 소장 도서 CSV 파일](https://data4library.kr/openDataL) : 크롤링 방식

csv 파일 선택
