<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Thêm sinh viên</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body class="container mt-4">

<h2>Thêm sinh viên</h2>

<form:form modelAttribute="studentForm" method="post" class="mt-3">
    <div class="mb-3">
        <label class="form-label">Mã số (MSSV)</label>
        <form:input path="mssv" cssClass="form-control"/>
        <form:errors path="mssv" cssClass="text-danger"/>
    </div>

    <div class="mb-3">
        <label class="form-label">Họ tên</label>
        <form:input path="hoTen" cssClass="form-control"/>
        <form:errors path="hoTen" cssClass="text-danger"/>
    </div>

    <div class="mb-3">
        <label class="form-label">Điểm tổng kết</label>
        <form:input path="diemTongKet" type="number" step="0.01" cssClass="form-control"/>
        <form:errors path="diemTongKet" cssClass="text-danger"/>
    </div>

    <button type="submit" class="btn btn-primary">Lưu</button>
    <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">Hủy</a>
</form:form>

</body>
</html>
