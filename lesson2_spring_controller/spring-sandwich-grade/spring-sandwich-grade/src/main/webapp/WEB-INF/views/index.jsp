<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sandwich Condiments</title>
</head>
<body>
<h2>Sandwich Condiments</h2>
<form action="save" method="post">
    <label>
        <input type="checkbox" name="condiment" value="Lettuce">
    </label> Lettuce
    <label>
        <input type="checkbox" name="condiment" value="Tomato">
    </label> Tomato
    <label>
        <input type="checkbox" name="condiment" value="Mustard">
    </label> Mustard
    <label>
        <input type="checkbox" name="condiment" value="Sprouts">
    </label> Sprouts
    <br><br>
    <input type="submit" value="Save"/>
</form>
</body>
</html>
