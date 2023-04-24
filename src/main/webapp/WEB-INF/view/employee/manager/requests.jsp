<%@ page import="com.taw.polybank.entity.RequestEntity" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.List" %><%--
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
    List<RequestEntity> requests = (List<RequestEntity>) request.getAttribute("requests");
%>
<div class="container">
<h1>Solicitudes pendientes:</h1>
<% if (requests != null) { %>
    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Cuenta que solicita</th>
                <th>Timestamp</th>
                <th>Descripción</th>
                <th>Aprobar/Denegar</th>
            </tr>
        </thead>
        <tbody>
            <% for (RequestEntity requestEntity : requests ) { %>
            <tr>
                <td>
                    <%= requestEntity.getId()%>
                </td>
                <td>
                    <%= requestEntity.getClientByClientId().getDni()%>
                </td>
                <td>
                    <%= requestEntity.getTimestamp()%>
                </td>
                <td>
                    <%= requestEntity.getDescription()%>
                </td>
                <td>
                    <a href="/employee/manager/approve/<%=requestEntity.getId()%>">Aprobar</a>
                    </br>
                    <a href="/employee/manager/deny/<%=requestEntity.getId()%>">Denegar</a>
                </td>
            </tr>
            <% } %>
        </tbody>
    </table>
<% } %>
</div>
</body>
</html>
