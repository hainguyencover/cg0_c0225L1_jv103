<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Danh sách sinh viên</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">

<h2 class="mb-3">Danh sách sinh viên</h2>

<!-- Flash message -->
<c:if test="${not empty message}">
    <div class="alert alert-success" role="alert">
        <c:out value="${message}"/>
    </div>
</c:if>

<!-- Toolbar -->
<div class="d-flex justify-content-between align-items-center mb-3">
    <form class="row g-2" method="get" action="${pageContext.request.contextPath}/students">
        <div class="col-auto">
            <label>
                <input type="text" name="q" class="form-control" placeholder="Tìm MSSV hoặc Họ tên"
                       value="${fn:escapeXml(q)}"/>
            </label>
        </div>
        <div class="col-auto">
            <label>
                <select name="sort" class="form-select">
                    <option value="mssv" ${sort == 'mssv' ? 'selected' : ''}>Mã số</option>
                    <option value="name" ${sort == 'name' ? 'selected' : ''}>Họ tên</option>
                    <option value="gpa" ${sort == 'gpa' ? 'selected' : ''}>Điểm</option>
                </select>
            </label>
        </div>
        <div class="col-auto">
            <label>
                <select name="dir" class="form-select">
                    <option value="asc"  ${dir == 'asc'  ? 'selected' : ''}>Tăng dần</option>
                    <option value="desc" ${dir == 'desc' ? 'selected' : ''}>Giảm dần</option>
                </select>
            </label>
        </div>
        <div class="col-auto">
            <label>
                <select name="size" class="form-select">
                    <option value="5"  ${size == 5  ? 'selected' : ''}>5</option>
                    <option value="10" ${size == 10 ? 'selected' : ''}>10</option>
                    <option value="20" ${size == 20 ? 'selected' : ''}>20</option>
                </select>
            </label>
        </div>
        <div class="col-auto">
            <button type="submit" class="btn btn-primary">Lọc</button>
        </div>
    </form>

    <a href="${pageContext.request.contextPath}/students/add" class="btn btn-success">
        Thêm sinh viên
    </a>
</div>

<!-- Table -->
<table class="table table-striped table-bordered">
    <thead class="table-dark">
    <tr>
        <th>Mã số</th>
        <th>Họ tên</th>
        <th>Điểm tổng kết</th>
        <th>Hạng</th>
        <th>Hành động</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="s" items="${students}">
        <tr>
            <td><c:out value="${s.mssv}"/></td>
            <td><c:out value="${s.hoTen}"/></td>
            <td><fmt:formatNumber value="${s.diemTongKet}" minFractionDigits="0" maxFractionDigits="2"/></td>
            <td><span class="badge bg-info text-dark"><c:out value="${s.xepLoai.label}"/></span></td>
            <td>
                <a href="${pageContext.request.contextPath}/students/${s.mssv}" class="btn btn-sm btn-info">Chi tiết</a>
                <a href="${pageContext.request.contextPath}/students/${s.mssv}/edit" class="btn btn-sm btn-warning">Sửa</a>
                <form action="${pageContext.request.contextPath}/students/${s.mssv}/delete"
                      method="post" style="display:inline"
                      onsubmit="return confirm('Bạn muốn xóa sinh viên ${s.hoTen}?');">
                    <button type="submit" class="btn btn-sm btn-danger">Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty students}">
        <tr><td colspan="5" class="text-center">Không có sinh viên nào.</td></tr>
    </c:if>
    </tbody>
</table>

<!-- Pagination -->
<nav>
    <ul class="pagination">
        <c:if test="${page > 1}">
            <li class="page-item">
                <a class="page-link" href="?q=${q}&sort=${sort}&dir=${dir}&page=${page-1}&size=${size}">« Prev</a>
            </li>
        </c:if>

        <c:forEach var="i" begin="1" end="${totalPages}">
            <li class="page-item ${i == page ? 'active' : ''}">
                <a class="page-link" href="?q=${q}&sort=${sort}&dir=${dir}&page=${i}&size=${size}">${i}</a>
            </li>
        </c:forEach>

        <c:if test="${page < totalPages}">
            <li class="page-item">
                <a class="page-link" href="?q=${q}&sort=${sort}&dir=${dir}&page=${page+1}&size=${size}">Next »</a>
            </li>
        </c:if>
    </ul>
</nav>

<p class="text-muted">Trang ${page} / ${totalPages} — Tổng ${total} sinh viên</p>

<!-- Bootstrap JS (optional for modal/alert) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
