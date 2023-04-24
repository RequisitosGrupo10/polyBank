<%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 27/03/2023
  Time: 13:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Log In</title>
</head>
<body>
<h1>Welcome to PolyBank ATM!</h1>
<h3>Please, log in to continue.</h3>
<c:if test="${error != null}">
    <p style="color:red">
            ${error}
    </p></c:if>
<form method="post" action="/atm/login">
    <label for="userName">Username: </label><input id="userName" name="userName" type="text" size="45" maxlength="45"><br><br>
    <label for="password">Password: </label><input id="password" name="password" type="password" size="40" maxlength="40"><br><br>
    <button type="submit"> Log In</button>
</form>
</body>
</html>
