<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="com.taw.polybank.entity.CompanyEntity" %>

<%--
  Created by IntelliJ IDEA.
  User: panva
  Date: 05/04/2023
  Time: 12:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    ClientEntity client = (ClientEntity) session.getAttribute("client");
    if(client == null){
%>
<jsp:forward page="../login.jsp"/>
<%
    }
    CompanyEntity company = (CompanyEntity) session.getAttribute("company");
%>



<h1>Welcome ${client.name} you acting on behalf of ${company.name} company</h1>

<a class="prettyButton" style="background-color:#ffdb90" href="/company/user/logout">Exit</a>