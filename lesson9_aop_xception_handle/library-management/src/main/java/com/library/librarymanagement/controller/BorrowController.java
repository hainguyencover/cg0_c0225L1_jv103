package com.library.librarymanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.library.librarymanagement.dto.BorrowRequest;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.exception.BookNotAvailableException;
import com.library.librarymanagement.service.BorrowService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet xử lý mượn sách
 * Endpoints:
 * - POST /borrow - Mượn sách
 * - GET /borrow - Lấy danh sách các sách đang mượn
 * - GET /borrow?status=BORROWED - Lọc theo trạng thái
 * - GET /borrow?code=XXXXX - Kiểm tra thông tin mượn theo mã
 * - GET /borrow?bookId=X - Lịch sử mượn của một cuốn sách
 */
@WebServlet("/borrow")
public class BorrowController extends HttpServlet {
    private BorrowService borrowService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.borrowService = new BorrowService();
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        System.out.println("[SERVLET] BorrowServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String borrowCode = request.getParameter("code");
        String status = request.getParameter("status");
        String bookIdParam = request.getParameter("bookId");
        String borrowerName = request.getParameter("borrower");

        try {
            Map<String, Object> result = new HashMap<>();

            if (borrowCode != null) {
                handleGetByBorrowCode(borrowCode, result);
            } else if (bookIdParam != null) {
                handleGetHistoryByBook(bookIdParam, result);
            } else if (borrowerName != null) {
                handleGetHistoryByBorrower(borrowerName, result);
            } else if (status != null) {
                handleGetByStatus(status, result);
            } else {
                handleGetAllBorrowRecords(result);
            }

            sendJsonResponse(response, HttpServletResponse.SC_OK, result);

        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] BorrowServlet.doGet: " + e.getMessage());
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

        try {
            Map<String, Object> result = new HashMap<>();

            String contentType = request.getContentType();

            if (contentType != null && contentType.contains("application/json")) {
                handleBorrowBookJson(request, result);
            } else {
                handleBorrowBookForm(request, result);
            }

            sendJsonResponse(response, HttpServletResponse.SC_OK, result);

        } catch (BookNotAvailableException e) {
            sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] BorrowServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error: " + e.getMessage());
        }
    }

    private void handleGetAllBorrowRecords(Map<String, Object> result) throws Exception {
        List<BorrowRecord> records = borrowService.getAllBorrowRecords();
        result.put("success", true);
        result.put("data", records);
        result.put("total", records.size());
        result.put("message", "Borrow records retrieved successfully");
    }

    private void handleGetByBorrowCode(String borrowCode, Map<String, Object> result) throws Exception {
        if (borrowCode == null || borrowCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Borrow code is required");
        }

        BorrowRecord record = borrowService.findByBorrowCode(borrowCode.trim());

        if (record == null) {
            throw new IllegalArgumentException("Borrow record not found with code: " + borrowCode);
        }

        result.put("success", true);
        result.put("data", record);
        result.put("message", "Borrow record retrieved successfully");
    }

    private void handleGetByStatus(String status, Map<String, Object> result) throws Exception {
        String upperStatus = status.toUpperCase();

        if (!upperStatus.equals("BORROWED") && !upperStatus.equals("RETURNED")) {
            throw new IllegalArgumentException("Invalid status. Must be BORROWED or RETURNED");
        }

        List<BorrowRecord> records;

        if (upperStatus.equals("BORROWED")) {
            records = borrowService.getCurrentlyBorrowedBooks();
        } else {
            records = borrowService.getReturnedBooks();
        }

        result.put("success", true);
        result.put("data", records);
        result.put("total", records.size());
        result.put("status", upperStatus);
        result.put("message", "Records with status " + upperStatus + " retrieved successfully");
    }

    private void handleGetHistoryByBook(String bookIdParam, Map<String, Object> result) throws Exception {
        Integer bookId = Integer.parseInt(bookIdParam);
        List<BorrowRecord> records = borrowService.getBorrowHistoryByBook(bookId);

        result.put("success", true);
        result.put("data", records);
        result.put("total", records.size());
        result.put("bookId", bookId);
        result.put("message", "Borrow history for book retrieved successfully");
    }

    private void handleGetHistoryByBorrower(String borrowerName, Map<String, Object> result) throws Exception {
        List<BorrowRecord> records = borrowService.getBorrowHistoryByBorrower(borrowerName);

        result.put("success", true);
        result.put("data", records);
        result.put("total", records.size());
        result.put("borrowName", borrowerName);
        result.put("message", "Borrow history for borrower retrieved successfully");
    }

    private void handleBorrowBookJson(HttpServletRequest request, Map<String, Object> result) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        BorrowRequest borrowRequest = gson.fromJson(sb.toString(), BorrowRequest.class);

        if (borrowRequest.getBookId() == null) {
            throw new IllegalArgumentException("Book ID is required");
        }
        if (borrowRequest.getBorrowName() == null || borrowRequest.getBorrowName().trim().isEmpty()) {
            throw new IllegalArgumentException("Borrower name is required");
        }

        BorrowRecord record = borrowService.borrowBook(
                borrowRequest.getBookId(),
                borrowRequest.getBorrowName().trim()
        );

        result.put("success", true);
        result.put("data", record);
        result.put("borrowCode", record.getBorrowCode());
        result.put("message", "Book borrowed successfully. Your borrow code is: " + record.getBorrowCode());
    }

    private void handleBorrowBookForm(HttpServletRequest request, Map<String, Object> result) throws Exception {
        String bookIdParam = request.getParameter("bookId");
        String borrowerName = request.getParameter("borrowerName");

        if (bookIdParam == null || bookIdParam.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID is required");
        }
        if (borrowerName == null || borrowerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Borrower name is required");
        }

        Integer bookId = Integer.parseInt(bookIdParam);

        BorrowRecord record = borrowService.borrowBook(bookId, borrowerName.trim());

        result.put("success", true);
        result.put("data", record);
        result.put("borrowCode", record.getBorrowCode());
        result.put("message", "Book borrowed successfully. Your borrow code is: " + record.getBorrowCode());
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
        System.out.println("[SERVLET] BorrowServlet destroyed");
        super.destroy();
    }
}
