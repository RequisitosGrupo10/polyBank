<%@ page import="com.taw.polybank.entity.RequestEntity" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="com.taw.polybank.entity.CompanyEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.ClientDTO" %><%--
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
<jsp:include page="../components/navbar.jsp"></jsp:include>
<div class="container">
<%
    List<ClientDTO> clientDTOList = (List<ClientDTO>) request.getAttribute("clients");
    if (clientDTOList != null) {
%>
<h1>Clientes</h1>
    <jsp:include page="../components/client_filter.jsp"></jsp:include>
    <table class="table">
        <thead>
            <tr>
                <th scope="col">DNI</th>
                <th scope="col">Name</th>
                <th scope="col">Timestamp</th>
                <th scope="col">Link</th>
            </tr>
        </thead>
        <tbody>
    <% for (ClientDTO client : clientDTOList) { %>
    <tr>
        <td><%= client.getDni()%></td>
        <td><%= client.getName()%> <%= client.getSurname()%></td>
        <td><%= client.getCreationDate() %></td>
        <td><a href="/employee/manager/account/client/<%= client.getId()%>">Ver cuenta</a></td>
    </tr>
    <% } %>
    </tbody>
</table>
<% } %>
</div>
</body>
</html>
