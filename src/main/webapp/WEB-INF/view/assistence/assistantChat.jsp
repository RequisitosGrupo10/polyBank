<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.taw.polybank.dto.ChatDTO" %>
<%@ page import="com.taw.polybank.dto.MessageDTO" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: Javier Jordán Luque
  Date: 27/03/23
  Time: 10:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    ChatDTO chat = (ChatDTO) request.getAttribute("chat");
    List<MessageDTO> messageList = new ArrayList<>(chat.getMessageList());
%>

<html>
<head>
    <title>Polybank - Assistant - Chat</title>
</head>
<body>
<h1>Assistence Chat (Client <%= chat.getClient().getName() %>)</h1>
<table border="1">
    <tr>
        <th>ME</th>
        <th>CLIENT</th>
    </tr>
        <%
            for (MessageDTO message : messageList) {
                if (message.getAssistant() != null) {
        %>
    <tr>
        <td><%= message.getContentAndDate() %></td>
        <td></td>
    </tr>
        <%
                } else {
        %>
    <tr>
        <td></td>
        <td><%= message.getContentAndDate() %></td>
    </tr>
        <%
                }
            }
        %>
</table>
<br>
        <%
            if (chat.getClosed() == 0) {
        %>
    <form action="/employee/assistence/send" method="post">
        <input hidden="true" name="chatId" value="<%= chat.getId() %>">
        <textarea name="content" cols="50" rows="5" maxlength="1000"></textarea>
        <br>
        <button>Send</button>
    </form>
        <%
            }
        %>
    <br>
    <form action="/employee/assistence/" method="get">
        <button>Back</button>
    </form>
</body>
</html>