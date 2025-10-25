package com.library.librarymanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.library.librarymanagement.dto.ReturnRequest;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.exception.InvalidBorrowCodeException;
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
import java.util.Map;

/**
 * Servlet xử lý trả sách
 * Endpoints:
 * - POST /return - Trả sách bằng mã mượn
 * - GET /return?code=XXXXX - Kiểm tra thông tin trước khi trả (validation)
 */
@WebServlet("/library-management/return")
public class ReturnController extends HttpServlet {
    private BorrowService borrowService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.borrowService = new BorrowService();
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        System.out.println("[SERVLET] ReturnServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String borrowCode = request.getParameter("code");

        try {
            Map<String, Object> result = new HashMap<>();

            if (borrowCode == null || borrowCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Borrow code is required");
            }

            handleValidateBorrowCode(borrowCode.trim(), result);
            sendJsonResponse(response, HttpServletResponse.SC_OK, result);

        } catch (InvalidBorrowCodeException e) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] ReturnServlet.doGet: " + e.getMessage());
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
                handleReturnBookJson(request, result);
            } else {
                handleReturnBookForm(request, result);
            }

            sendJsonResponse(response, HttpServletResponse.SC_OK, result);

        } catch (InvalidBorrowCodeException e) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] ReturnServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error: " + e.getMessage());
        }
    }

    /**
     * Validate mã mượn trước khi trả - GET request
     */
    private void handleValidateBorrowCode(String borrowCode, Map<String, Object> result) throws Exception {
        BorrowRecord record = borrowService.getBorrowDetails(borrowCode);

        boolean canReturn = "BORROWED".equals(record.getStatus());

        result.put("success", true);
        result.put("data", record);
        result.put("canReturn", canReturn);

        if (canReturn) {
            result.put("message", "This book can be returned. Borrow code is valid.");
        } else {
            result.put("message", "This book has already been returned on " + record.getReturnDate());
        }
    }

    /**
     * Xử lý trả sách - JSON request
     */
    private void handleReturnBookJson(HttpServletRequest request, Map<String, Object> result) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        ReturnRequest returnRequest = gson.fromJson(sb.toString(), ReturnRequest.class);

        if (returnRequest.getBorrowCode() == null || returnRequest.getBorrowCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Borrow code is required");
        }

        String borrowCode = returnRequest.getBorrowCode().trim();
        BorrowRecord record = borrowService.returnBook(borrowCode);

        result.put("success", true);
        result.put("data", record);
        result.put("message", "Book returned successfully. Thank you!");
    }

    /**
     * Xử lý trả sách - Form data request
     */
    private void handleReturnBookForm(HttpServletRequest request, Map<String, Object> result) throws Exception {
        String borrowCode = request.getParameter("borrowCode");

        if (borrowCode == null || borrowCode.trim().isEmpty()) {
            // Fallback: check parameter name "code"
            borrowCode = request.getParameter("code");
        }

        if (borrowCode == null || borrowCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Borrow code is required");
        }

        borrowCode = borrowCode.trim();

        // Validate format (5 digits)
        if (!borrowCode.matches("\\d{5}")) {
            throw new IllegalArgumentException("Invalid borrow code format. Must be 5 digits.");
        }

        BorrowRecord record = borrowService.returnBook(borrowCode);

        result.put("success", true);
        result.put("data", record);
        result.put("borrowCode", borrowCode);
        result.put("bookTitle", record.getBookTitle());
        result.put("borrowerName", record.getBorrowerName());
        result.put("borrowDate", record.getBorrowDate());
        result.put("returnDate", record.getReturnDate());
        result.put("message", "Book '" + record.getBookTitle() + "' returned successfully. Thank you!");
    }

    /**
     * Send JSON response
     */
    private void sendJsonResponse(HttpServletResponse response, int statusCode, Map<String, Object> data)
            throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    /**
     * Send error response
     */
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
        System.out.println("[SERVLET] ReturnServlet destroyed");
        super.destroy();
    }
}
