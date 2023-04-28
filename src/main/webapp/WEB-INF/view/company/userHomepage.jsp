<%@ page import="com.taw.polybank.dto.ClientDTO" %>
<%--
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

<c:if test="${message != null}" >
    <h4 style="color:red;">
            ${message}
    </h4>
</c:if>

<h2>Available actions:</h2>

<a class="prettyButton" href="/company/user/addRepresentative">Add representative</a>
<a class="prettyButton" href="/company/user/editMyData">Modify my data</a>
<a class="prettyButton" href="/company/user/editCompanyData">Modify company data</a>
<a class="prettyButton" href="/company/user/listAllRepresentatives">Show all representatives</a>
<a class="prettyButton" href="/company/user/newTransfer">New Transfer</a>
<a class="prettyButton" href="/company/user/moneyExchange">Exchange money</a>
<a class="prettyButton" href="/company/user/operationHistory">Operation history</a>


</body>
</html>
