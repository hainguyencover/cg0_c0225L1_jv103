<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
  <title>Kết quả</title>
</head>
<body>
<h2>Kết quả</h2>
<p><fmt:formatNumber value="${usd}" type="number" groupingUsed="true"/> USD = <b><fmt:formatNumber value="${vnd}" type="number" groupingUsed="true"/> VNĐ</b></p>
<a href="/">Quay lại</a>
</body>
</html>
