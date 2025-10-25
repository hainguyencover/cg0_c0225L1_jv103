package com.library.librarymanagement.service;

import com.library.librarymanagement.dao.BookDAO;
import com.library.librarymanagement.dao.BorrowRecordDAO;
import com.library.librarymanagement.dao.LogBookActionDAO;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.entity.LogBookAction;
import com.library.librarymanagement.exception.BookNotAvailableException;
import com.library.librarymanagement.exception.InvalidBorrowCodeException;

import java.sql.SQLException;
import java.util.List;

public class BorrowService {
    private BookDAO bookDAO;
    private BorrowRecordDAO borrowRecordDAO;
    private LogBookActionDAO logBookActionDAO;

    public BorrowService() {
        this.bookDAO = new BookDAO();
        this.borrowRecordDAO = new BorrowRecordDAO();
        this.logBookActionDAO = new LogBookActionDAO();
    }

    /**
     * Mượn sách - Logic chính
     *
     * @return BorrowRecord với borrowCode để người dùng lưu lại
     */
    public BorrowRecord borrowBook(Integer bookId, String borrowerName)
            throws SQLException, BookNotAvailableException {

        // Validate input
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        if (borrowerName == null || borrowerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Borrower name cannot be empty");
        }

        // Kiểm tra sách có tồn tại không
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }

        // Kiểm tra sách còn không
        if (book.getAvailableQuantity() <= 0) {
            throw new BookNotAvailableException(
                    "Book '" + book.getTitle() + "' is not available. All copies are borrowed."
            );
        }

        // Generate unique borrow code
        String borrowCode = borrowRecordDAO.generateUniqueBorrowCode();

        // Lưu trạng thái trước khi thay đổi
        int beforeQuantity = book.getAvailableQuantity();

        // Giảm số lượng sách
        boolean decreased = bookDAO.decreaseAvailableQuantity(bookId);
        if (!decreased) {
            throw new SQLException("Failed to update book quantity");
        }

        // Tạo bản ghi mượn
        BorrowRecord record = new BorrowRecord(borrowCode, bookId, borrowerName.trim());
        record = borrowRecordDAO.insert(record);

        // Set book title cho response
        record.setBookTitle(book.getTitle());

        // Ghi log BORROW
        LogBookAction log = new LogBookAction(
                bookId,
                "BORROW",
                -1,
                beforeQuantity,
                beforeQuantity - 1,
                borrowerName
        );
        logBookActionDAO.insert(log);

