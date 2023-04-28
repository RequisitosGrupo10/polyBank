<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.entity.MessageEntity" %>
<%@ page import="com.taw.polybank.dto.ClientDTO" %>
<%@ page import="com.taw.polybank.dto.CompanyDTO" %>
<%@ page import="com.taw.polybank.service.ClientService" %>
<%@ page import="com.taw.polybank.service.AuthorizedAccountService" %><%--
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
        List<ClientDTO> clientList = (List<ClientDTO>) request.getAttribute("clientList");
        ClientDTO active = (ClientDTO) session.getAttribute("client");
        CompanyDTO company = (CompanyDTO) session.getAttribute("company");
        ClientService clientService = (ClientService) request.getAttribute("clientService");
        AuthorizedAccountService authorizedAccountService = (AuthorizedAccountService) request.getAttribute("authorizedAccountService");
    %>
    <title>Representatives of ${company.name}</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>
<jsp:include page="corporateHeader.jsp"/>
<h1>Representatives of ${company.name} company</h1>

<form:form modelAttribute="clientFilter" method="post" action="/company/user/listFilteredRepresentatives">
    <form:label path="nameOrSurname">Name or surname:</form:label>
    <form:input path="nameOrSurname"/>
    <br/>
    <form:label path="registeredAfter">Registered after</form:label>
    <form:input type="date" path="registeredAfter" name="registeredAfter"/>
    <form:label path="registeredBefore">Registered before</form:label>
    <form:input type="date" path="registeredBefore" name="registeredBefore"/>
    <br/>
    <form:button class="prettyButton" name="Submit">Filter</form:button>
</form:form>

<a href="/company/user/listAllRepresentatives" class="prettyButton">Reset</a><br/>

<table border="1">
    <tr>
        <th>Block Representative</th>
        <th>Name</th>
        <th>Surname</th>
        <th>ID number</th>
        <th>Status</th>
        <th>Registration date</th>
        <th>Last Message</th>
    </tr>
    <%
        for(ClientDTO c : clientList){
    %>
    <tr>
        <%
            String style = "";
            if(active.getId() == c.getId() || clientService.getNumberAuthorizedAccounts(c.getId()) < 1){
                style = "style=\"pointer-events: none; color: gray; text-decoration: none;\"";
            }
        %>
        <td><a <%=style%> href="/company/user/blockRepresentative?id=<%=c.getId()%>">Block</a></td>
        <td><%=c.getName()%></td>
        <td><%=c.getSurname()%></td>
        <td><%=c.getDni()%></td>
        <%
            String status = "ok";
            if(clientService.isBlocked(c, company, authorizedAccountService)){
                status = "blocked";
            }
        %>
        <td><%=status%></td>
        <td><%=c.getCreationDate().toLocalDateTime()%></td>
        <td><%=clientService.getLastMessage(c)%></td>
    </tr>
    <%
        }
    %>
</table>
<p style="font-size: 6pt">Can not block your own account, neither account of the company's owner</p>


</body>
</html>
