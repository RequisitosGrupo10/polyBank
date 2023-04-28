<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 06/04/2023
  Time: 11:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit ${company.name}</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>
<jsp:include page="corporateHeader.jsp"/>
<h1>Edit ${company.name}</h1>

<form:form modelAttribute="company" method="post" action="/company/user/updateCompanyData">
    <form:label path="name">Company name:</form:label>
    <form:input path="name" size="45" maxlength="45" class="formElement" />
    <br/>
    <form:button class="prettyButton" value="submit">Submit</form:button>
</form:form>

</body>
</html>
