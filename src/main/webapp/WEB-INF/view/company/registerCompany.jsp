<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 02/04/2023
  Time: 16:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank. Create your company account</title>
</head>
<body>

<h1>Create your company account</h1>

<form:form method="post" modelAttribute="company" action="/company/registerCompanyOwner">
    <form:hidden path="id"/>
    <form:hidden path="bankAccountByBankAccountId.id"/>

    <form:label path="name">Company name:</form:label>
    <form:input path="name" size="45" maxlength="45"/>
    <br/>
    <form:label path="bankAccountByBankAccountId.badgeByBadgeId">Currency:</form:label>
    <form:select path="bankAccountByBankAccountId.badgeByBadgeId"
                 items="${badgeList}"
                 itemValue="id"
                 itemLabel="name"
                 multiple="false"/>
    <br/>

    As a part of registration process you required to associate at least one manager to your bank account
    <form:button name="Register">Add a manager</form:button>
</form:form>

</body>
</html>
