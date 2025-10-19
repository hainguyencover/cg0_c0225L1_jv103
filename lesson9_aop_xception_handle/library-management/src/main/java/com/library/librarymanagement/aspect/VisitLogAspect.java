package com.library.librarymanagement.aspect;

import com.library.librarymanagement.dao.VisitLogDAO;
import com.library.librarymanagement.entity.VisitLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Aspect
public class VisitLogAspect {
    private VisitLogDAO visitLogDAO;

    public VisitLogAspect() {
        this.visitLogDAO = new VisitLogDAO();
    }

    /**
     * Pointcut cho tất cả method trong Service layer
     * Log mọi thao tác nghiệp vụ
     */
    @Pointcut("execution(* com.library.librarymanagement.service.*.*(..))")
    public void serviceLayerPointcut() {
    }

    /**
     * Pointcut cho các method trong Servlet layer
     * Log mọi HTTP request
     */
    @Pointcut("execution(* com.library.librarymanagement.controller.*.doGet(..)) || " +
            "execution(* com.library.librarymanagement.controller.*.doPost(..))")
    public void servletLayerPointcut() {
    }

    /**
     * Pointcut cho BorrowService methods
     */
    @Pointcut("execution(* com.library.librarymanagement.service.BorrowService.borrowBook(..)) || " +
            "execution(* com.library.librarymanagement.service.BorrowService.returnBook(..))")
    public void borrowServicePointcut() {
    }

    /**
     * Pointcut cho BookService query methods
     */
    @Pointcut("execution(* com.library.librarymanagement.service.BookService.getAllBooks(..)) || " +
            "execution(* com.library.librarymanagement.service.BookService.getBookById(..)) || " +
            "execution(* com.library.librarymanagement.service.BookService.searchBooksByTitle(..))")
    public void bookQueryPointcut() {
    }

    /**
     * Log TRƯỚC khi thực hiện các thao tác mượn/trả sách
     * Sử dụng @Before để đảm bảo log được ghi ngay cả khi method fail
     */
    @Before("borrowServicePointcut()")
    public void logBeforeBorrowOperation(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String action = methodName.toUpperCase().replace("BOOK", "");

            Object[] args = joinPoint.getArgs();
            String details = buildDetailsFromArgs(methodName, args);

            VisitLog log = new VisitLog(action, getIpAddress(), details);
            visitLogDAO.insert(log);

            System.out.println("[AOP] VisitLogAspect: Logged " + action + " operation - " + details);

        } catch (SQLException e) {
            System.err.println("[AOP] Error logging visit (before borrow operation): " + e.getMessage());
        }
    }

    /**
     * Log SAU khi thực hiện các query về sách
     * Sử dụng @After để log mọi lần xem sách (thành công hay thất bại)
     */
    @After("bookQueryPointcut()")
    public void logAfterBookQuery(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String action = "VIEW_BOOKS";

            if (methodName.contains("ById")) {
                action = "VIEW_BOOK_DETAIL";
            } else if (methodName.contains("search")) {
                action = "SEARCH_BOOKS";
            }

            Object[] args = joinPoint.getArgs();
            String details = buildDetailsFromArgs(methodName, args);

            VisitLog log = new VisitLog(action, getIpAddress(), details);
            visitLogDAO.insert(log);

            System.out.println("[AOP] VisitLogAspect: Logged " + action + " - " + details);

        } catch (SQLException e) {
            System.err.println("[AOP] Error logging visit (after book query): " + e.getMessage());
        }
    }

    /**
     * Log tất cả request vào servlet layer
     * Sử dụng @Before để log ngay khi request đến
     */
    @Before("servletLayerPointcut()")
    public void logServletRequest(JoinPoint joinPoint) {
        try {
            String servletName = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            String action = servletName.replace("Servlet", "").toUpperCase() + "_" +
                    methodName.replace("do", "").toUpperCase();

            Object[] args = joinPoint.getArgs();
            String ipAddress = extractIpFromServletArgs(args);
            String details = buildServletDetails(servletName, methodName, args);

            VisitLog log = new VisitLog(action, ipAddress, details);
            visitLogDAO.insert(log);

            System.out.println("[AOP] VisitLogAspect: Logged servlet request " + action +
                    " from IP: " + ipAddress);

        } catch (SQLException e) {
            System.err.println("[AOP] Error logging servlet request: " + e.getMessage());
        }
    }

    /**
     * Log mọi thao tác vào service layer (tổng quát)
     * Có thể bật/tắt tùy nhu cầu
     */
    @After("serviceLayerPointcut()")
    public void logServiceLayerAccess(JoinPoint joinPoint) {
        try {
            String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            // Skip logging cho một số method không quan trọng
            if (shouldSkipLogging(className, methodName)) {
                return;
            }

            String action = "SERVICE_" + className.replace("Service", "").toUpperCase() +
                    "_" + methodName.toUpperCase();

            String details = className + "." + methodName + "()";

            VisitLog log = new VisitLog(action, getIpAddress(), details);
            visitLogDAO.insert(log);

            System.out.println("[AOP] VisitLogAspect: Logged service access - " + details);

        } catch (SQLException e) {
            System.err.println("[AOP] Error logging service access: " + e.getMessage());
        }
    }

    /**
     * Helper: Build chi tiết từ arguments
     */
    private String buildDetailsFromArgs(String methodName, Object[] args) {
        StringBuilder details = new StringBuilder(methodName + "(");

        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    // Chỉ lấy giá trị đơn giản, không log toàn bộ object
                    if (args[i] instanceof String || args[i] instanceof Number) {
                        details.append(args[i]);
                    } else {
                        details.append(args[i].getClass().getSimpleName());
                    }

                    if (i < args.length - 1) {
                        details.append(", ");
                    }
                }
            }
        }
        details.append(")");

        return details.toString();
    }

    /**
     * Helper: Build chi tiết cho servlet
     */
    private String buildServletDetails(String servletName, String methodName, Object[] args) {
        StringBuilder details = new StringBuilder(servletName + "." + methodName);

        if (args != null && args.length > 0 && args[0] instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) args[0];
            String queryString = request.getQueryString();
            if (queryString != null && !queryString.isEmpty()) {
                details.append("?").append(queryString);
            }
        }

        return details.toString();
    }

    /**
     * Helper: Extract IP từ servlet arguments
     */
    private String extractIpFromServletArgs(Object[] args) {
        if (args != null && args.length > 0 && args[0] instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) args[0];
            return getClientIpAddress(request);
        }
        return getIpAddress();
    }

    /**
     * Helper: Lấy IP address của client từ request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Helper: Lấy IP address mặc định (dùng khi không có HttpServletRequest)
     */
    private String getIpAddress() {
        // Trong môi trường thực tế, có thể lấy từ ThreadLocal hoặc context
        return "SYSTEM";
    }

    /**
     * Helper: Kiểm tra có nên skip logging không
     */
    private boolean shouldSkipLogging(String className, String methodName) {
        // Skip các method getter đơn giản
        if (methodName.startsWith("get") && methodName.length() < 20) {
            return true;
        }

        // Skip statistics methods (đã có log riêng nếu cần)
        if (className.equals("StatisticsService")) {
            return true;
        }

        return false;
    }
}
