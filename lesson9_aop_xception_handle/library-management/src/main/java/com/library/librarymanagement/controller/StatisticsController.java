package com.library.librarymanagement.controller;

import javax.servlet.http.HttpServlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.library.librarymanagement.service.StatisticsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet xử lý thống kê
 * Endpoints:
 * - GET /statistics?action=overview - Tổng quan hệ thống
 * - GET /statistics?action=dashboard - Dashboard data đầy đủ
 * - GET /statistics?action=topBooks&limit=10 - Top sách được mượn nhiều
 * - GET /statistics?action=topBorrowers&limit=10 - Top người mượn nhiều
 * - GET /statistics?action=categories - Thống kê theo thể loại
 * - GET /statistics?action=health - Library health report
 * - GET /statistics?action=lowStock - Sách sắp hết
 */
@WebServlet("/library-management/statistics")
public class StatisticsController extends HttpServlet {
    private StatisticsService statisticsService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.statisticsService = new StatisticsService();
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();

        System.out.println("[SERVLET] StatisticsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            Map<String, Object> result = new HashMap<>();

            if (action == null || "overview".equals(action)) {
                handleGetOverview(result);
            } else if ("dashboard".equals(action)) {
                handleGetDashboard(result);
            } else if ("topBooks".equals(action)) {
                handleGetTopBooks(request, result);
            } else if ("topBorrowers".equals(action)) {
                handleGetTopBorrowers(request, result);
            } else if ("categories".equals(action)) {
                handleGetCategoryStats(result);
            } else if ("health".equals(action)) {
                handleGetHealthReport(result);
            } else if ("lowStock".equals(action)) {
                handleGetLowStockBooks(result);
            } else if ("activity".equals(action)) {
                handleGetActivityStats(result);
            } else {
                throw new IllegalArgumentException("Invalid action: " + action);
            }

            sendJsonResponse(response, HttpServletResponse.SC_OK, result);

        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] StatisticsServlet.doGet: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error: " + e.getMessage());
        }
    }

    private void handleGetOverview(Map<String, Object> result) throws Exception {
        Map<String, Object> overview = statisticsService.getSystemOverview();
        result.put("success", true);
        result.put("data", overview);
        result.put("message", "System overview retrieved successfully");
    }

    private void handleGetDashboard(Map<String, Object> result) throws Exception {
        Map<String, Object> dashboard = statisticsService.getDashboardData();
        result.put("success", true);
        result.put("data", dashboard);
        result.put("message", "Dashboard data retrieved successfully");
    }

    private void handleGetTopBooks(HttpServletRequest request, Map<String, Object> result) throws Exception {
        String limitParam = request.getParameter("limit");
        int limit = limitParam != null ? Integer.parseInt(limitParam) : 10;

        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        result.put("success", true);
        result.put("data", statisticsService.getMostBorrowedBooks(limit));
        result.put("limit", limit);
        result.put("message", "Top borrowed books retrieved successfully");
    }

    private void handleGetTopBorrowers(HttpServletRequest request, Map<String, Object> result) throws Exception {
        String limitParam = request.getParameter("limit");
        int limit = limitParam != null ? Integer.parseInt(limitParam) : 10;

        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        result.put("success", true);
        result.put("data", statisticsService.getTopBorrowers(limit));
        result.put("limit", limit);
        result.put("message", "Top borrowers retrieved successfully");
    }

    private void handleGetCategoryStats(Map<String, Object> result) throws Exception {
        result.put("success", true);
        result.put("data", statisticsService.getStatsByCategory());
        result.put("message", "Category statistics retrieved successfully");
    }

    private void handleGetHealthReport(Map<String, Object> result) throws Exception {
        result.put("success", true);
        result.put("data", statisticsService.getLibraryHealthReport());
        result.put("message", "Library health report retrieved successfully");
    }

    private void handleGetLowStockBooks(Map<String, Object> result) throws Exception {
        result.put("success", true);
        result.put("data", statisticsService.getLowStockBooks());
        result.put("message", "Low stock books retrieved successfully");
    }

    private void handleGetActivityStats(Map<String, Object> result) throws Exception {
        result.put("success", true);
        result.put("data", statisticsService.getActivityByAction());
        result.put("message", "Activity statistics retrieved successfully");
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
        System.out.println("[SERVLET] StatisticsServlet destroyed");
        super.destroy();
    }
}
