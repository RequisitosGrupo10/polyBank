<%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 15/04/2023
  Time: 11:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<head>
    <title>PolyBank - Request Unban</title>
</head>
<body>
<div class="container-sm text-center">
    <h1>Make an unban petition</h1>
    <div class="card" style="width: 600px; margin: auto; margin-top: 200px">
        <h3 class="card-header">Reason for the petition</h3>
        <ul class="list-group list-group-flush">
            <form method="get" action="/atm/makeUnbanPetition">
                <li class="list-group-item">
                    <label for="description">Tell us why do you want to unban your account:</label><br>
                    <textarea cols="65" rows="3" maxlength="100" id="description" name="description"></textarea><br><br>
                    <button class="btn btn-primary">Make the petition</button>
                </li>
                <br>
                <button class="btn btn-secondary" formaction="/atm/enumerarAcciones" formmethod="post">Go back</button>
            </form>
        </ul>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
</body>
</html>
