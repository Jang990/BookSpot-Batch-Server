package com.bookspot.batch;

import org.springframework.jdbc.core.JdbcTemplate;

public class TestInsertUtils {

    public static class LibraryBuilder {
        private long id = 1L;
        private String name = "Sample 도서관";
        private String libraryCode = "SampleCode";
        private double latitude = 0d;
        private double longitude = 0d;

        public LibraryBuilder id(long id) {this.id = id; return this;}
        public LibraryBuilder name(String name) {this.name = name; return this;}
        public LibraryBuilder libraryCode(String libraryCode) {this.libraryCode = libraryCode; return this;}
        public LibraryBuilder latitude(double latitude) {this.latitude = latitude; return this;}
        public LibraryBuilder longitude(double longitude) {this.longitude = longitude; return this;}

        public void insert(JdbcTemplate jdbcTemplate) {
            jdbcTemplate.update("""
                INSERT INTO bookspot_test.library
                (id, name, library_code, location)
                VALUES(?, ?, ?, ST_GeomFromText(CONCAT('POINT(', ?, ' ', ?, ')'), 4326));
                """, ps -> {
                        ps.setLong(1, id);
                        ps.setString(2, name);
                        ps.setString(3, libraryCode);
                        ps.setDouble(4, latitude);
                        ps.setDouble(5, longitude);
                    }
            );
        }
    }

    public static LibraryBuilder libraryBuilder() {
        return new LibraryBuilder();
    }
}
