<%@ page import="com.taw.polybank.entity.RequestEntity" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="com.taw.polybank.entity.CompanyEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.ClientDTO" %>
<%@ page import="com.taw.polybank.dto.CompanyDTO" %>
<%@ page import="com.taw.polybank.dto.BankAccountDTO" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank - Employee Check Accounts</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<%
    List<BankAccountDTO> bankAccountDTOS = (List<BankAccountDTO>) request.getAttribute("inactive");
%>
<jsp:include page="../components/navbar.jsp"></jsp:include>
<div class="container">
    <h1>Inactive Bank Accounts</h1>
    <table class="table">
        <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">IBAN</th>
                <th scope="col">Balance</th>
                <th scope="col">Link</th>
            </tr>
        </thead>
        <tbody>
        <% if (bankAccountDTOS != null) { %>
        <% for (BankAccountDTO bankAccountDTO : bankAccountDTOS ) { %>
        <tr>
            <td><%= bankAccountDTO.getId()%></td>
            <td><%= bankAccountDTO.getIban()%></td>
            <td><%= bankAccountDTO.getBalance()%></td>
            <td><a href="/employee/manager/accounts/bank/block/<%= bankAccountDTO.getId()%>">Block Account</a></td>
        </tr>
        <% } %>
        <% } %>
        </tbody>
    </table>
</div>
</body>
</html>
