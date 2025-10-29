package com.example.springsecurityjwt.jwt;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    // Phương thức handle sẽ được gọi khi người dùng bị từ chối quyền truy cập
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exc)
            throws IOException {
        // Thiết lập mã trạng thái HTTP trả về là 403 Forbidden (cấm truy cập)
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Gửi phản hồi dạng text cho client với nội dung thông báo "Access Denied!"
        // Điều này giúp client biết được rằng quyền truy cập bị từ chối
        response.getWriter().write("Access Denied!");
    }
}
