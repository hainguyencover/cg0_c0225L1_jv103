<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>Kết quả dịch</title>
</head>
<body>
<h2>Kết quả tra cứu</h2>
<p>
  Từ: <b>${word}</b><br/>
  Nghĩa: <b>${result}</b>
</p>
<a href="<c:url value='/'/>">Quay lại tra cứu</a>
</body>
</html>
