package com.library.librarymanagement.dao;

import com.library.librarymanagement.config.DatabaseConfig;
import com.library.librarymanagement.entity.LogBookAction;
import com.library.librarymanagement.entity.VisitLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitLogDAO {
    public List<VisitLog> findAll() throws SQLException {
        List<VisitLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM visit_log ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                logs.add(mapResultSetToVisitLog(rs));
            }
        }

        return logs;
    }

    public List<VisitLog> findByAction(String action) throws SQLException {
        List<VisitLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM visit_log WHERE action = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, action);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToVisitLog(rs));
                }
            }
        }

        return logs;
    }

    public List<VisitLog> findByIpAddress(String ipAddress) throws SQLException {
        List<VisitLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM visit_log WHERE ip_address = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ipAddress);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToVisitLog(rs));
                }
            }
        }

        return logs;
    }

    public List<VisitLog> findByDateRange(Timestamp startDate, Timestamp endDate) throws SQLException {
        List<VisitLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM visit_log WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToVisitLog(rs));
                }
            }
        }

        return logs;
    }

    public VisitLog insert(VisitLog log) throws SQLException {
        String sql = "INSERT INTO visit_log (action, ip_address, timestamp, details) " +
                "VALUES (?, ?, NOW(), ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, log.getAction());
            stmt.setString(2, log.getIpAddress());
            stmt.setString(3, log.getDetails());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        log.setVisitId(rs.getLong(1));
                    }
                }
            }
        }

        return log;
    }

    public long countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM visit_log";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        return 0;
    }

    public long countByAction(String action) throws SQLException {
        String sql = "SELECT COUNT(*) FROM visit_log WHERE action = ?";

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

    public long countByIpAddress(String ipAddress) throws SQLException {
        String sql = "SELECT COUNT(*) FROM visit_log WHERE ip_address = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ipAddress);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        return 0;
    }

    public long countTodayVisits() throws SQLException {
        String sql = "SELECT COUNT(*) FROM visit_log WHERE DATE(timestamp) = CURDATE()";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        return 0;
    }

    public List<VisitLog> findRecent(int limit) throws SQLException {
        List<VisitLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM visit_log ORDER BY timestamp DESC LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToVisitLog(rs));
                }
            }
        }

        return logs;
    }

    public boolean deleteOldLogs(int daysToKeep) throws SQLException {
        String sql = "DELETE FROM visit_log WHERE timestamp < DATE_SUB(NOW(), INTERVAL ? DAY)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, daysToKeep);
            return stmt.executeUpdate() > 0;
        }
    }

    private VisitLog mapResultSetToVisitLog(ResultSet rs) throws SQLException {
        VisitLog log = new VisitLog();
        log.setVisitId(rs.getLong("visit_id"));
        log.setAction(rs.getString("action"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setTimestamp(rs.getTimestamp("timestamp"));
        log.setDetails(rs.getString("details"));
        return log;
    }
}
