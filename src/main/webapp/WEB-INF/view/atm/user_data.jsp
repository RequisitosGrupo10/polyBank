<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.ClientDTO" %>
<%@ page import="com.taw.polybank.dto.BankAccountDTO" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 27/03/2023
  Time: 13:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ClientDTO cliente = (ClientDTO) session.getAttribute("client");
    List<BankAccountDTO> bankAccounts = (List<BankAccountDTO>) request.getAttribute("bankAccounts");
%>
<html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<head>
    <title>Polybank - Client</title>
</head>
<body>
<div class="container-sm text-center">
    <h1>Welcome, <%=cliente.getName()%> <%=cliente.getSurname()%>
    </h1>
    <div class="card" style="width: 600px; margin: auto; margin-top: 200px">
        <h3 class="card-header" style="background-color: aliceblue">What do you want to do?</h3>
        <div class="card-body">
            <ul class="list-group list-group-flush">
                <li class="list-group-item">
                    <form action="/atm/editarDatos" method="get">
                        <button type="submit" class="btn btn-primary">Modify my data</button>
                    </form>
                </li>
                <li class="list-group-item">
                    <form action="/atm/enumerarAcciones" method="post">
                        <label for="bankAccount">Select a bank account:</label>
                        <select id="bankAccount" name="bankAccount">
                            <%
                                for (BankAccountDTO bankAccount : bankAccounts) {
                            %>
                            <option value=<%=bankAccount.getId()%>><%=bankAccount.getIban()%>
                            </option>
                            <%
                                }
                            %>
                        </select>
                        <br>
                        <br>
                        <button type="submit" class="btn btn-primary">Make/audit transactions</button>
                    </form>
                </li>
            </ul>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
</body>
</html>
