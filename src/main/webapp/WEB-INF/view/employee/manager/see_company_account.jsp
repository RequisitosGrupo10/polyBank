<%@ page import="com.taw.polybank.entity.RequestEntity" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="com.taw.polybank.entity.CompanyEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.ClientDTO" %>
<%@ page import="com.taw.polybank.dto.CompanyDTO" %><%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank - Company</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<div class="container">
    <h1>Company</h1>
    <p>Name<span>${company.name}</span></p>
</div>
</body>
</html>
