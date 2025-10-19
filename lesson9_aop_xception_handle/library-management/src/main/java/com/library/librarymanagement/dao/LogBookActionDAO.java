package com.library.librarymanagement.dao;

import com.library.librarymanagement.config.DatabaseConfig;
import com.library.librarymanagement.entity.LogBookAction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogBookActionDAO {
    public List<LogBookAction> findAll() throws SQLException {
        List<LogBookAction> logs = new ArrayList<>();
        String sql = "SELECT * FROM log_book_action ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logs.add(mapResultSetToLogBookAction(rs));
            }
        }
        return logs;
    }

    public List<LogBookAction> findByBookId(Integer bookId) throws SQLException {
        List<LogBookAction> logs = new ArrayList<>();
        String sql = "SELECT * FROM log_book_action WHERE book_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToLogBookAction(rs));
                }
            }
        }

        return logs;
    }

    public List<LogBookAction> findByAction(String action) throws SQLException {
        List<LogBookAction> logs = new ArrayList<>();
        String sql = "SELECT * FROM log_book_action WHERE action = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, action);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToLogBookAction(rs));
                }
            }
        }

        return logs;
    }

    public List<LogBookAction> findByDateRange(Timestamp startDate, Timestamp endDate) throws SQLException {
        List<LogBookAction> logs = new ArrayList<>();
        String sql = "SELECT * FROM log_book_action WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToLogBookAction(rs));
                }
            }
        }

        return logs;
    }

    public LogBookAction insert(LogBookAction log) throws SQLException {
        String sql = "INSERT INTO log_book_action (book_id, action, change_amount, before_quantity, " +
                "after_quantity, timestamp, actor) VALUES (?, ?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, log.getBookId());
            stmt.setString(2, log.getAction());
            stmt.setInt(3, log.getChangeAmount());
            stmt.setInt(4, log.getBeforeQuantity());
            stmt.setInt(5, log.getAfterQuantity());
            stmt.setString(6, log.getActor());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        log.setLogId(rs.getLong(1));
                    }
                }
            }
        }

        return log;
    }

    public long countByAction(String action) throws SQLException {
        String sql = "SELECT COUNT(*) FROM log_book_action WHERE action = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, action);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    public long countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM log_book_action";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        return 0;
    }

    public List<LogBookAction> findRecent(int limit) throws SQLException {
        List<LogBookAction> logs = new ArrayList<>();
        String sql = "SELECT * FROM log_book_action ORDER BY timestamp DESC LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToLogBookAction(rs));
                }
            }
        }

        return logs;
    }

    private LogBookAction mapResultSetToLogBookAction(ResultSet rs) throws SQLException {
        LogBookAction log = new LogBookAction();
        log.setLogId(rs.getLong("log_id"));
        log.setBookId(rs.getInt("book_id"));
        log.setAction(rs.getString("action"));
        log.setChangeAmount(rs.getInt("change_amount"));
        log.setBeforeQuantity(rs.getInt("before_quantity"));
        log.setAfterQuantity(rs.getInt("after_quantity"));
        log.setTimestamp(rs.getTimestamp("timestamp"));
        log.setActor(rs.getString("actor"));
        return log;
    }
}
