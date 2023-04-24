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
    <title>Polybank - Employee Check Accounts</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<%
    List<ClientDTO> clientDTOList = (List<ClientDTO>) request.getAttribute("clients");
    List<CompanyDTO> companyDTOList = (List<CompanyDTO>) request.getAttribute("companies");
%>
<div class="container">
    <% if (clientDTOList != null) { %>
    <h1>Clientes</h1>
    <table>
        <thead>
        <tr>
            <th>DNI</th>
            <th>Timestamp</th>
        </tr>
        </thead>
        <tbody>
        <% for (ClientDTO client : clientDTOList) { %>
        <tr>
            <td>
                <%= client.getDni()%>
            </td>
            <td>
                <%= client.getCreationDate() %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>
<% if (companyDTOList != null) { %>
<h1>Empresas:</h1>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Nombre</th>
    </tr>
    </thead>
    <tbody>
    <% for (CompanyDTO companyDTO : companyDTOList) { %>
           <tr>
                <td>
                    <%= companyDTO.getId()%>
                </td>
                <td>
                    <%= companyDTO.getName() %>
                </td>
            </tr>
      <% } %>
    </tbody>
</table>
<% } %>
</div>
</body>
</html>
