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
</head>
<body>
<h1>Add representative to ${company.name}</h1>

<form:form method="post" modelAttribute="authorizedAccount" action="/company/saveNewCompany">
    <form:hidden path="clientByClientId.id"/>
    <form:hidden path="bankAccountByBankAccountId.id"/>

    <form:label path="clientByClientId.dni">Representative's ID:</form:label>
    <form:input path="clientByClientId.dni" size="45" maxlength="45"/>
    <br/>
    <form:label path="clientByClientId.name">Representative's name:</form:label>
    <form:input path="clientByClientId.name" size="45" maxlength="45"/>
    <br/>
    <form:label path="clientByClientId.surname">Representative's surname:</form:label>
    <form:input path="clientByClientId.surname" size="45" maxlength="45"/>
    <br/>
    <form:label path="clientByClientId.password">Representative's password:</form:label>
    <form:input path="clientByClientId.password" size="20" maxlength="64"/>
    <br/>
    <form:button name="Add representative">Add representative</form:button>

</form:form>

</body>
</html>
