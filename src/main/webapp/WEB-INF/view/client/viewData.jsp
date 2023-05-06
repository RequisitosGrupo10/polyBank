<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="com.taw.polybank.entity.BankAccountEntity" %><%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ClientEntity client = (ClientEntity) request.getAttribute("client");
%>
<html>
<head>
    <title>Polybank - Client</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<h1>Client data:</h1>
<table class="table table-bordered">
    <tr>
        <th scope="row">DNI:</th>
        <td><%=client.getDni()%></td>
    </tr>
    <tr>
        <th scope="row">Name:</th>
        <td><%=client.getName()%></td>
    </tr>
    <tr>
        <th scope="row">Surname:</th>
        <td><%=client.getSurname()%></td>
    </tr>
    <tr>
        <th scope="row">Sign-up date:</th>
        <td><%=client.getCreationDate()%></td>
    </tr>
    <tr>
        <th scope="row">Bank accounts:</th>
        <td>
            <% for (BankAccountEntity bank : client.getBankAccountsById()) { %>
            <a href="/client/account?id=<%=bank.getId()%>"><%=bank.getIban()%></a><br>
            <% } %>
        </td>
    </tr>
</table>
<a class="btn btn-primary" href='/client/edit?id=<%=client.getId()%>'>Edit my data</a>

<%-- Don't Remove, button for client assistence --%>
<br>
<h3>Assistance</h3>
<form action="/client/assistance" method="get">
    <button>Assistance</button>
</form>
</body>
</html>
