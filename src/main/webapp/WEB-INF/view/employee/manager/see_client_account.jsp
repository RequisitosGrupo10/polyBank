<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.taw.polybank.entity.RequestEntity" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="com.taw.polybank.entity.CompanyEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.ClientDTO" %>
<%@ page import="com.taw.polybank.dto.CompanyDTO" %>
<%@ page import="com.taw.polybank.dto.TransactionDTO" %><%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank - Client</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<jsp:include page="../components/navbar.jsp"></jsp:include>
<div class="container">
    <h1>Client</h1>
    <ul>
        <li><p>DNI - <span>${client.dni}</span></p></li>
        <li><p>Name - <span>${client.name}</span></p></li>
        <li><p>Surname - <span>${client.surname}</span></p></li>
        <li><p>Creation Date - <span>${client.creationDate}</span></p></li>
    </ul>
    <h1>Transactions</h1>
    <jsp:include page="../components/transaction_filter.jsp"></jsp:include>
    <jsp:include page="../components/transaction_table.jsp"></jsp:include>
</div>
</body>
</html>
