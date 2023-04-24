<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
    <title>Polybank - Cliente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<h1>Datos del cliente:</h1>
<form:form action="/client/save" modelAttribute="client" method="post">
    Nombre: <form:input path="name" size="30" maxlength="30"  /><br/>
    Apellidos: <form:input path="surname" size="40"  maxlength="40"/> <br/>
    Contraseña: <form:input path="password" type="password" size="30" maxlength="30"/>
    <form:button>Guardar</form:button>
</form:form>
</body>
</html>
