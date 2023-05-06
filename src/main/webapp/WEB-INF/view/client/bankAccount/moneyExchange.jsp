<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.taw.polybank.entity.BankAccountEntity" %><%--
<%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 07/04/2023
  Time: 23:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank - Bank Account</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<%
    BankAccountEntity account = (BankAccountEntity) request.getAttribute("account");
%>
<div id="transactionWindow">
    <h1>Money Exchange</h1>
    <p>Balance: <%=account.getBalance()%> <%=account.getBadgeByBadgeId().getName()%></p>

    <form:form modelAttribute="badge" method="post" action="/client/account/makeExchange?id=${account.getId()}">
        <form:label path="id">Desired currency: </form:label>
        <form:select path="id" items="${badgeList}" itemLabel="name" itemValue="id"/>
        <form:button class="btn btn-primary">Exchange</form:button>
    </form:form>
</div>
</body>
</html>
