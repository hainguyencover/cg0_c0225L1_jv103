package com.library.librarymanagement.controller;

import com.library.librarymanagement.config.DatabaseConfig;
import com.library.librarymanagement.dao.BookDAO;
import com.library.librarymanagement.entity.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;


@WebServlet("/test")
public class TestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Test Results</title>");
        out.println("<style>body{font-family:Arial;padding:20px;} .success{color:green;} .error{color:red;} table{border-collapse:collapse;margin-top:20px;} td,th{border:1px solid #ddd;padding:8px;text-align:left;}</style>");
        out.println("</head><body>");
        out.println("<h1>🧪 Library Management System - Test Results</h1>");

        // Test 1: Database Connection
        out.println("<h2>Test 1: Database Connection</h2>");
        try {
            Connection conn = DatabaseConfig.getConnection();
            if (conn != null && !conn.isClosed()) {
                out.println("<p class='success'>✓ Database connection: SUCCESS</p>");
                out.println("<p>Connection URL: " + conn.getMetaData().getURL() + "</p>");
                conn.close();
            } else {
                out.println("<p class='error'>✗ Database connection: FAILED (connection is null or closed)</p>");
            }
        } catch (Exception e) {
            out.println("<p class='error'>✗ Database connection: FAILED</p>");
            out.println("<p class='error'>Error: " + e.getMessage() + "</p>");
            out.println("<pre>" + getStackTrace(e) + "</pre>");
        }

        // Test 2: BookDAO - Load Books
        out.println("<h2>Test 2: Load Books from Database</h2>");
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.findAll();

            if (books != null && books.size() > 0) {
                out.println("<p class='success'>✓ Load books: SUCCESS</p>");
                out.println("<p>Total books found: " + books.size() + "</p>");

                out.println("<table>");
                out.println("<tr><th>ID</th><th>Title</th><th>Author</th><th>Category</th><th>Total</th><th>Available</th></tr>");

                for (Book book : books) {
                    out.println("<tr>");
                    out.println("<td>" + book.getBookId() + "</td>");
                    out.println("<td>" + book.getTitle() + "</td>");
                    out.println("<td>" + book.getAuthor() + "</td>");
                    out.println("<td>" + (book.getCategory() != null ? book.getCategory() : "N/A") + "</td>");
                    out.println("<td>" + book.getTotalQuantity() + "</td>");
                    out.println("<td>" + book.getAvailableQuantity() + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");
            } else {
                out.println("<p class='error'>✗ No books found in database</p>");
                out.println("<p>Possible reasons:</p>");
                out.println("<ul>");
                out.println("<li>Database is empty - run database_schema.sql to insert sample data</li>");
                out.println("<li>Table 'book' doesn't exist</li>");
                out.println("<li>Database connection issue</li>");
                out.println("</ul>");
            }

        } catch (Exception e) {
            out.println("<p class='error'>✗ Load books: FAILED</p>");
            out.println("<p class='error'>Error: " + e.getMessage() + "</p>");
            out.println("<pre>" + getStackTrace(e) + "</pre>");
        }

        // Test 3: API Endpoint Test
        out.println("<h2>Test 3: API Endpoints</h2>");
        out.println("<p>Try these API endpoints:</p>");
        out.println("<ul>");
        out.println("<li><a href='/books' target='_blank'>/books</a> - Get all books (JSON)</li>");
        out.println("<li><a href='/statistics?action=overview' target='_blank'>/statistics?action=overview</a> - Get overview</li>");
        out.println("<li><a href='/borrow?status=BORROWED' target='_blank'>/borrow?status=BORROWED</a> - Get borrowed books</li>");
        out.println("</ul>");

        // Test 4: Frontend Pages
        out.println("<h2>Test 4: Frontend Pages</h2>");
        out.println("<p>Access these pages:</p>");
        out.println("<ul>");
        out.println("<li><a href='/index.html' target='_blank'>Trang Chủ</a></li>");
        out.println("<li><a href='/books.html' target='_blank'>Danh Sách Sách</a></li>");
        out.println("<li><a href='/borrow.html' target='_blank'>Mượn Sách</a></li>");
        out.println("<li><a href='/return.html' target='_blank'>Trả Sách</a></li>");
        out.println("<li><a href='/statistics.html' target='_blank'>Thống Kê</a></li>");
        out.println("</ul>");

        // Configuration Info
        out.println("<h2>Configuration Info</h2>");
        out.println("<p><strong>Context Path:</strong> " + request.getContextPath() + "</p>");
        out.println("<p><strong>Server Info:</strong> " + getServletContext().getServerInfo() + "</p>");

        out.println("<hr><p><a href='/library-management/test'>Refresh Test</a></p>");
        out.println("</body></html>");
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
            if (sb.length() > 2000) {
                sb.append("\t... (truncated)");
                break;
            }
        }
        return sb.toString();
    }
}
