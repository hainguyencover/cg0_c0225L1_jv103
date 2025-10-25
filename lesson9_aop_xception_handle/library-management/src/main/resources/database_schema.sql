-- Create database
CREATE
    DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE
    library_db;

# -- Drop tables if exist (for clean installation)
# DROP TABLE IF EXISTS visit_log;
# DROP TABLE IF EXISTS log_book_action;
# DROP TABLE IF EXISTS borrow_record;
# DROP TABLE IF EXISTS book;

-- Table: book
CREATE TABLE book
(
    book_id            INT AUTO_INCREMENT PRIMARY KEY,
    title              VARCHAR(255) NOT NULL,
    author             VARCHAR(255) NOT NULL,
    category           VARCHAR(100),
    total_quantity     INT          NOT NULL DEFAULT 0,
    available_quantity INT          NOT NULL DEFAULT 0,
    created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_category (category),
    INDEX idx_author (author),
    CHECK (total_quantity >= 0),
    CHECK (available_quantity >= 0),
    CHECK (available_quantity <= total_quantity)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: borrow_record
CREATE TABLE borrow_record
(
    borrow_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    borrow_code   CHAR(5)                       NOT NULL UNIQUE,
    book_id       INT                           NOT NULL,
    borrower_name VARCHAR(255)                  NOT NULL,
    borrow_date   DATETIME                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    return_date   DATETIME                      NULL,
    status        ENUM ('BORROWED', 'RETURNED') NOT NULL DEFAULT 'BORROWED',
    FOREIGN KEY (book_id) REFERENCES book (book_id) ON DELETE RESTRICT,
    INDEX idx_borrow_code (borrow_code),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date),
    INDEX idx_borrower_name (borrower_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: log_book_action
CREATE TABLE log_book_action
(
    log_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id         INT                                                     NOT NULL,
    action          ENUM ('BORROW', 'RETURN', 'CREATE', 'UPDATE', 'DELETE') NOT NULL,
    change_amount   INT                                                     NOT NULL DEFAULT 0,
    before_quantity INT                                                     NOT NULL,
    after_quantity  INT                                                     NOT NULL,
    timestamp       DATETIME                                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actor           VARCHAR(255),
    FOREIGN KEY (book_id) REFERENCES book (book_id) ON DELETE CASCADE,
    INDEX idx_book_id (book_id),
    INDEX idx_action (action),
    INDEX idx_timestamp (timestamp)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: visit_log
CREATE TABLE visit_log
(
    visit_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    action     VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45),
    timestamp  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details    TEXT,
    INDEX idx_action (action),
    INDEX idx_timestamp (timestamp),
    INDEX idx_ip_address (ip_address)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO book (title, author, category, total_quantity, available_quantity)
VALUES ('Clean Code', 'Robert C. Martin', 'Programming', 5, 5),
       ('Design Patterns', 'Gang of Four', 'Programming', 3, 3),
       ('The Pragmatic Programmer', 'Andy Hunt, Dave Thomas', 'Programming', 4, 4),
       ('Introduction to Algorithms', 'Thomas H. Cormen', 'Computer Science', 6, 6),
       ('Artificial Intelligence: A Modern Approach', 'Stuart Russell, Peter Norvig', 'AI', 3, 3),
       ('Head First Java', 'Kathy Sierra, Bert Bates', 'Programming', 7, 7),
       ('Effective Java', 'Joshua Bloch', 'Programming', 5, 5),
       ('Spring in Action', 'Craig Walls', 'Programming', 4, 4),
       ('Database System Concepts', 'Abraham Silberschatz', 'Database', 5, 5),
       ('Computer Networks', 'Andrew S. Tanenbaum', 'Networking', 4, 4);

-- Verify data
SELECT 'Books created:' AS info, COUNT(*) AS count
FROM book;
SELECT *
FROM book;
