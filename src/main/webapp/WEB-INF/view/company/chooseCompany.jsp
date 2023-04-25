<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.entity.CompanyEntity" %><%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 25/04/2023
  Time: 22:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Choose company</title>

    <%
        List<CompanyEntity> companies = (List<CompanyEntity>) request.getAttribute("companies");
    %>
</head>
<body>

<h1>Choose company to manage</h1>
You currently assigned to manage following companies:

<table border="1">
    <tr>
        <th>Company</th>
        <th></th>
    </tr>
    <%
        for(CompanyEntity c : companies){
    %>
    <tr>
        <td><%=c.getName()%></td>
        <td><a href="/company/chooseCompany?id=<%=c.getId()%>">Manage</a></td>
    </tr>
    <%
        }
    %>
</table>

</body>
</html>
