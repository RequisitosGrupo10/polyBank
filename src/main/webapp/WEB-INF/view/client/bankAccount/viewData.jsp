<%@ page import="com.taw.polybank.entity.BankAccountEntity" %>
<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="com.taw.polybank.entity.AuthorizedAccountEntity" %>
<%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BankAccountEntity account = (BankAccountEntity) request.getAttribute("account");
%>
<html>
<head>
    <title>Polybank - Bank Account</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<h1>Account data:</h1>
<table class="table table-bordered">
    <tr>
        <th scope="row">IBAN:</th>
        <td><%=account.getIban()%></td>
    </tr>
    <tr>
        <th scope="row">Balance:</th>
        <td><%=account.getBalance()%> <%=account.getBadgeByBadgeId().getName()%></td>
    </tr>
    <tr>
        <th scope="row">Owner:</th>
        <td><%=account.getClientByClientId().getName()%> <%=account.getClientByClientId().getSurname()%></td>
    </tr>
</table>
<a class="btn btn-primary" href='/client/account/moneyExchange?id=<%=account.getId()%>'>Money exchange</a>
<a class="btn btn-primary" href=''>Make a transaction</a>
<a class="btn btn-primary" href=''>Operation history</a>
<a class="btn btn-danger" href='/client/view'>Return</a>
</body>
</html>