        return record;
    }

    /**
     * Trả sách - Logic chính
     */
    public BorrowRecord returnBook(String borrowCode)
            throws SQLException, InvalidBorrowCodeException {

        // Validate input
        if (borrowCode == null || borrowCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Borrow code cannot be empty");
        }

        borrowCode = borrowCode.trim();

        // Kiểm tra mã mượn có tồn tại không
        BorrowRecord record = borrowRecordDAO.findByBorrowCode(borrowCode);
        if (record == null) {
            throw new InvalidBorrowCodeException("Invalid borrow code: " + borrowCode);
        }

        // Kiểm tra đã trả chưa
        if ("RETURNED".equals(record.getStatus())) {
            throw new InvalidBorrowCodeException(
                    "This book has already been returned on " + record.getReturnDate()
            );
        }

        // Lấy thông tin sách
        Book book = bookDAO.findById(record.getBookId());
        if (book == null) {
            throw new SQLException("Book not found in database");
        }

        // Lưu trạng thái trước khi thay đổi
        int beforeQuantity = book.getAvailableQuantity();

        // Tăng số lượng sách
        boolean increased = bookDAO.increaseAvailableQuantity(record.getBookId());
        if (!increased) {
            throw new SQLException("Failed to update book quantity");
        }

        // Cập nhật trạng thái mượn
        boolean updated = borrowRecordDAO.updateStatusToReturned(borrowCode);
        if (!updated) {
            // Rollback: giảm lại số lượng
            bookDAO.decreaseAvailableQuantity(record.getBookId());
            throw new SQLException("Failed to update borrow record");
        }

        // Lấy lại record đã cập nhật
        record = borrowRecordDAO.findByBorrowCode(borrowCode);

        // Ghi log RETURN
        LogBookAction log = new LogBookAction(
                record.getBookId(),
                "RETURN",
                1,
                beforeQuantity,
                beforeQuantity + 1,
                record.getBorrowerName()
        );
        logBookActionDAO.insert(log);

        return record;
    }

    /**
     * Lấy tất cả lịch sử mượn
     */
    public List<BorrowRecord> getAllBorrowRecords() throws SQLException {
        return borrowRecordDAO.findAll();
    }

    /**
     * Lấy các sách đang được mượn
     */
    public List<BorrowRecord> getCurrentlyBorrowedBooks() throws SQLException {
        return borrowRecordDAO.findByStatus("BORROWED");
    }

    /**
     * Lấy lịch sử đã trả
     */
    public List<BorrowRecord> getReturnedBooks() throws SQLException {
        return borrowRecordDAO.findByStatus("RETURNED");
    }

    /**
     * Lấy lịch sử mượn của một cuốn sách
     */
    public List<BorrowRecord> getBorrowHistoryByBook(Integer bookId) throws SQLException {
        return borrowRecordDAO.findByBookId(bookId);
    }

    /**
     * Lấy lịch sử mượn của người dùng
     */
    public List<BorrowRecord> getBorrowHistoryByBorrower(String borrowerName) throws SQLException {
        return borrowRecordDAO.findByBorrowerName(borrowerName);
    }

    /**
     * Tìm bản ghi mượn theo mã
     */
    public BorrowRecord findByBorrowCode(String borrowCode) throws SQLException {
        return borrowRecordDAO.findByBorrowCode(borrowCode);
    }

    /**
     * Kiểm tra mã mượn có hợp lệ không
     */
    public boolean isValidBorrowCode(String borrowCode) throws SQLException {
        if (borrowCode == null || borrowCode.trim().isEmpty()) {
            return false;
        }
        return borrowRecordDAO.findByBorrowCode(borrowCode.trim()) != null;
    }

    /**
     * Kiểm tra sách đã được trả chưa
     */
    public boolean isReturned(String borrowCode) throws SQLException {
        BorrowRecord record = borrowRecordDAO.findByBorrowCode(borrowCode);
        return record != null && "RETURNED".equals(record.getStatus());
    }

    /**
     * Đếm tổng số lượt mượn
     */
    public long getTotalBorrowsCount() throws SQLException {
        return borrowRecordDAO.countTotalBorrows();
    }

    /**
     * Đếm số sách đang được mượn
     */
    public long getCurrentBorrowsCount() throws SQLException {
        return borrowRecordDAO.countByStatus("BORROWED");
    }

    /**
     * Đếm số sách đã trả
     */
    public long getReturnedCount() throws SQLException {
        return borrowRecordDAO.countByStatus("RETURNED");
    }

    /**
     * Lấy thông tin chi tiết về một lần mượn
     */
    public BorrowRecord getBorrowDetails(String borrowCode)
            throws SQLException, InvalidBorrowCodeException {

        if (borrowCode == null || borrowCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Borrow code cannot be empty");
        }

        BorrowRecord record = borrowRecordDAO.findByBorrowCode(borrowCode.trim());
        if (record == null) {
            throw new InvalidBorrowCodeException("Borrow code not found: " + borrowCode);
        }

        return record;
    }

    /**
     * Kiểm tra người dùng có sách đang mượn không
     */
    public boolean hasBorrowedBooks(String borrowerName) throws SQLException {
        List<BorrowRecord> records = borrowRecordDAO.findByBorrowerName(borrowerName);
        return records.stream().anyMatch(r -> "BORROWED".equals(r.getStatus()));
    }

    /**
     * Đếm số sách người dùng đang mượn
     */
    public long countBorrowedBooksByUser(String borrowerName) throws SQLException {
        List<BorrowRecord> records = borrowRecordDAO.findByBorrowerName(borrowerName);
        return records.stream().filter(r -> "BORROWED".equals(r.getStatus())).count();
    }
}
