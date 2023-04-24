<%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 15/04/2023
  Time: 11:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>PolyBank - Request Unban</title>
</head>
<body>
    <form method="get" action="/atm/makeUnbanPetition">
        <label for="description">Tell us why do you want to unban your account:</label><br>
        <input type="text" size="100" maxlength="100" id="description" name="description"><br>
        <button>Make the petition</button>
        <button formaction="/atm/enumerarAcciones" formmethod="post">Go back</button>
    </form>
</body>
</html>
