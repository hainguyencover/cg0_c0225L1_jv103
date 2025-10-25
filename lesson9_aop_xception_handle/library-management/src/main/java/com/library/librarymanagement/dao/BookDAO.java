package com.library.librarymanagement.dao;

import com.library.librarymanagement.config.DatabaseConfig;
import com.library.librarymanagement.entity.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    public Book findById(Integer bookId) throws SQLException {
        String sql = "SELECT * FROM book WHERE book_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        }
        return null;
    }

    public List<Book> findByCategory(String category) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE category = ? ORDER BY title";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }
        return books;
    }

    public List<Book> searchByTitle(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE title LIKE ? ORDER BY title";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }
        return books;
    }

    public Book insert(Book book) throws SQLException {
        String sql = "INSERT INTO book(title, author, category, total_quantity, available_quantity, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?,NOW(), NOW()";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getTotalQuantity());
            ps.setInt(5, book.getAvailableQuantity());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        book.setBookId(rs.getInt(1));
                    }
                }
            }
        }
        return book;
    }

    public boolean update(Book book) throws SQLException {
        String sql = "UPDATE book set title = ?, author = ?, category = ?, " +
                "total_quantity = ?, available_quantity = ?, updated_at = NOW()" +
                "WHERE book_id = ?";
        try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getTotalQuantity());
            ps.setInt(5, book.getAvailableQuantity());
            ps.setInt(6, book.getBookId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Integer bookId) throws SQLException {
        String sql = "DELETE FROM book WHERE book_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateAvailableQuantity(Integer bookId, int changeAmount) throws SQLException {
        String sql = "UPDATE book SET available_quantity = available_quantity + ?, updated_at = NOW()" +
                "WHERE book_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, changeAmount);
            ps.setInt(2, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean increaseAvailableQuantity(Integer bookId) throws SQLException {
        return updateAvailableQuantity(bookId, 1);
    }

    public boolean decreaseAvailableQuantity(Integer bookId) throws SQLException {
        return updateAvailableQuantity(bookId, -1);
    }

    public boolean isAvailable(Integer bookId) throws SQLException {
        String sql = "SELECT available_quantity FROM book WHERE book_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_quantity") > 0;
                }
            }
        }
        return false;
    }

    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM book WHERE category IS NULL ORDER BY category";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setCategory(rs.getString("category"));
        book.setTotalQuantity(rs.getInt("total_quantity"));
        book.setAvailableQuantity(rs.getInt("available_quantity"));
        book.setCreatedAt(rs.getTimestamp("created_at"));
        book.setUpdatedAt(rs.getTimestamp("updated_at"));
        return book;
    }
}
