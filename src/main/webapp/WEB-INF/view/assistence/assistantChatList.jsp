<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.taw.polybank.entity.ChatEntity" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: Javier Jordán Luque
  Date: 20/03/2023
  Time: 13:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    List<ChatEntity> chatList = (List<ChatEntity>) request.getAttribute("chatList");
%>

<html>
<head>
    <title>Polybank - Assistence</title>
</head>
<body>
<h1>Chats List</h1>
<strong>Filter:</strong>
<br>
<form:form modelAttribute="filter" method="post" action="/employee/assistence/filter">
    Client DNI: <form:input path="clientDni"></form:input>
    <br>
    Client Name: <form:input path="clientName"></form:input>
   <br>
    Order By Most Recent Messages: <form:checkbox path="recent"></form:checkbox>
    <br>
    <form:button>Filter</form:button>
</form:form>
<br>
<table border="1">
    <tr>
        <th>DNI</th>
        <th>CLIENT</th>
        <th>CLOSED</th>
        <th></th>
    </tr>
    <%
        for (ChatEntity chat : chatList) {
    %>
    <tr>
        <td><%= chat.getClientByClientId().getDni() %></td>
        <td><%= chat.getClientByClientId().getName() %></td>
        <td>
            <%
                if (chat.getClosed() == 1) {
            %>
            Yes
            <%
            } else {
            %>
            No
            <%
                }
            %>
        </td>
        <td><a href="/employee/assistence/chat?id=<%= chat.getId() %>">Open</a></td>
    </tr>
    <%
        }
    %>
</table>
</body>
</html>