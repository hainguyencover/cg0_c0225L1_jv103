<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Thêm sinh viên</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body class="container mt-4">
<h2>Sửa sinh viên</h2>

<form:form modelAttribute="studentForm" method="post">

    <div class="mb-3">
        <label>MSSV</label>
        <!-- MSSV thường không cho sửa -->
        <form:input path="mssv" class="form-control" readonly="true"/>
    </div>

    <div class="mb-3">
        <label>Họ tên</label>
        <form:input path="hoTen" class="form-control"/>
        <form:errors path="hoTen" cssClass="text-danger"/>
    </div>

    <div class="mb-3">
        <label>GPA</label>
        <form:input path="diemTongKet" class="form-control"/>
        <form:errors path="diemTongKet" cssClass="text-danger"/>
    </div>

    <button type="submit" class="btn btn-primary">Cập nhật</button>
    <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">Hủy</a>
</form:form>
</body>
</html>
