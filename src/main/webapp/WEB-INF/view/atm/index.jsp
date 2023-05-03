<%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 27/03/2023
  Time: 13:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<head>
    <title>Log In</title>
</head>
<body>
<div class="container-sm text-center">
    <h1 style="margin-top: 100px" >Welcome to PolyBank ATM!</h1>
    <div class="card" style="width: 600px; margin: auto; margin-top: 200px">
        <h3 class="card-header" style="background-color: aliceblue">Please, log in to continue.</h3>
        <div class="card-body">
            <c:if test="${error != null}">
                <p style="color:red;">
                        ${error}
                </p>
            </c:if>
            <form method="post" action="/atm/login">
                <label for="userName">Username: </label><input id="userName" name="userName" type="text" size="45"
                                                               maxlength="45"><br><br>
                <label for="password">Password: </label><input id="password" name="password" type="password" size="40"
                                                               maxlength="40"><br><br>
                <button type="submit" class="btn btn-primary"> Log In</button>
            </form>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
</body>
</html>
