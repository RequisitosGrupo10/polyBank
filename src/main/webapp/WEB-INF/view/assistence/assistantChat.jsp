<%@ page import="com.taw.polybank.entity.MessageEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.entity.ChatEntity" %>
<%@ page import="java.util.ArrayList" %>
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
    ChatEntity chat = (ChatEntity) request.getAttribute("chat");
    List<MessageEntity> messageList = new ArrayList<>(chat.getMessagesById());
%>

<html>
<head>
    <title>Polybank - Assistant - Chat</title>
</head>
<body>
<h1>Assistence Chat (Client <%= chat.getClientByClientId().getName() %>)</h1>
<table border="1">
    <tr>
        <th>ME</th>
        <th>CLIENT</th>
    </tr>
        <%
            for (MessageEntity message : messageList) {
                if (message.getEmployeeByEmployeeId() != null) {
        %>
    <tr>
        <td><%= message.getContent()%> (<%= message.getDate() %>)</td>
        <td></td>
    </tr>
        <%
                } else {
        %>
    <tr>
        <td></td>
        <td><%= message.getContent()%> (<%= message.getDate() %>)</td>
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
        <textarea name="content" cols="50" rows="5" maxlength="1000"/>
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