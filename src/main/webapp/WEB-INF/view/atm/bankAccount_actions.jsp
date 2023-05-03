<%@ page import="com.taw.polybank.dto.BankAccountDTO" %>
<%@ page import="com.taw.polybank.dto.BadgeDTO" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 01/04/2023
  Time: 17:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
    BadgeDTO badge = (BadgeDTO) session.getAttribute("badge");
%>
<html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<head>
    <title>PolyBank - Account Actions</title>
</head>
<body>
<div class="container-sm text-center">
    <h1>Account actions</h1>
    <div class="card" style="width: 600px; margin: auto; margin-top: 200px">
        <h3 class="card-header" style="background-color: aliceblue">What do you want to do?</h3>
        <div class="card-body">
            <ul class="list-group list-group-flush">
                <li class="list-group-item">
                    <h5>Balance: <%= bankAccount.getBalance()%> <%=badge.getName()%>
                    </h5>
                </li>
                <li class="list-group-item">
                    <form method="get">
                        <%
                            if (bankAccount.isActive()) {
                        %>
                        <button class="btn btn-primary" type="submit" formaction="/atm/makeTransfer">Transfer money
                        </button>
                        <br>
                        <button class="btn btn-primary" style="margin-top: 10px" type="submit" formaction="/atm/takeOut">Take out money</button>
                        <br>
                        <button class="btn btn-primary" style="margin-top: 10px" type="submit" formaction="/atm/checkTransactions">Check previous
                            transactions
                        </button>
                        <br>
                        <%
                        } else {
                        %>
                        <h5 style="color: darkred">This account has been banned.</h5>
                        <button class="btn btn-danger" type="submit" formaction="/atm/requestUnban" formmethod="get">
                            See/Make unban petitions
                        </button>
                        <br>
                        <%
                            }
                        %>
                    </form>
                </li>
                <li class="list-group-item">
                    <form action="/atm/" method="get">
                        <button class="btn btn-secondary">Go back</button>
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
