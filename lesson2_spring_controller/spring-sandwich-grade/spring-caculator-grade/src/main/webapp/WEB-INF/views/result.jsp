<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Result</title>
</head>
<body>
<h2>Calculation Result</h2>

<p>
    Operation: ${message} <br>
    ${num1} ${operator} ${num2} = ${result}
</p>

<a href="<c:url value='/'/>">Back</a>
</body>
</html>
