package com.library.librarymanagement.aspect;

import com.library.librarymanagement.dao.VisitLogDAO;
import com.library.librarymanagement.entity.VisitLog;
import com.library.librarymanagement.exception.BookNotAvailableException;
import com.library.librarymanagement.exception.InvalidBorrowCodeException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.sql.SQLException;

@Aspect
public class ErrorLogAspect {
    private VisitLogDAO visitLogDAO;

    public ErrorLogAspect() {
        this.visitLogDAO = new VisitLogDAO();
    }

    /**
     * Pointcut cho tất cả method trong Service layer
     */
    @Pointcut("execution(* com.library.librarymanagement.service.*.*(..))")
    public void serviceLayerPointcut() {
    }

    /**
     * Pointcut cho tất cả method trong DAO layer
     */
    @Pointcut("execution(* com.library.librarymanagement.dao.*.*(..))")
    public void daoLayerPointcut() {
    }

    /**
     * Pointcut cho tất cả method trong Servlet layer
     */
    @Pointcut("execution(* com.library.librarymanagement.controller.*.*(..))")
    public void servletLayerPointcut() {
    }

    /**
     * Log tất cả exception trong Service layer
     */
    @AfterThrowing(
            pointcut = "serviceLayerPointcut()",
            throwing = "exception"
    )
    public void logServiceLayerException(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String location = className + "." + methodName + "()";

        logException(location, exception, joinPoint.getArgs());
    }

    /**
     * Log tất cả exception trong DAO layer
     */
    @AfterThrowing(
            pointcut = "daoLayerPointcut()",
            throwing = "exception"
    )
    public void logDaoLayerException(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String location = className + "." + methodName + "()";

        logException(location, exception, joinPoint.getArgs());
    }

    /**
     * Log tất cả exception trong Servlet layer
     */
    @AfterThrowing(
            pointcut = "servletLayerPointcut()",
            throwing = "exception"
    )
    public void logServletLayerException(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String location = className + "." + methodName + "()";

        logException(location, exception, joinPoint.getArgs());
    }

    /**
     * Log Business Exception riêng - BookNotAvailableException
     */
    @AfterThrowing(
            pointcut = "serviceLayerPointcut()",
            throwing = "exception"
    )
    public void logBookNotAvailableException(JoinPoint joinPoint, BookNotAvailableException exception) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        String action = "ERROR_BOOK_NOT_AVAILABLE";
        String details = buildErrorDetails(className, methodName, exception, joinPoint.getArgs());

