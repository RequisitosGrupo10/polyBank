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
    <title>Polybank - Employee Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
</head>
<body>
<div class="container">
    <h1>Acciones de ${employeeType}:</h1>
    <a class="btn btn-primary" href="manager/requests">Peticiones</a>
    <a class="btn btn-primary" href="manager/accounts/clients">Clientes</a>
    <a class="btn btn-primary" href="manager/accounts/companies">Empresas</a>
    <a class="btn btn-primary" href="manager/suspicious">Transacciones a cuentas sospechosas</a>
    <a class="btn btn-primary" href="manager/accounts/inactive">Cuentas inactivas</a>
</div>
</body>
</html>
