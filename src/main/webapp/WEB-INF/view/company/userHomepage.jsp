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
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>

<jsp:include page="corporateHeader.jsp" />

<h2>Available actions:</h2>

<a class="prettyButton" href="/company/addRepresentative">Add representative</a>
<a class="prettyButton" href="/company/editMyData">Modify my data</a>
<a class="prettyButton" href="/company/listAllRepresentatives">Show all representatives</a>
<a class="prettyButton" href="/company/newTransfer">New Transfer</a>
<a class="prettyButton" href="/company/moneyExchange">Exchange money</a>
<a class="prettyButton" href="/company/operationHistory">Operation history</a>


</body>
</html>
