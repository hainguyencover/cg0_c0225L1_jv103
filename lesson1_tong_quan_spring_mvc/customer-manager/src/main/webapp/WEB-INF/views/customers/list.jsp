<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
  <title>Customers List</title>
  <style>
    table {
      border: 1px solid #000;
      border-collapse: collapse;
    }

    th, td {
      border: 1px dotted #555;
      padding: 8px;
      text-align: left;
    }
  </style>
</head>
<body>
<h1>Customers List</h1>
<p>There are ${customers.size()} customer(s) in list.</p>
<table>
  <caption>Customers List</caption>
  <thead>
  <tr>
    <th>Id</th>
    <th>Name</th>
    <th>Email</th>
    <th>Address</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="c" items="${customers}">
    <tr>
      <td>
        <c:out value="${c.id}"/>
      </td>
      <td>
        <a href="${pageContext.request.contextPath}/customers/detail?id=${c.id}">${c.name}</a>
      </td>
      <td>
        <c:out value="${c.email}"/>
      </td>
      <td>
        <c:out value="${c.address}"/>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>
</body>
</html>
