<%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank - Employee Login</title>
</head>
<body>
<h1>Inicia sesión como empleado:</h1>
<form action="/employee/login" method="post">
    <label for="name">Nombre: </label>
    <input type="text" id="name"><br>
    <label for="password">Contraseña: </label>
    <input type="password" id="password"><br>
    <input type="submit">
</form>
</body>
</html>
