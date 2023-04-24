<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.ChatDTO" %><%--
  Created by IntelliJ IDEA.
  User: Javier Jordán Luque
  Date: 27/03/2023
  Time: 13:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    List<ChatDTO> chatList = (List<ChatDTO>) request.getAttribute("chatList");
%>

<html>
<head>
    <title>Polybank - Assistence</title>
</head>
<body>
<h1>Chats List</h1>
<table border="1">
    <tr>
        <th>ASSISTANT</th>
        <th>CLOSED</th>
        <th></th>
        <th></th>
    </tr>
    <%
        for (ChatDTO chat : chatList) {
    %>
    <tr>
        <td><%= chat.getAssistant().getName() %></td>
        <td><%= chat.isClosed() %></td>
        <td><a href="/client/assistence/chat?id=<%= chat.getId() %>">Open</a></td>
        <%
            if (!chat.isClosed()) {
        %>
        <td>
            <form action="/client/assistence/close" method="post">
                <input hidden="true" name="chatId" value="<%= chat.getId() %>">
                <button>Close</button>
            </form>
        </td>
        <%
            } else {
        %>
        <td></td>
        <%
            }
        %>
    </tr>
    <%
        }
    %>
</table>
<br>
<form action="/client/assistence/newChat" method="post">
    <button>New Chat</button>
</form>
</body>
</html>