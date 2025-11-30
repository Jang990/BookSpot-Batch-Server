-- seq 테이블들의 유니크 키 이름을 식별할 수 있게 바뀜
-- point location not null -> point varchar(1) 변경됨 | Point 형식이 없기 때문에 도서관의 location필드는 항상 null로 조회됨

DROP TABLE IF EXISTS book_codes;
DROP TABLE IF EXISTS library_stock;
DROP TABLE IF EXISTS library;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS book_code;


-- bookspot.book_codes definition
CREATE TABLE `book_codes` (
  `id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `parent_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `book_codes_FK` (`parent_id`),
  CONSTRAINT `book_codes_FK` FOREIGN KEY (`parent_id`) REFERENCES `book_codes` (`id`)
) ENGINE=InnoDB;

-- bookspot.library definition
CREATE TABLE `library` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `library_code` varchar(100) NOT NULL,
  `location` varchar(1),
  `name` varchar(255) NOT NULL,
  `updated_at` date DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `naru_detail` varchar(100) DEFAULT NULL,
  `stock_updated_at` date DEFAULT NULL,
  `closed_info` varchar(255) DEFAULT NULL,
  `contact_number` varchar(255) DEFAULT NULL,
  `home_page` varchar(255) DEFAULT NULL,
  `operating_info` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `library_un` (`library_code`)
) ENGINE=InnoDB;

-- bookspot.book definition
CREATE TABLE `book` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `isbn13` varchar(13) NOT NULL,
  `title` varchar(300) DEFAULT NULL,
  `subject_code` int DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `publication_year` year DEFAULT NULL,
  `publisher` varchar(255) DEFAULT NULL,
  `loan_count` int NOT NULL DEFAULT '0',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `monthly_loan_increase` INT UNSIGNED DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKdjx0bsw5qtlpa3ertiyf8j0bc` (`isbn13`)
) ENGINE=InnoDB;

-- bookspot.library_stock definition
CREATE TABLE `library_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NOT NULL,
  `library_id` bigint NOT NULL,
  `created_at` date DEFAULT NULL,
  `updated_at_time` datetime DEFAULT NULL,
  `subject_code` VARCHAR(40) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `library_stock_un` (`book_id`,`library_id`),
  KEY `library_stock_FK_1` (`library_id`),
  CONSTRAINT `library_stock_FK` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
  CONSTRAINT `library_stock_FK_1` FOREIGN KEY (`library_id`) REFERENCES `library` (`id`)
) ENGINE=InnoDB;

-- bookspot_sample.book_code definition
CREATE TABLE `book_code` (
  `id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;