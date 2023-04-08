<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 02/04/2023
  Time: 18:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${company.name}. Add representative</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>
<h1>Add representative to ${company.name}</h1>

<form:form method="post" modelAttribute="client" action="/company/saveNewCompany">
    <form:hidden path="id"/>

    <form:label path="name">Representative's name:</form:label>
    <form:input path="name" size="45" maxlength="45" class="formElement"/>
    <br/>
    <form:label path="surname">Representative's surname:</form:label>
    <form:input path="surname" size="45" maxlength="45" class="formElement" />
    <br/>
    <form:label path="dni">Representative's ID:</form:label>
    <form:input path="dni" size="45" maxlength="45" class="formElement" />
    <br/>
    <form:label path="password">Representative's password:</form:label>
    <form:password path="password" size="20" maxlength="64" class="formElement" />
    <br/>
    <form:button class="prettyButton" name="Add representative">Add representative</form:button>

</form:form>

</body>
</html>
