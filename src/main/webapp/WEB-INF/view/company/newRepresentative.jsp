<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 05/04/2023
  Time: 16:25
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="com.taw.polybank.dto.ClientDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%
        ClientDTO client = (ClientDTO) request.getAttribute("client");
    %>
    <title>Add new Representative to ${company.name}</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>
<jsp:include page="corporateHeader.jsp"/>
<h1>Add new Representative to ${company.name}</h1>

<form:form id="mainForm" action="" modelAttribute="client" method="post">
    <form:hidden path="id"/>
    <form:hidden path="isNew"/>

    <form:label path="name">Representative's name:</form:label>
    <form:input path="name" size="45" maxlength="45" class="formElement"/>
    <br/>
    <form:label path="surname">Representative's surname:</form:label>
    <form:input path="surname" size="45" maxlength="45" class="formElement"/>
    <br/>
    <form:label path="dni">Representative's ID:</form:label>
    <form:input path="dni" size="45" maxlength="45" class="formElement"/>
    <br/>

    <form:button id="submitButtonOne" class="prettyButton" onclick="setUpPassword()">Set up password</form:button>
    <form:button id="submitButtonTwo" class="prettyButton" onclick="saveRepresentative()">Save</form:button>
</form:form>

<script>
    const newClient = <%=client.getIsNew()%>;
    const mainForm = document.getElementById("mainForm");
    const actionNewPassword = "/company/user/setUpPassword";
    const actionKeepPassword = "/company/user/saveRepresentative"
    const submitButtonOne = document.getElementById("submitButtonOne");
    const submitButtonTwo = document.getElementById("submitButtonTwo");

    window.addEventListener('load', passwordField, false);

    function passwordField(){
        if(newClient) {
            submitButtonTwo.style.setProperty("display", "none");
        }else {
            submitButtonOne.innerHTML = "Change Password"
        }
    };

    function setUpPassword(){
        mainForm.action = actionNewPassword;
        mainForm.submit();
    }
    function saveRepresentative(){
        mainForm.action = actionKeepPassword;
        mainForm.submit();
    }

</script>

</body>
</html>
