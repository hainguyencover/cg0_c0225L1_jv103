package com.library.librarymanagement.service;

import com.library.librarymanagement.dao.BookDAO;
import com.library.librarymanagement.dao.LogBookActionDAO;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.LogBookAction;

import java.sql.SQLException;
import java.util.List;

public class BookService {
    private BookDAO bookDAO;
    private LogBookActionDAO logBookActionDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
        this.logBookActionDAO = new LogBookActionDAO();
    }

    /**
     * Lấy tất cả sách
     */
    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.findAll();
    }

    /**
     * Lấy sách theo ID
     */
    public Book getBookById(Integer bookId) throws SQLException {
        return bookDAO.findById(bookId);
    }

    /**
     * Tìm sách theo thể loại
     */
    public List<Book> getBooksByCategory(String category) throws SQLException {
        return bookDAO.findByCategory(category);
    }

    /**
     * Tìm kiếm sách theo tên
     */
    public List<Book> searchBooksByTitle(String keyword) throws SQLException {
        return bookDAO.searchByTitle(keyword);
    }

    /**
     * Lấy tất cả thể loại
     */
    public List<String> getAllCategories() throws SQLException {
        return bookDAO.getAllCategories();
    }

    /**
     * Thêm sách mới
     * Tự động ghi log CREATE
     */
    public Book createBook(Book book) throws SQLException {
        // Validate
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be empty");
        }
        if (book.getTotalQuantity() == null || book.getTotalQuantity() < 0) {
            throw new IllegalArgumentException("Total quantity must be >= 0");
        }

        // Set available_quantity = total_quantity khi tạo mới
        if (book.getAvailableQuantity() == null) {
            book.setAvailableQuantity(book.getTotalQuantity());
        }

        // Insert book
        Book createdBook = bookDAO.insert(book);

        // Log action CREATE
        LogBookAction log = new LogBookAction(
                createdBook.getBookId(),
                "CREATE",
                createdBook.getTotalQuantity(),
                0,
                createdBook.getAvailableQuantity(),
                "SYSTEM"
        );
        logBookActionDAO.insert(log);

        return createdBook;
    }

    /**
     * Cập nhật thông tin sách
     * Tự động ghi log UPDATE nếu có thay đổi số lượng
     */
    public boolean updateBook(Book book) throws SQLException {
        // Validate
        if (book.getBookId() == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        Book existingBook = bookDAO.findById(book.getBookId());
        if (existingBook == null) {
            throw new IllegalArgumentException("Book not found with ID: " + book.getBookId());
        }

        if (book.getTotalQuantity() < 0 || book.getAvailableQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (book.getAvailableQuantity() > book.getTotalQuantity()) {
            throw new IllegalArgumentException("Available quantity cannot exceed total quantity");
        }

        // Update book
        boolean updated = bookDAO.update(book);

        // Log nếu có thay đổi số lượng
        if (updated && !existingBook.getAvailableQuantity().equals(book.getAvailableQuantity())) {
            int changeAmount = book.getAvailableQuantity() - existingBook.getAvailableQuantity();

            LogBookAction log = new LogBookAction(
                    book.getBookId(),
                    "UPDATE",
                    changeAmount,
                    existingBook.getAvailableQuantity(),
                    book.getAvailableQuantity(),
                    "ADMIN"
            );
            logBookActionDAO.insert(log);
        }

        return updated;
    }

    /**
     * Xóa sách
     */
    public boolean deleteBook(Integer bookId) throws SQLException {
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }

        // Kiểm tra xem có sách đang được mượn không
        if (book.getAvailableQuantity() < book.getTotalQuantity()) {
            throw new IllegalStateException("Cannot delete book. Some copies are currently borrowed.");
        }

        // Log action DELETE
        LogBookAction log = new LogBookAction(
                bookId,
                "DELETE",
                -book.getTotalQuantity(),
                book.getAvailableQuantity(),
                0,
                "ADMIN"
        );
        logBookActionDAO.insert(log);

        return bookDAO.delete(bookId);
    }

    /**
     * Kiểm tra sách có sẵn để mượn không
     */
    public boolean isBookAvailable(Integer bookId) throws SQLException {
        return bookDAO.isAvailable(bookId);
    }

    /**
     * Lấy số lượng sách còn lại
     */
    public Integer getAvailableQuantity(Integer bookId) throws SQLException {
        Book book = bookDAO.findById(bookId);
        return book != null ? book.getAvailableQuantity() : 0;
    }

    /**
     * Thêm số lượng sách (nhập thêm)
     */
    public boolean addBookQuantity(Integer bookId, int quantity) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }

        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }

        int beforeTotal = book.getTotalQuantity();
        int beforeAvailable = book.getAvailableQuantity();

        book.setTotalQuantity(book.getTotalQuantity() + quantity);
        book.setAvailableQuantity(book.getAvailableQuantity() + quantity);

        boolean updated = bookDAO.update(book);

        if (updated) {
            LogBookAction log = new LogBookAction(
                    bookId,
                    "UPDATE",
                    quantity,
                    beforeAvailable,
                    book.getAvailableQuantity(),
                    "ADMIN"
            );
            logBookActionDAO.insert(log);
        }

        return updated;
    }

    /**
     * Lấy log history của một cuốn sách
     */
    public List<LogBookAction> getBookActionLogs(Integer bookId) throws SQLException {
        return logBookActionDAO.findByBookId(bookId);
    }

    /**
     * Thống kê tổng số sách
     */
    public int getTotalBooksCount() throws SQLException {
        List<Book> books = bookDAO.findAll();
        return books.size();
    }

    /**
     * Thống kê tổng số lượng sách (tất cả copies)
     */
    public int getTotalBooksQuantity() throws SQLException {
        List<Book> books = bookDAO.findAll();
        return books.stream()
                .mapToInt(Book::getTotalQuantity)
                .sum();
    }

    /**
     * Thống kê số sách đang được mượn
     */
    public int getBorrowedBooksCount() throws SQLException {
        List<Book> books = bookDAO.findAll();
        return books.stream()
                .mapToInt(book -> book.getTotalQuantity() - book.getAvailableQuantity())
                .sum();
    }
}
