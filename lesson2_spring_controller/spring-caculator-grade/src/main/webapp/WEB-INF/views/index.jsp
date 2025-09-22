<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Calculator</title>
</head>
<body>
<h2>Caculator</h2>
<form action="calculate" method="post">
    <label>
        <input type="text" name="num1" placeholder="Enter number 1"/>
    </label>
    <label>
        <input type="text" name="num2" placeholder="Enter number 2"/>
    </label>
    <br><br>
    <button type="submit" name="operator" value="+">Addition(+)</button>
    <button type="submit" name="operator" value="-">Subtraction(-)</button>
    <button type="submit" name="operator" value="*">Multiplication(X)</button>
    <button type="submit" name="operator" value="/">Division(/)</button>
</form>
</body>
</html>
