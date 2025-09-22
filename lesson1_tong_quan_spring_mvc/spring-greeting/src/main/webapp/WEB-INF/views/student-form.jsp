<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Thêm sinh viên</title></head>
<body>
<h2>Thêm sinh viên</h2>
<form action="${pageContext.request.contextPath}/students/add" method="post">
    <p>Mã số: <label>
        <input type="text" name="mssv" required />
    </label></p>
    <p>Họ tên: <label>
        <input type="text" name="hoTen" required />
    </label></p>
    <p>Điểm tổng kết: <label>
        <input type="number" step="0.01" name="diemTongKet" required />
    </label></p>
    <p><button type="submit">Lưu</button></p>
</form>
<p><a href="${pageContext.request.contextPath}/students">⬅ Quay lại danh sách</a></p>
</body>
</html>
