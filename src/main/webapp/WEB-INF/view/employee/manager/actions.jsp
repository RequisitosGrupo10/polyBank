<%--
  Created by IntelliJ IDEA.
  User: jmsan
  Date: 13/03/2023
  Time: 14:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polybank - Manager actions</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<div class="container">
    <h1 class="my-4">Manager Activities</h1>
    <div class="list-group">
        <a href="/employee/manager/requests" class="list-group-item list-group-item-action flex-column align-items-start">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Requests</h5>
            </div>
            <p class="mb-1">See account request to reactivate their accounts.</p>
        </a>
        <a href="/employee/manager/accounts/clients" class="list-group-item list-group-item-action flex-column align-items-start">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Clients</h5>
            </div>
            <p class="mb-1">Client list information.</p>
        </a>
        <a href="/employee/manager/accounts/companies" class="list-group-item list-group-item-action flex-column align-items-start">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Companies</h5>
            </div>
            <p class="mb-1">Company list information.</p>
        </a>
        <a href="/employee/manager/suspicious" class="list-group-item list-group-item-action flex-column align-items-start">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Suspicious account</h5>
            </div>
            <p class="mb-1">Accounts related with suspicious accounts.</p>
        </a>
        <a href="/employee/manager/accounts/inactive" class="list-group-item list-group-item-action flex-column align-items-start">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Inactive account</h5>
            </div>
            <p class="mb-1">Accounts with inactivity in the last month.</p>
        </a>
    </div>

</div>
</body>
</html>
