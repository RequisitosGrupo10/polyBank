<%@ page import="com.taw.polybank.entity.RequestEntity" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.RequestDTO" %><%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank - Employee Requests</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<%
    List<RequestDTO> requests = (List<RequestDTO>) request.getAttribute("requests");
%>
<jsp:include page="../components/navbar.jsp"></jsp:include>
<div class="container">
<h1>Pending requests:</h1>
<% if (requests != null) { %>
    <table class="table">
        <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Cuenta que solicita</th>
                <th scope="col">IBAN</th>
                <th scope="col">Timestamp</th>
                <th scope="col">Descripción</th>
                <th scope="col">Aprobar/Denegar</th>
            </tr>
        </thead>
        <tbody>
            <% for (RequestDTO requestDTO : requests ) { %>
            <tr>
                <td><%= requestDTO.getId()%></td>
                <td><%= requestDTO.getClientByClientId().getName()%></td>
                <td><%= requestDTO.getBankAccountByBankAccountId().getIban()%></td>
                <td><%= requestDTO.getTimestamp()%></td>
                <td><%= requestDTO.getDescription()%></td>
                <td>
                    <a href="/employee/manager/approve/<%=requestDTO.getId()%>">Aprobar</a>
                    </br>
                    <a href="/employee/manager/deny/<%=requestDTO.getId()%>">Denegar</a>
                </td>
            </tr>
            <% } %>
        </tbody>
    </table>
<% } %>
</div>
</body>
</html>
