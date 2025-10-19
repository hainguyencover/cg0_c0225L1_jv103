package com.library.librarymanagement.dao;

import com.library.librarymanagement.config.DatabaseConfig;
import com.library.librarymanagement.entity.BorrowRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BorrowRecordDAO {
    public List<BorrowRecord> findAll() throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.*, b.title as book_title " +
                "FROM borrow_record br " +
                "JOIN book b ON br.book_id = b.book_id " +
                "ORDER BY br.borrow_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                records.add(mapResultSetToBorrowRecord(rs));
            }
        }

        return records;
    }

    public BorrowRecord findById(Long borrowId) throws SQLException {
        String sql = "SELECT br.*, b.title as book_title " +
                "FROM borrow_record br " +
                "JOIN book b ON br.book_id = b.book_id " +
                "WHERE br.borrow_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, borrowId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBorrowRecord(rs);
                }
            }
        }

        return null;
    }

    public BorrowRecord findByBorrowCode(String borrowCode) throws SQLException {
        String sql = "SELECT br.*, b.title as book_title " +
                "FROM borrow_record br " +
                "JOIN book b ON br.book_id = b.book_id " +
                "WHERE br.borrow_code = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, borrowCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBorrowRecord(rs);
                }
            }
        }

        return null;
    }

    public List<BorrowRecord> findByStatus(String status) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.*, b.title as book_title " +
                "FROM borrow_record br " +
                "JOIN book b ON br.book_id = b.book_id " +
                "WHERE br.status = ? " +
                "ORDER BY br.borrow_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToBorrowRecord(rs));
                }
            }
        }

        return records;
    }

    public List<BorrowRecord> findByBookId(Integer bookId) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.*, b.title as book_title " +
                "FROM borrow_record br " +
                "JOIN book b ON br.book_id = b.book_id " +
                "WHERE br.book_id = ? " +
                "ORDER BY br.borrow_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToBorrowRecord(rs));
                }
            }
        }

        return records;
    }

    public List<BorrowRecord> findByBorrowerName(String borrowerName) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.*, b.title as book_title " +
                "FROM borrow_record br " +
                "JOIN book b ON br.book_id = b.book_id " +
                "WHERE br.borrower_name LIKE ? " +
                "ORDER BY br.borrow_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + borrowerName + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToBorrowRecord(rs));
                }
            }
        }

        return records;
    }

    public BorrowRecord insert(BorrowRecord record) throws SQLException {
        String sql = "INSERT INTO borrow_record (borrow_code, book_id, borrower_name, borrow_date, status) " +
                "VALUES (?, ?, ?, NOW(), ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, record.getBorrowCode());
            stmt.setInt(2, record.getBookId());
            stmt.setString(3, record.getBorrowName());
            stmt.setString(4, record.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        record.setBorrowId(rs.getLong(1));
                    }
                }
            }
        }

        return record;
    }

    public boolean updateStatusToReturned(String borrowCode) throws SQLException {
        String sql = "UPDATE borrow_record SET status = 'RETURNED', return_date = NOW() " +
                "WHERE borrow_code = ? AND status = 'BORROWED'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, borrowCode);
            return stmt.executeUpdate() > 0;
        }
    }

    public String generateUniqueBorrowCode() throws SQLException {
        Random random = new Random();
        String borrowCode;
        int attempts = 0;
        int maxAttempts = 100;

        do {
            borrowCode = String.format("%05d", random.nextInt(100000));
            attempts++;

            if (attempts >= maxAttempts) {
                throw new SQLException("Unable to generate unique borrow code after " + maxAttempts + " attempts");
            }
        } while (isBorrowCodeExists(borrowCode));

        return borrowCode;
    }

    public boolean isBorrowCodeExists(String borrowCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM borrow_record WHERE borrow_code = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, borrowCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public long countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM borrow_record WHERE status = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    public long countTotalBorrows() throws SQLException {
        String sql = "SELECT COUNT(*) FROM borrow_record";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        return 0;
    }

    private BorrowRecord mapResultSetToBorrowRecord(ResultSet rs) throws SQLException {
        BorrowRecord record = new BorrowRecord();
        record.setBorrowId(rs.getLong("borrow_id"));
        record.setBorrowCode(rs.getString("borrow_code"));
        record.setBookId(rs.getInt("book_id"));
        record.setBorrowName(rs.getString("borrow_name"));
        record.setBorrowDate(rs.getTimestamp("borrow_date"));
        record.setReturnDate(rs.getTimestamp("return_date"));
        record.setStatus(rs.getString("status"));
        record.setBookTitle(rs.getString("book_title"));
        return record;
    }
}
