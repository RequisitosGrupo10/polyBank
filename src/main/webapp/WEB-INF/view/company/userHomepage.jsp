<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="com.taw.polybank.entity.BankAccountEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.taw.polybank.entity.CompanyEntity" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 04/04/2023
  Time: 12:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome ${client.name}</title>
    <style>
        .prettyButton {
            margin-right: 1rem;
            padding: 0.2rem;
            width: fit-content;
            height: 1.5rem;
            border: solid 1px black;
            border-radius: 5px;
            background-color: #ecffa8;
            text-decoration:none;
        }
    </style>
</head>
<body>

<h1>Welcome ${client.name} currently you representing ${company.name} </h1>



</body>
</html>
