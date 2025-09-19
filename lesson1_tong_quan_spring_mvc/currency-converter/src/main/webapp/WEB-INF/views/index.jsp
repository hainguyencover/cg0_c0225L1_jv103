<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>USD to VND Converter</title>
</head>
<body>
<h2>Chuyển đổi USD → VND</h2>
<form action="convert" method="post">
    Nhập số USD: <label>
    <input type="number" step="1" name="usd" required/>
</label>
    <button type="submit">Chuyển đổi</button>
</form>
</body>
</html>
