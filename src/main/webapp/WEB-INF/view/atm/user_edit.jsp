<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 01/04/2023
  Time: 16:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<head>
    <title>Polybank - Edit data</title>
</head>
<body>
<div class="container text-center">
    <h1>Edit client data</h1>
    <div class="row">
        <div class="col">
            <div class="card">
                <h3 class="card-header" style="background-color: aliceblue">Edit name or surname</h3>
                <div class="card-body">
                    <form:form modelAttribute="client" method="post" action="/atm/editarDatos">
                        <label for="dni">DNI:</label>
                        <form:input path="dni" readonly="true" id="dni" cssStyle="background-color: lightgray" style=""></form:input><br> <br>
                        <label for="name">Name:</label>
                        <form:input path="name" size="45" maxlength="45" id="name"></form:input><br> <br>
                        <label for="surname">Surname:</label>
                        <form:input path="surname" size="45" maxlength="45" id="surname"></form:input><br> <br>
                        <form:button class="btn btn-primary">Save</form:button>
                    </form:form>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card">
                <h3 class="card-header" style="background-color: aliceblue">Change password</h3>
                <div class="card-body">
                    <c:if test="${error != null}">
                        <p style="color:red;">
                                ${error}
                        </p>
                    </c:if>
                    <form action="/atm/changePassword" method="post">
                        <label for="password">Type new password: </label>
                        <input id="password" name="password" type="password" maxlength="64" size="64"> <br><br>
                        <label for="password2">Repeat new password: </label>
                        <input id="password2" name="password2" type="password" maxlength="64" size="64"> <br> <br>
                        <button class="btn btn-primary">Change password</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="row" style="margin-top: 20px">
        <div class="col">
            <form action="/atm/" method="get">
                <button class="btn btn-secondary">Go back</button>
            </form>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
</body>
</html>
