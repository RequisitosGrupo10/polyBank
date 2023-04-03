<%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
  completely rewritten by Illya Rozumovskyy 03/04/2023
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank - Login</title>
    <style>
        .prettyButton {
            margin-right: 1rem;
            padding: 0.2rem;
            width: fit-content;
            height: 1.5rem;
            border: solid 1px black;
            border-radius: 5px;
            background-color: #ecffa8;
            text-decoration:none;
        }
    </style>
</head>
<body>
<h1>Insert your id and password:</h1>

<form id="someForm" action="" method="POST">
    <label for="dni">ID: </label>
    <input type="text" id="dni" name="dni"><br>
    <label for="password">Password: </label>
    <input type="password" id="password" name="password"><br>

    <input class="prettyButton" type="button" value="I am client" name="save" onclick="asClient()" />    <%-- // Insert your link in the below <script> --%>
    <input class="prettyButton" type="button" value="I am Company Representative" name="finished" onclick="asRepresentative()" />
</form>

<script>
    form=document.getElementById("someForm");
    function asClient() {
        form.action="/"; // TODO Insert your login link
        form.submit();
    }
    function asRepresentative() {
        form.action="/company/login";
        form.submit();
    }
</script>

</body>
</html>
