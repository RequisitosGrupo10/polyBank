<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: panva
  Date: 05/04/2023
  Time: 16:25
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="com.taw.polybank.entity.CompanyEntity" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add new Representative to ${company.name}</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>
<jsp:include page="corporateHeader.jsp"/>
<h1>Add new Representative to ${company.name}</h1>

<form:form action="/company/user/saveRepresenative" modelAttribute="client" method="post">
    <form:hidden path="id"/>

    <form:label path="name">Representative's name:</form:label>
    <form:input path="name" size="45" maxlength="45"/>
    <br/>
    <form:label path="surname">Representative's surname:</form:label>
    <form:input path="surname" size="45" maxlength="45"/>
    <br/>
    <form:label path="dni">Representative's ID:</form:label>
    <form:input path="dni" size="45" maxlength="45"/>
    <br/>
    <form:label path="password">Representative's password:</form:label>
    <form:password path="password" size="20" maxlength="64"/>
    <br/>
    <form:button name="Add representative">Add representative</form:button>
</form:form>

</body>
</html>
