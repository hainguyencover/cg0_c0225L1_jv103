package com.library.librarymanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.LogBookAction;
import com.library.librarymanagement.service.BookService;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet xử lý các request liên quan đến sách
 * Endpoints:
 * - GET /books - Lấy danh sách sách (có thể filter theo category)
 * - GET /books?id=X - Lấy chi tiết sách
 * - GET /books?action=search&keyword=X - Tìm kiếm sách
 * - GET /books?action=categories - Lấy danh sách thể loại
 * - POST /books?action=create - Tạo sách mới
 * - POST /books?action=update - Cập nhật sách
 * - POST /books?action=delete&id=X - Xóa sách
 */
@WebServlet("/books")
public class BookController extends HttpServlet {
    private BookService bookService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.bookService = new BookService();
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        System.out.println("[SERVLET] BookServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String bookIdParam = request.getParameter("id");
        String category = request.getParameter("category");

        try {
            Map<String, Object> result = new HashMap<>();

            if (bookIdParam != null) {
                handleGetBookById(bookIdParam, result);
            } else if ("search".equals(action)) {
                handleSearchBooks(request, result);
            } else if ("categories".equals(action)) {
                handleGetCategories(result);
            } else if (category != null) {
                handleGetBooksByCategory(category, result);
            } else {
                handleGetAllBooks(result);
            }

            sendJsonResponse(response, HttpServletResponse.SC_OK, result);

        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] BookServlet.doGet: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            Map<String, Object> result = new HashMap<>();

            if ("create".equals(action)) {
                handleCreateBook(request, result);
            } else if ("update".equals(action)) {
                handleUpdateBook(request, result);
            } else if ("delete".equals(action)) {
                handleDeleteBook(request, result);
            } else {
                throw new IllegalArgumentException("Invalid action: " + action);
            }

            sendJsonResponse(response, HttpServletResponse.SC_OK, result);

        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] BookServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error: " + e.getMessage());
        }
    }

    private void handleGetAllBooks(Map<String, Object> result) throws Exception {
        List<Book> books = bookService.getAllBooks();
        result.put("success", true);
        result.put("data", books);
        result.put("total", books.size());
        result.put("message", "Books retrieved successfully");
    }

    private void handleGetBookById(String bookIdParam, Map<String, Object> result) throws Exception {
        Integer bookId = Integer.parseInt(bookIdParam);
        Book book = bookService.getBookById(bookId);

        if (book == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }

        List<LogBookAction> logs = bookService.getBookActionLogs(bookId);

        result.put("success", true);
        result.put("data", book);
        result.put("logs", logs);
        result.put("message", "Book details retrieved successfully");
    }

    private void handleSearchBooks(HttpServletRequest request, Map<String, Object> result) throws Exception {
        String keyword = request.getParameter("keyword");

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword is required");
        }

        List<Book> books = bookService.searchBooksByTitle(keyword.trim());
        result.put("success", true);
        result.put("data", books);
        result.put("total", books.size());
        result.put("keyword", keyword);
        result.put("message", "Search completed successfully");
    }

    private void handleGetCategories(Map<String, Object> result) throws Exception {
        List<String> categories = bookService.getAllCategories();
        result.put("success", true);
        result.put("data", categories);
        result.put("total", categories.size());
        result.put("message", "Categories retrieved successfully");
    }

    private void handleGetBooksByCategory(String category, Map<String, Object> result) throws Exception {
        List<Book> books = bookService.getBooksByCategory(category);
        result.put("success", true);
        result.put("data", books);
        result.put("total", books.size());
        result.put("category", category);
        result.put("message", "Books in category retrieved successfully");
    }

    private void handleCreateBook(HttpServletRequest request, Map<String, Object> result) throws Exception {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String category = request.getParameter("category");
        String totalQuantityParam = request.getParameter("totalQuantity");

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Book author is required");
        }
        if (totalQuantityParam == null || totalQuantityParam.trim().isEmpty()) {
            throw new IllegalArgumentException("Total quantity is required");
        }

        Integer totalQuantity = Integer.parseInt(totalQuantityParam);

        if (totalQuantity < 0) {
            throw new IllegalArgumentException("Total quantity must be non-negative");
        }

        Book book = new Book();
        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setCategory(category != null ? category.trim() : null);
        book.setTotalQuantity(totalQuantity);
        book.setAvailableQuantity(totalQuantity);

        Book createdBook = bookService.createBook(book);

        result.put("success", true);
        result.put("data", createdBook);
        result.put("message", "Book created successfully with ID: " + createdBook.getBookId());
    }

    private void handleUpdateBook(HttpServletRequest request, Map<String, Object> result) throws Exception {
        String bookIdParam = request.getParameter("id");

        if (bookIdParam == null || bookIdParam.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID is required for update");
        }

        Integer bookId = Integer.parseInt(bookIdParam);
        Book existingBook = bookService.getBookById(bookId);

        if (existingBook == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }

        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String category = request.getParameter("category");
        String totalQuantityParam = request.getParameter("totalQuantity");
        String availableQuantityParam = request.getParameter("availableQuantity");

        if (title != null && !title.trim().isEmpty()) {
            existingBook.setTitle(title.trim());
        }
        if (author != null && !author.trim().isEmpty()) {
            existingBook.setAuthor(author.trim());
        }
        if (category != null) {
            existingBook.setCategory(category.trim());
        }
        if (totalQuantityParam != null && !totalQuantityParam.trim().isEmpty()) {
            existingBook.setTotalQuantity(Integer.parseInt(totalQuantityParam));
        }
        if (availableQuantityParam != null && !availableQuantityParam.trim().isEmpty()) {
            existingBook.setAvailableQuantity(Integer.parseInt(availableQuantityParam));
        }

        boolean updated = bookService.updateBook(existingBook);

        result.put("success", updated);
        result.put("data", existingBook);
        result.put("message", updated ? "Book updated successfully" : "Book update failed");
    }

    private void handleDeleteBook(HttpServletRequest request, Map<String, Object> result) throws Exception {
        String bookIdParam = request.getParameter("id");

        if (bookIdParam == null || bookIdParam.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID is required for deletion");
        }

        Integer bookId = Integer.parseInt(bookIdParam);
        boolean deleted = bookService.deleteBook(bookId);

        result.put("success", deleted);
        result.put("message", deleted ? "Book deleted successfully" : "Book deletion failed");
    }

    private void sendJsonResponse(HttpServletResponse response, int statusCode, Map<String, Object> data)
            throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(error));
        out.flush();
    }

    @Override
    public void destroy() {
        System.out.println("[SERVLET] BookServlet destroyed");
        super.destroy();
    }
}
