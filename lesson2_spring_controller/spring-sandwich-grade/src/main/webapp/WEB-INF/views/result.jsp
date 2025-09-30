<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Result</title>
</head>
<body>
<h2>Your selected condiments:</h2>

<c:if test="${not empty condiments}">
    <ul>
        <c:forEach var="c" items="${condiments}">
            <li>${c}</li>
        </c:forEach>
    </ul>
</c:if>

<c:if test="${empty condiments}">
    <p>No condiments selected!</p>
</c:if>

<a href="<c:url value='/'/>">Back</a>
</body>
</html>
