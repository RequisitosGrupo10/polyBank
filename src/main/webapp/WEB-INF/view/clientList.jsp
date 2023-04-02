<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="ch.qos.logback.core.net.server.Client" %><%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<ClientEntity> lista = (List<ClientEntity>) request.getAttribute("clients");
%>
<html>
<head>
    <title>Polybank - Clientes</title>
</head>
<body>
<h1>Lista de clientes:</h1>
<table border="1">
    <tr>
        <th>ID</th>
        <th>NOMBRE</th>
        <th>APELLIDOS</th>
        <th>FECHA DE CREACIÓN</th>
        <th></th>
        <th></th>
    </tr>
    <%
        for (ClientEntity cliente : lista) {
    %>
    <tr>
        <td><%= cliente.getId() %></td>
        <td><%= cliente.getName() %></td>
        <td><%= cliente.getSurname() %></td>
        <td><%= cliente.getCreationDate() %></td>
        <td><a href="/client/edit?id=<%= cliente.getId() %>"> Editar</a></td>
        <td><a href="/client/delete?id=<%= cliente.getId() %>"> Borrar</a></td>
    </tr>
    <%
        }
    %>
</table><br>
<button type="button" onclick="location.href='/client/create'">Añadir Cliente</button>
</body>
</html>
