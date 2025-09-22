<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Danh sách sinh viên</title></head>
<body>
<h2>Danh sách sinh viên</h2>
<table border="1" cellpadding="5">
    <tr>
        <th>Mã số</th>
        <th>Họ tên</th>
        <th>Điểm tổng kết</th>
    </tr>
    <c:forEach var="s" items="${students}">
        <tr>
            <td><c:out value="${s.mssv}" /></td>
            <td><c:out value="${s.hoTen}" /></td>
            <td><c:out value="${s.diemTongKet}" /></td>
        </tr>
    </c:forEach>
</table>
<p><a href="${pageContext.request.contextPath}/students/add">➕ Thêm sinh viên</a></p>
</body>
</html>
