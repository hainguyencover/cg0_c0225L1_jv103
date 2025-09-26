<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Chi tiết sinh viên</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">

<h2 class="mb-4">Thông tin chi tiết sinh viên</h2>

<c:if test="${not empty message}">
    <div class="alert alert-info">${message}</div>
</c:if>

<table class="table table-bordered">
    <tr>
        <th style="width: 200px">Mã số sinh viên</th>
        <td><c:out value="${student.mssv}"/></td>
    </tr>
    <tr>
        <th>Họ tên</th>
        <td><c:out value="${student.hoTen}"/></td>
    </tr>
    <tr>
        <th>Điểm tổng kết</th>
        <td><fmt:formatNumber value="${student.diemTongKet}" minFractionDigits="0" maxFractionDigits="2"/></td>
    </tr>
    <tr>
        <th>Xếp loại</th>
        <td><c:out value="${student.xepLoai.label}"/></td>
    </tr>
</table>

<div class="mt-3">
    <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">Quay lại</a>
    <a href="${pageContext.request.contextPath}/students/${student.mssv}/edit" class="btn btn-warning">Sửa</a>
    <form action="${pageContext.request.contextPath}/students/${student.mssv}/delete"
          method="post" style="display:inline"
          onsubmit="return confirm('Bạn chắc chắn muốn xóa sinh viên ${student.hoTen}?');">
        <button type="submit" class="btn btn-danger">Xóa</button>
    </form>
</div>

</body>
</html>
