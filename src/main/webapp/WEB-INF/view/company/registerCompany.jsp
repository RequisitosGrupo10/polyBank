<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.BadgeDTO" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 02/04/2023
  Time: 16:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank. Create your company account</title>

    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>

<h1>Create your company account</h1>

<form method="post" action="/company/registerCompanyOwner">
    <label id="name">Company name:</label>
    <input type="text" id="name" name="name" size="45" maxlength="45" class="formElement" />
    <br/>
    <label id="badge">Currency:</label>
    <select name="badge" id="badge">
        <%
            List<BadgeDTO> badgeList = (List<BadgeDTO>) request.getAttribute("badgeList");
            for(BadgeDTO b : badgeList){
        %>
            <option value="<%=b.getId()%>" label="<%=b.getName()%>"></option>
        <% } %>
    </select>
    <br/>
    As a part of registration process you required to associate at least one manager to your bank account
    <br/>
    <input type="submit" class="prettyButton" name="Register" value="Add a manager">
</form>

</body>
</html>
