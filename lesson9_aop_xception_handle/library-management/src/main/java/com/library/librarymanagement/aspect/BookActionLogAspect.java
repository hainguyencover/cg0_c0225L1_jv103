package com.library.librarymanagement.aspect;

import com.library.librarymanagement.dao.LogBookActionDAO;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.entity.LogBookAction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.sql.SQLException;

@Aspect
public class BookActionLogAspect {
    private LogBookActionDAO logBookActionDAO;

    public BookActionLogAspect() {
        this.logBookActionDAO = new LogBookActionDAO();
    }

    /**
     * Pointcut cho các method mượn sách trong BorrowService
     */
    @Pointcut("execution(* com.library.librarymanagement.service.BorrowService.borrowBook(..))")
    public void borrowBookPointcut() {
    }

    /**
     * Pointcut cho các method trả sách trong BorrowService
     */
    @Pointcut("execution(* com.library.librarymanagement.service.BorrowService.returnBook(..))")
    public void returnBookPointcut() {
    }

    /**
     * Pointcut cho các method tạo sách trong BookService
     */
    @Pointcut("execution(* com.library.librarymanagement.service.BookService.createBook(..))")
    public void createBookPointcut() {
    }

    /**
     * Pointcut cho các method cập nhật sách trong BookService
     */
    @Pointcut("execution(* com.library.librarymanagement.service.BookService.updateBook(..))")
    public void updateBookPointcut() {
    }

    /**
     * Pointcut cho các method xóa sách trong BookService
     */
    @Pointcut("execution(* com.library.service.BookService.deleteBook(..))")
    public void deleteBookPointcut() {
    }

    /**
     * Log sau khi mượn sách thành công
     * Method này sẽ được gọi sau khi borrowBook() return thành công
     */
    @AfterReturning(
            pointcut = "borrowBookPointcut()",
            returning = "result"
    )
    public void logAfterBorrow(JoinPoint joinPoint, Object result) {
        try {
            if (result instanceof BorrowRecord) {
                BorrowRecord record = (BorrowRecord) result;

                Object[] args = joinPoint.getArgs();
                Integer bookId = (Integer) args[0];
                String borrowerName = (String) args[1];

                System.out.println("[AOP] BookActionLogAspect: Logged BORROW action for book ID " +
                        bookId + " by " + borrowerName + " (Code: " + record.getBorrowCode() + ")");

                // Note: Actual logging is already done in BorrowService.borrowBook()
                // This aspect is for additional monitoring/auditing if needed
            }
        } catch (Exception e) {
            System.err.println("[AOP] Error in logAfterBorrow: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Log sau khi trả sách thành công
     */
    @AfterReturning(
            pointcut = "returnBookPointcut()",
            returning = "result"
    )
    public void logAfterReturn(JoinPoint joinPoint, Object result) {
        try {
            if (result instanceof BorrowRecord) {
                BorrowRecord record = (BorrowRecord) result;

                Object[] args = joinPoint.getArgs();
                String borrowCode = (String) args[0];

                System.out.println("[AOP] BookActionLogAspect: Logged RETURN action for borrow code " +
                        borrowCode + " (Book: " + record.getBookTitle() + ")");

                // Note: Actual logging is already done in BorrowService.returnBook()
                // This aspect is for additional monitoring/auditing if needed
            }
        } catch (Exception e) {
            System.err.println("[AOP] Error in logAfterReturn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Log sau khi tạo sách thành công
     */
    @AfterReturning(
            pointcut = "createBookPointcut()",
            returning = "result"
    )
    public void logAfterCreate(JoinPoint joinPoint, Object result) {
        try {
            if (result instanceof Book) {
                Book book = (Book) result;

                System.out.println("[AOP] BookActionLogAspect: Logged CREATE action for book '" +
                        book.getTitle() + "' (ID: " + book.getBookId() +
                        ", Quantity: " + book.getTotalQuantity() + ")");

                // Note: Actual logging is already done in BookService.createBook()
                // This aspect is for additional monitoring/auditing if needed
            }
        } catch (Exception e) {
            System.err.println("[AOP] Error in logAfterCreate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Log sau khi cập nhật sách thành công
     */
    @AfterReturning(
            pointcut = "updateBookPointcut()",
            returning = "result"
    )
    public void logAfterUpdate(JoinPoint joinPoint, Object result) {
        try {
            if (result instanceof Boolean && (Boolean) result) {
                Object[] args = joinPoint.getArgs();
                if (args.length > 0 && args[0] instanceof Book) {
                    Book book = (Book) args[0];

                    System.out.println("[AOP] BookActionLogAspect: Logged UPDATE action for book ID " +
                            book.getBookId() + " ('" + book.getTitle() + "')");

                    // Note: Actual logging is already done in BookService.updateBook()
                    // This aspect is for additional monitoring/auditing if needed
                }
            }
        } catch (Exception e) {
            System.err.println("[AOP] Error in logAfterUpdate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Log sau khi xóa sách thành công
     */
    @AfterReturning(
            pointcut = "deleteBookPointcut()",
            returning = "result"
    )
    public void logAfterDelete(JoinPoint joinPoint, Object result) {
        try {
            if (result instanceof Boolean && (Boolean) result) {
                Object[] args = joinPoint.getArgs();
                if (args.length > 0 && args[0] instanceof Integer) {
                    Integer bookId = (Integer) args[0];

                    System.out.println("[AOP] BookActionLogAspect: Logged DELETE action for book ID " + bookId);

                    // Note: Actual logging is already done in BookService.deleteBook()
                    // This aspect is for additional monitoring/auditing if needed
                }
            }
        } catch (Exception e) {
            System.err.println("[AOP] Error in logAfterDelete: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method để log thủ công (nếu cần)
     */
    private void saveLog(LogBookAction log) {
        try {
            logBookActionDAO.insert(log);
        } catch (SQLException e) {
            System.err.println("[AOP] Failed to save log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
