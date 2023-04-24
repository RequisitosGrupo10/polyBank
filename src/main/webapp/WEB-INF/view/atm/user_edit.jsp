<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 01/04/2023
  Time: 16:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank - Editar datos</title>
</head>
<body>
<h1>Datos del cliente</h1>
<form:form modelAttribute="client" method="post" action="/atm/editarDatos">
    <form:hidden path="id"></form:hidden>
    <label for="dni">DNI:</label>
    <form:input path="dni" readonly="true" id="dni"></form:input><br>
    <label for="name">Name:</label>
    <form:input path="name" size="45" maxlength="45" id="name"></form:input><br>
    <form:hidden path="password"></form:hidden>
    <form:hidden path="salt"></form:hidden>
    <label for="surname">Surname:</label>
    <form:input path="surname" size="45" maxlength="45" id="surname"></form:input><br>

    <form:hidden path="creationDate"></form:hidden>

    <form:button>Save</form:button>
</form:form> <br>
<form action="/atm" method="get">
    <button>Go back</button>
</form>
</body>
</html>
