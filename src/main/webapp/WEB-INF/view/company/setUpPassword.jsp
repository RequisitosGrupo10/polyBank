<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.taw.polybank.dto.ClientDTO" %><%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 05/04/2023
  Time: 23:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%
        ClientDTO client = (ClientDTO) request.getAttribute("client");
    %>
    <title>New password for <%=client.getName()%></title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>
<jsp:include page="corporateHeader.jsp"/>
<h1>Set up new password for <%=client.getName()%></h1>


<form:form id="mainForm" action="/company/user/saveNewPassword" method="post" modelAttribute="client">
    <form:hidden path="id"/>
    <form:hidden path="isNew"/>
    <form:hidden path="name"/>
    <form:hidden path="surname"/>
    <form:hidden path="dni"/>

    <label for="password">Password: </label>
    <input id="password" type="password" name="password" size="25" maxlength="64" class="formElement"/>
    <br/>
    <label for="passwordRep">Repeat password: </label>
    <input id="passwordRep" type="password" size="25" maxlength="64" class="formElement" />
    <br/>
    <form:button id="submitButtonOne" class="prettyButton" onclick="checkPass()">Submit</form:button>
</form:form>

<script>
    const mainForm = document.getElementById("mainForm");
    const pass = document.getElementById("password").value;
    const passRep = document.getElementById("passwordRep").value;
    function checkPass(){
        if(pass === passRep){
            mainForm.submit();
        }else {
            window.alert("Passwords doesn't match");
        }
    }
</script>
</body>
</html>
