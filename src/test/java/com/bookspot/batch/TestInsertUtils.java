package com.bookspot.batch;

import org.springframework.jdbc.core.JdbcTemplate;

public class TestInsertUtils {

    public static class LibraryBuilder {
        private Long id = null;
        private String name = "Sample 도서관";
        private String libraryCode = null;
        private double latitude = 0d;
        private double longitude = 0d;

        public LibraryBuilder id(long id) {this.id = id; return this;}
        public LibraryBuilder name(String name) {this.name = name; return this;}
        public LibraryBuilder libraryCode(String libraryCode) {this.libraryCode = libraryCode; return this;}
        public LibraryBuilder latitude(double latitude) {this.latitude = latitude; return this;}
        public LibraryBuilder longitude(double longitude) {this.longitude = longitude; return this;}

        public void insert(JdbcTemplate jdbcTemplate) {
            if(id == null)
                throw new IllegalArgumentException("도서관 Insert 시 ID는 필수");

            jdbcTemplate.update("""
                INSERT INTO bookspot_test.library
                (id, name, library_code, location)
                VALUES(?, ?, ?, ST_GeomFromText(CONCAT('POINT(', ?, ' ', ?, ')'), 4326));
                """, ps -> {
                        ps.setLong(1, id);
                        ps.setString(2, name);
                        ps.setString(3, libraryCode == null ? String.valueOf(id) : libraryCode);
                        ps.setDouble(4, latitude);
                        ps.setDouble(5, longitude);
                    }
            );
        }
    }

    public static class BookBuilder {
        private static final String WITHOUT_ID_QUERY ="""
                INSERT INTO bookspot_test.book
                (isbn13, title, loan_count)
                VALUES(?, ?, ?);
                """;

        private static final String WITH_ID_QUERY ="""
                INSERT INTO bookspot_test.book
                (id, isbn13, title, loan_count)
                VALUES(?, ?, ?, ?);
                """;

        private Long id = null;
        private String isbn13 = null;
        private String title = "Sample Title";
        private int loanCount = 0;

        public BookBuilder id(long id) {
            this.id = id;
            return this;
        }

        public BookBuilder isbn13(String isbn13) {
            this.isbn13 = isbn13;
            return this;
        }

        public BookBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BookBuilder loanCount(int loanCount) {
            this.loanCount = loanCount;
            return this;
        }

        public void insert(JdbcTemplate jdbcTemplate) {
            if (isbn13 == null)
                throw new IllegalArgumentException("책 Insert 시 ISBN13은 필수 설정");

            if (id == null) {
                jdbcTemplate.update(WITHOUT_ID_QUERY, ps -> {
                            ps.setString(1, isbn13);
                            ps.setString(2, title);
                            ps.setInt(3, loanCount);
                        }
                );
                return;
            }

            jdbcTemplate.update(WITH_ID_QUERY, ps -> {
                        ps.setLong(1, id);
                        ps.setString(2, isbn13);
                        ps.setString(3, title);
                        ps.setInt(4, loanCount);
                    }
            );
        }
    }

    public static LibraryBuilder libraryBuilder() {
        return new LibraryBuilder();
    }
    public static BookBuilder bookBuilder() {
        return new BookBuilder();
    }

}
