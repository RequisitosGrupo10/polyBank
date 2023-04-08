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
    <title>Polybank - Employee Sign up</title>
</head>
<body>
<h1>Registrar empleado:</h1>
<form:form action="/employee/register" method="post" modelAttribute="newEmployee">
    <label>DNI: </label><form:input path="dni" size="15" maxlength="15"/><br/>
    <label>Name: </label><form:input path="name" size="40" maxlength="15"/><br/>
    <form:button>Submit</form:button>
</form:form>
</body>
</html>
