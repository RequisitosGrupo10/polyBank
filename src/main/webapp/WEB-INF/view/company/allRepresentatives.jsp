<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.entity.MessageEntity" %><%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 06/04/2023
  Time: 12:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%
        List<ClientEntity> clientList = (List<ClientEntity>) request.getAttribute("clientList");
    %>
    <title>Representatives of ${company.name}</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>
<jsp:include page="corporateHeader.jsp"/>
<h1>Representatives of ${company.name} company</h1>

<form:form modelAttribute="clientFilter" method="post" action="/company/listFilteredRepresentatives">
    <form:label path="nameOrSurname">Name or surname:</form:label>
    <form:input path="nameOrSurname"/>
    <br/>
    <form:label path="messageText">Message text</form:label>
    <form:input path="messageText"/>
    <br/>
    <form:button class="prettyButton" name="Submit">Filter</form:button>
</form:form>

<table border="1">
    <tr>
        <th>Name</th>
        <th>Surname</th>
        <th>ID number</th>
        <th>Last Message</th>
    </tr>
    <%
        for(ClientEntity client : clientList){
    %>
    <tr>
        <td><%=client.getName()%></td>
        <th><%=client.getSurname()%></th>
        <th><%=client.getDni()%></th>
        <th><%=client.getMessagesById().stream().reduce((a, b) -> b).map(MessageEntity::getContent).orElse("")%></th>
    </tr>
    <%
        }
    %>
</table>

<a class="prettyButton" href="/company/user/">Return</a>

</body>
</html>
