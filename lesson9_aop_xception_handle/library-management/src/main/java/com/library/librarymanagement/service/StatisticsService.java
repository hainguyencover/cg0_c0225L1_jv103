package com.library.librarymanagement.service;

import com.library.librarymanagement.dao.BookDAO;
import com.library.librarymanagement.dao.BorrowRecordDAO;
import com.library.librarymanagement.dao.LogBookActionDAO;
import com.library.librarymanagement.dao.VisitLogDAO;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.entity.LogBookAction;
import com.library.librarymanagement.entity.VisitLog;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsService {
    private BookDAO bookDAO;
    private BorrowRecordDAO borrowRecordDAO;
    private LogBookActionDAO logBookActionDAO;
    private VisitLogDAO visitLogDAO;

    public StatisticsService() {
        this.bookDAO = new BookDAO();
        this.borrowRecordDAO = new BorrowRecordDAO();
        this.logBookActionDAO = new LogBookActionDAO();
        this.visitLogDAO = new VisitLogDAO();
    }

    /**
     * Thống kê tổng quan hệ thống
     */
    public Map<String, Object> getSystemOverview() throws SQLException {
        Map<String, Object> stats = new HashMap<>();

        // Thống kê sách
        List<Book> allBooks = bookDAO.findAll();
        stats.put("totalBookTitles", allBooks.size());
        stats.put("totalBookCopies", allBooks.stream()
                .mapToInt(Book::getTotalQuantity).sum());
        stats.put("availableBooks", allBooks.stream()
                .mapToInt(Book::getAvailableQuantity).sum());
        stats.put("borrowedBooks", allBooks.stream()
                .mapToInt(b -> b.getTotalQuantity() - b.getAvailableQuantity()).sum());

        // Thống kê mượn trả
        stats.put("totalBorrows", borrowRecordDAO.countTotalBorrows());
        stats.put("currentBorrows", borrowRecordDAO.countByStatus("BORROWED"));
        stats.put("totalReturns", borrowRecordDAO.countByStatus("RETURNED"));

        // Thống kê visit
        stats.put("totalVisits", visitLogDAO.countTotal());
        stats.put("todayVisits", visitLogDAO.countTodayVisits());

        // Thống kê log actions
        stats.put("totalActions", logBookActionDAO.countTotal());
        stats.put("borrowActions", logBookActionDAO.countByAction("BORROW"));
        stats.put("returnActions", logBookActionDAO.countByAction("RETURN"));

        return stats;
    }

    /**
     * Top sách được mượn nhiều nhất
     */
    public List<Map<String, Object>> getMostBorrowedBooks(int limit) throws SQLException {
        List<BorrowRecord> allRecords = borrowRecordDAO.findAll();

        // Đếm số lần mượn theo book_id
        Map<Integer, Long> borrowCounts = allRecords.stream()
                .collect(Collectors.groupingBy(
                        BorrowRecord::getBookId,
                        Collectors.counting()
                ));

        // Lấy thông tin sách và sắp xếp
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : borrowCounts.entrySet()) {
            Book book = bookDAO.findById(entry.getKey());
            if (book != null) {
                Map<String, Object> bookStats = new HashMap<>();
                bookStats.put("bookId", book.getBookId());
                bookStats.put("title", book.getTitle());
                bookStats.put("author", book.getAuthor());
                bookStats.put("category", book.getCategory());
                bookStats.put("borrowCount", entry.getValue());
                bookStats.put("availableQuantity", book.getAvailableQuantity());
                result.add(bookStats);
            }
        }

        // Sort theo borrowCount giảm dần
        result.sort((a, b) -> Long.compare(
                (Long) b.get("borrowCount"),
                (Long) a.get("borrowCount")
        ));

        return result.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Thống kê theo thể loại
     */
    public List<Map<String, Object>> getStatsByCategory() throws SQLException {
        List<String> categories = bookDAO.getAllCategories();
        List<Map<String, Object>> categoryStats = new ArrayList<>();

        for (String category : categories) {
            List<Book> booksInCategory = bookDAO.findByCategory(category);

            Map<String, Object> stats = new HashMap<>();
            stats.put("category", category);
            stats.put("totalTitles", booksInCategory.size());
            stats.put("totalCopies", booksInCategory.stream()
                    .mapToInt(Book::getTotalQuantity).sum());
            stats.put("availableCopies", booksInCategory.stream()
                    .mapToInt(Book::getAvailableQuantity).sum());
            stats.put("borrowedCopies", booksInCategory.stream()
                    .mapToInt(b -> b.getTotalQuantity() - b.getAvailableQuantity()).sum());

            categoryStats.add(stats);
        }

        // Sort theo totalTitles
        categoryStats.sort((a, b) -> Integer.compare(
                (Integer) b.get("totalTitles"),
                (Integer) a.get("totalTitles")
        ));

        return categoryStats;
    }

    /**
     * Lấy danh sách sách đang được mượn nhiều nhất (hết hàng hoặc gần hết)
     */
    public List<Book> getLowStockBooks() throws SQLException {
        List<Book> allBooks = bookDAO.findAll();

        return allBooks.stream()
                .filter(book -> book.getAvailableQuantity() == 0 ||
                        (book.getAvailableQuantity() * 1.0 / book.getTotalQuantity() < 0.3))
                .sorted((a, b) -> Integer.compare(a.getAvailableQuantity(), b.getAvailableQuantity()))
                .collect(Collectors.toList());
    }

    /**
     * Thống kê người mượn nhiều nhất
     */
    public List<Map<String, Object>> getTopBorrowers(int limit) throws SQLException {
        List<BorrowRecord> allRecords = borrowRecordDAO.findAll();

        // Đếm số lần mượn theo người
        Map<String, Long> borrowerCounts = allRecords.stream()
                .collect(Collectors.groupingBy(
                        BorrowRecord::getBorrowerName,
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Long> entry : borrowerCounts.entrySet()) {
            Map<String, Object> borrowerStats = new HashMap<>();
            borrowerStats.put("borrowerName", entry.getKey());
            borrowerStats.put("totalBorrows", entry.getValue());

            // Đếm số sách đang mượn
            long currentBorrows = allRecords.stream()
                    .filter(r -> r.getBorrowerName().equals(entry.getKey()) &&
                            "BORROWED".equals(r.getStatus()))
                    .count();
            borrowerStats.put("currentBorrows", currentBorrows);

            result.add(borrowerStats);
        }

        // Sort theo totalBorrows
        result.sort((a, b) -> Long.compare(
                (Long) b.get("totalBorrows"),
                (Long) a.get("totalBorrows")
        ));

        return result.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Thống kê hoạt động theo ngày (từ visit_log)
     */
    public Map<String, Long> getActivityByAction() throws SQLException {
        List<VisitLog> logs = visitLogDAO.findAll();

        return logs.stream()
                .collect(Collectors.groupingBy(
                        VisitLog::getAction,
                        Collectors.counting()
                ));
    }

    /**
     * Lấy các hoạt động gần đây
     */
    public List<LogBookAction> getRecentBookActions(int limit) throws SQLException {
        return logBookActionDAO.findRecent(limit);
    }

    /**
     * Lấy lịch sử visit gần đây
     */
    public List<VisitLog> getRecentVisits(int limit) throws SQLException {
        return visitLogDAO.findRecent(limit);
    }

    /**
     * Thống kê theo khoảng thời gian
     */
    public Map<String, Object> getStatsByDateRange(Timestamp startDate, Timestamp endDate)
            throws SQLException {

        Map<String, Object> stats = new HashMap<>();

        // Log book actions trong khoảng thời gian
        List<LogBookAction> bookActions = logBookActionDAO.findByDateRange(startDate, endDate);
        stats.put("totalActions", bookActions.size());
        stats.put("borrowsInPeriod", bookActions.stream()
                .filter(l -> "BORROW".equals(l.getAction())).count());
        stats.put("returnsInPeriod", bookActions.stream()
                .filter(l -> "RETURN".equals(l.getAction())).count());

        // Visits trong khoảng thời gian
        List<VisitLog> visits = visitLogDAO.findByDateRange(startDate, endDate);
        stats.put("visitsInPeriod", visits.size());

        return stats;
    }

    /**
     * Phân tích tình trạng thư viện
     */
    public Map<String, Object> getLibraryHealthReport() throws SQLException {
        Map<String, Object> report = new HashMap<>();

        List<Book> allBooks = bookDAO.findAll();
        int totalBooks = allBooks.size();

        // Tính tỷ lệ sách khả dụng
        int totalCopies = allBooks.stream().mapToInt(Book::getTotalQuantity).sum();
        int availableCopies = allBooks.stream().mapToInt(Book::getAvailableQuantity).sum();
        double availabilityRate = totalCopies > 0 ?
                (availableCopies * 100.0 / totalCopies) : 0;

        report.put("totalBookTitles", totalBooks);
        report.put("totalCopies", totalCopies);
        report.put("availableCopies", availableCopies);
        report.put("availabilityRate", String.format("%.2f%%", availabilityRate));

        // Đánh giá
        String healthStatus;
        if (availabilityRate >= 70) {
            healthStatus = "Excellent - High availability";
        } else if (availabilityRate >= 50) {
            healthStatus = "Good - Moderate availability";
        } else if (availabilityRate >= 30) {
            healthStatus = "Fair - Low availability";
        } else {
            healthStatus = "Poor - Very low availability";
        }
        report.put("healthStatus", healthStatus);

        // Sách cần nhập thêm
        List<Book> lowStock = getLowStockBooks();
        report.put("booksNeedingRestock", lowStock.size());
        report.put("lowStockBooks", lowStock.stream()
                .limit(5)
                .map(Book::getTitle)
                .collect(Collectors.toList()));

        return report;
    }

    /**
     * Thống kê IP truy cập nhiều nhất
     */
    public List<Map<String, Object>> getTopVisitingIPs(int limit) throws SQLException {
        List<VisitLog> allVisits = visitLogDAO.findAll();

        Map<String, Long> ipCounts = allVisits.stream()
                .filter(v -> v.getIpAddress() != null)
                .collect(Collectors.groupingBy(
                        VisitLog::getIpAddress,
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Long> entry : ipCounts.entrySet()) {
            Map<String, Object> ipStats = new HashMap<>();
            ipStats.put("ipAddress", entry.getKey());
            ipStats.put("visitCount", entry.getValue());
            result.add(ipStats);
        }

        result.sort((a, b) -> Long.compare(
                (Long) b.get("visitCount"),
                (Long) a.get("visitCount")
        ));

        return result.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Lấy tất cả visit logs (dùng cho admin)
     */
    public List<VisitLog> getAllVisitLogs() throws SQLException {
        return visitLogDAO.findAll();
    }

    /**
     * Lấy tất cả book action logs (dùng cho admin)
     */
    public List<LogBookAction> getAllBookActionLogs() throws SQLException {
        return logBookActionDAO.findAll();
    }

    /**
     * Xóa log cũ (maintenance)
     */
    public boolean cleanupOldVisitLogs(int daysToKeep) throws SQLException {
        return visitLogDAO.deleteOldLogs(daysToKeep);
    }

    /**
     * Dashboard data - tất cả thông tin cần thiết cho trang dashboard
     */
    public Map<String, Object> getDashboardData() throws SQLException {
        Map<String, Object> dashboard = new HashMap<>();

        // Overview stats
        dashboard.put("overview", getSystemOverview());

        // Top 5 most borrowed books
        dashboard.put("topBooks", getMostBorrowedBooks(5));

        // Category statistics
        dashboard.put("categoryStats", getStatsByCategory());

        // Top 5 borrowers
        dashboard.put("topBorrowers", getTopBorrowers(5));

        // Low stock books
        dashboard.put("lowStockBooks", getLowStockBooks());

        // Recent activities
        dashboard.put("recentActions", getRecentBookActions(10));

        // Activity breakdown
        dashboard.put("activityBreakdown", getActivityByAction());

        // Library health
        dashboard.put("healthReport", getLibraryHealthReport());

        return dashboard;
    }

    /**
     * Báo cáo hàng tháng
     */
    public Map<String, Object> getMonthlyReport(int year, int month) throws SQLException {
        Map<String, Object> report = new HashMap<>();

        // Tính ngày đầu và cuối tháng
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1, 0, 0, 0);
        Timestamp startDate = new Timestamp(cal.getTimeInMillis());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Timestamp endDate = new Timestamp(cal.getTimeInMillis());

        report.put("year", year);
        report.put("month", month);
        report.put("startDate", startDate);
        report.put("endDate", endDate);

        // Lấy thống kê theo khoảng thời gian
        Map<String, Object> periodStats = getStatsByDateRange(startDate, endDate);
        report.putAll(periodStats);

        return report;
    }

    /**
     * Tìm sách chưa bao giờ được mượn
     */
    public List<Book> getNeverBorrowedBooks() throws SQLException {
        List<Book> allBooks = bookDAO.findAll();
        List<BorrowRecord> allRecords = borrowRecordDAO.findAll();

        Set<Integer> borrowedBookIds = allRecords.stream()
                .map(BorrowRecord::getBookId)
                .collect(Collectors.toSet());

        return allBooks.stream()
                .filter(book -> !borrowedBookIds.contains(book.getBookId()))
                .collect(Collectors.toList());
    }

    /**
     * Thống kê trạng thái trả sách (đúng hạn, quá hạn - tùy chọn mở rộng)
     */
    public Map<String, Long> getBorrowStatusStats() throws SQLException {
        Map<String, Long> stats = new HashMap<>();

        stats.put("borrowed", borrowRecordDAO.countByStatus("BORROWED"));
        stats.put("returned", borrowRecordDAO.countByStatus("RETURNED"));
        stats.put("total", borrowRecordDAO.countTotalBorrows());

        return stats;
    }
}