        try {
            VisitLog log = new VisitLog(action, "SYSTEM", details);
            visitLogDAO.insert(log);

            System.err.println("[AOP] ErrorLogAspect: " + action + " - " + details);

        } catch (SQLException e) {
            System.err.println("[AOP] Failed to log BookNotAvailableException: " + e.getMessage());
        }
    }

    /**
     * Log Business Exception riêng - InvalidBorrowCodeException
     */
    @AfterThrowing(
            pointcut = "serviceLayerPointcut()",
            throwing = "exception"
    )
    public void logInvalidBorrowCodeException(JoinPoint joinPoint, InvalidBorrowCodeException exception) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        String action = "ERROR_INVALID_BORROW_CODE";
        String details = buildErrorDetails(className, methodName, exception, joinPoint.getArgs());

        try {
            VisitLog log = new VisitLog(action, "SYSTEM", details);
            visitLogDAO.insert(log);

            System.err.println("[AOP] ErrorLogAspect: " + action + " - " + details);

        } catch (SQLException e) {
            System.err.println("[AOP] Failed to log InvalidBorrowCodeException: " + e.getMessage());
        }
    }

    /**
     * Log SQLException riêng
     */
    @AfterThrowing(
            pointcut = "serviceLayerPointcut() || daoLayerPointcut()",
            throwing = "exception"
    )
    public void logSQLException(JoinPoint joinPoint, SQLException exception) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        String action = "ERROR_DATABASE";
        String details = buildErrorDetails(className, methodName, exception, joinPoint.getArgs());

        try {
            VisitLog log = new VisitLog(action, "SYSTEM", details);
            visitLogDAO.insert(log);

            System.err.println("[AOP] ErrorLogAspect: " + action + " - " + details);
            System.err.println("[AOP] SQL Error Code: " + exception.getErrorCode());
            System.err.println("[AOP] SQL State: " + exception.getSQLState());

        } catch (SQLException e) {
            System.err.println("[AOP] Failed to log SQLException: " + e.getMessage());
            // Không thể log vào database, chỉ in ra console
        }
    }

    /**
     * Log IllegalArgumentException (validation errors)
     */
    @AfterThrowing(
            pointcut = "serviceLayerPointcut()",
            throwing = "exception"
    )
    public void logIllegalArgumentException(JoinPoint joinPoint, IllegalArgumentException exception) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        String action = "ERROR_VALIDATION";
        String details = buildErrorDetails(className, methodName, exception, joinPoint.getArgs());

        try {
            VisitLog log = new VisitLog(action, "SYSTEM", details);
            visitLogDAO.insert(log);

            System.err.println("[AOP] ErrorLogAspect: " + action + " - " + details);

        } catch (SQLException e) {
            System.err.println("[AOP] Failed to log IllegalArgumentException: " + e.getMessage());
        }
    }

    /**
     * Core method để log exception
     */
    private void logException(String location, Throwable exception, Object[] args) {
        String action = "ERROR_" + exception.getClass().getSimpleName().toUpperCase();
        String details = buildErrorDetailsGeneric(location, exception, args);

        try {
            VisitLog log = new VisitLog(action, "SYSTEM", details);
            visitLogDAO.insert(log);

            System.err.println("[AOP] ErrorLogAspect: Exception caught at " + location);
            System.err.println("[AOP] Exception type: " + exception.getClass().getName());
            System.err.println("[AOP] Message: " + exception.getMessage());

            // Log stack trace nếu là lỗi nghiêm trọng
            if (isSevereException(exception)) {
                System.err.println("[AOP] Stack trace:");
                exception.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("[AOP] Failed to log exception: " + e.getMessage());
            // Fallback: log ra file hoặc console
            System.err.println("[AOP] Original exception: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Helper: Build chi tiết lỗi
     */
    private String buildErrorDetails(String className, String methodName,
                                     Throwable exception, Object[] args) {
        StringBuilder details = new StringBuilder();

        details.append("Location: ").append(className).append(".").append(methodName).append("() | ");
        details.append("Error: ").append(exception.getClass().getSimpleName()).append(" | ");
        details.append("Message: ").append(exception.getMessage());

        if (args != null && args.length > 0) {
            details.append(" | Args: ");
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
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

        return details.toString();
    }

    /**
     * Helper: Build chi tiết lỗi generic
     */
    private String buildErrorDetailsGeneric(String location, Throwable exception, Object[] args) {
        StringBuilder details = new StringBuilder();

        details.append("Location: ").append(location).append(" | ");
        details.append("Error: ").append(exception.getClass().getSimpleName()).append(" | ");
        details.append("Message: ").append(exception.getMessage());

        if (exception.getCause() != null) {
            details.append(" | Cause: ").append(exception.getCause().getMessage());
        }

        return details.toString();
    }

    /**
     * Helper: Kiểm tra exception có nghiêm trọng không
     */
    private boolean isSevereException(Throwable exception) {
        // SQLException, NullPointerException, etc. là severe
        return exception instanceof SQLException ||
                exception instanceof NullPointerException ||
                exception instanceof RuntimeException;
    }

    /**
     * Helper: Format stack trace thành string (nếu cần log vào DB)
     */
    private String getStackTraceAsString(Throwable exception) {
        StringBuilder sb = new StringBuilder();
        sb.append(exception.toString()).append("\n");

        StackTraceElement[] elements = exception.getStackTrace();
        for (int i = 0; i < Math.min(elements.length, 5); i++) {
            sb.append("\tat ").append(elements[i].toString()).append("\n");
        }

        if (elements.length > 5) {
            sb.append("\t... ").append(elements.length - 5).append(" more");
        }

        return sb.toString();
    }
}
