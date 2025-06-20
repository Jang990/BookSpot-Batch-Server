# BookSpot-Batch-Server


## Batch 처리 과정
![BookSpot-Batch](https://github.com/user-attachments/assets/b37032c1-e75f-4f87-b2ef-2263906e6eee)


### 도서관 동기화 작업
- 도서관 동기화 Step (ChunkSize -  200)
    - BeforeStep: 정보나루에서 도서관 Excel 파일 다운로드
    - Reader: 다운로드된 도서관 Excel 파일 읽기 - `close()` 호출 시 읽은 파일 삭제
    - Writer: DB 저장
- 도서관 상세 정보 파싱 Step (ChunkSize -  200)
    - Reader : [정보나루 도서관 페이지](https://www.data4library.kr/libDataL) 크롤링
    - Writer : 도서관 이름과 주소가 일치하는 도서관의 naru_detail 필드 업데이트

### 도서관 소장 도서 파일 다운로드 작업
1월 기준. 1500개의 파일. 17.5GB의 전체 파일 크기

- 파일 다운로드 Step (ChunkSize - 10)
    - Reader: 도서관 테이블에서 크롤링에 필요한 정보 가져오기
    - Processor: 가장 최근 소장 도서 csv 파일 경로를 크롤링
    - Writer: 도서관 소장 도서 csv 파일 다운로드

### 책 동기화 작업

```
IsbnSet의 내부 구현이 HashSet => LongHashSet으로 변경됨
```

- 책 정보 동기화 Master Step (파티셔닝 - 멀티 스레딩)
    - beforeStep: Book 테이블에서 ISBN13을 읽어와서 `IsbnSet` 초기화
    - 책 정보 동기화 Slave Step (ChunkSize - 1,300)
      - Reader: 도서관 소장 도서 csv 파일 읽기
      - Processor
          - 유효하지 않은 ISBN13 필터링
          - `IsbnSet` 필터링
          - 300자를 초과하는 제목을 `...`으로 생략
          - DB에 저장할 타입으로 변환
      - Writer: Book 테이블에 저장
    - afterStep: `IsbnSet clearAll()` 호출

### 대출 수 동기화 작업

```
Map<Long, AtomicInteger> => long[], AtomicIntegerArray로 변경됨
Map의 map.contains(isbn13)을 Arrays.binarySearch(isbnArray, isbn13)으로 대체됨
```

- 대출 수 읽기 Master Step (파티셔닝 - 멀티스레딩)
    - beforeStep: 집계할 메모리 저장소 초기화
        - DB의 ISBN13을 읽어와서 `long[] isbnArray`에 모두 삽입 후 `Arrays.sort(isbnArray)`로 정렬
        - `AtomicIntegerArray loanArray` 생성
    - 대출 수 읽기 Slave Step - `allowStartIfComplete(true)` (ChunkSize - 1,000)
        - Reader: 도서관 소장 도서 csv 파일 읽기
        - Processor
            - 유효하지 않은 ISBN13 필터링
            - `{ISBN13-대출 수}`객체로 변환
            - `Arrays.binarySearch(isbnArray, isbn13)`으로 ISBN13이 있는 인덱스를 찾을 수 없다면 필터링
        - Writer: 메모리 저장소에 있는 `loanArray` 값 증가
    - afterStep: 메모리 저장소 내용을 파일로 생성 + 집계한 메모리 저장소 청소
      - 메모리 저장소의 내용으로 대출 수 집계 파일 생성
      - `isbnArray`와 `loanArray`에 null로 세팅


- 대출 수 동기화 Step (ChunkSize - 3,000)
    - Reader: 대출 수 종합 파일읽기
    - Writer: Book 테이블에 대출 수 반영

### 도서관 소장 도서 동기화 작업
```
Insert Step에서 사용하는 Set이 HashSet<Long> => LongHashSet으로 변경됨
Delete 파일 생성 Step에서 사용하는 Map을 Map<Long, Boolean> => LongBooleanHahsMap으로 변경됨
```

- 도서관 소장 도서 데이터 정제 MasterStep
  - beforeStep: `Map<ISBN13,BookDbId>` 초기화
  - 도서관 소장 도서 데이터 정제 SlaveStep (ChunkSize - 800)
      - 책제목,발행년도,저자,대출수,... => 책ID,도서관ID
      - Reader: 도서관 소장 도서 csv 파일 읽기 - `close()` 호출 시 읽은 파일 삭제
      - Processor
          - 유효하지 않은 ISBN13 필터링
          - `{bookId, libraryId}` 객체로 변환
      - Writer: `{bookId, libraryId}` 정보가 담긴 csv 파일 생성
  - afterStep: 사용이 끝난 `Map<ISBN13,BookDbId>` 메모리 정리

- 중복 책 필터링 MasterStep
  - 중복된 {책ID, 도서관ID} 제거. `{1,1},{2,1},{1,1} => {1,1},{2,1}`
  - 중복 책 필터링 SlaveStep (ChunkSize - 1,500)
      - `BookIdSet`을 `@StepScope`로 생성
      - Reader: 정제된 도서관 소장 도서 csv 파일 읽기 - `close()` 호출 시 읽은 파일 삭제
      - Processor: `BookIdSet`에 존재하면 필터링. 존재하지 않는다면 `add(BookId)`
      - Writer: 중복을 제거한 도서관 소장 도서 파일 생성

<br>

- Insert Step (파티셔닝 - 멀티스레딩) (ChunkSize - 15_000) 
  - `Library_Stock` 테이블에서 도서관이 가지고 있는 BookId를 가져와서 `@StepScope`로 `BookIdSet` 생성
  - Reader: 정제된 csv 파일 읽기
  - Processor: `BookIdSet`에 존재하는 BookId를 필터링
  - Writer: 필터링되지 않은 데이터를 `Library_Stock` 테이블에 Insert

<br>

- Delete 파일 생성 Step (파티셔닝 - 멀티스레딩)
  - beforeStep: `Library_Stock` 테이블에서 도서관이 가지고 있는 BookId를 가져와서 
    `Map<BookId,Boolean>`에 `{BookId, False}`로 추가 + `@StepScope`로 생성
  - Reader: 정제된 csv 파일 읽기 - `close()` 호출 시 읽은 파일 삭제
  - Writer: 등장한 BookId을 Map에서 찾아서 True로 세팅
  - afterStep: csv파일에 등장하지 않은(False) BookId를 파일로 저장
- Delete Step (파티셔닝 - 멀티스레딩) (ChunkSize - 5,000)
  - Delete 파일을 읽고 사라진 도서관 소장 도서 정보 Delete - `close()` 호출 시 읽은 파일 삭제


### 책 정보 OpenSearch 동기화 작업

- OpenSearch 인덱스 생성 Step
  - (25년 6월 기준) books-2025-06 인덱스 생성
- OpenSearch 동기화 Step (ChunkSize - 300)
  - Reader: 책 테이블 정보 읽기
  - Processor: Document로 변환하기
  - Writer: OpenSearch로 Bulk Insert
- OpenSearch 인덱스 청소 Step
    - (25년 6월 기준) books-2025-06 인덱스에 books Alias 부여.
    - (25년 6월 기준) books-2025-05 인덱스에 books Alias 제거.
    - (25년 6월 기준) books-2025-04 인덱스 제거. 

- 


### 데이터 옵션
- [정보나루 API](https://data4library.kr/apiUtilization) : 일일 30,000건 제한
- [알라딘 API](https://blog.aladin.co.kr/openapi) : 일일 5,000건 - 서비스 URL 필요
- [네이버 책 API](https://developers.naver.com/docs/serviceapi/search/book/book.md) : 일일 25,000건
- [카카오 책 API](https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-book) : 일일 총 5만건, API 별 3만건
- [정보나루 최신 소장 도서 CSV 파일](https://data4library.kr/openDataL) : 크롤링 방식

csv 파일 선택
