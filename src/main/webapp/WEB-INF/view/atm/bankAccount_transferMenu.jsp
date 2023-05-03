<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.BankAccountDTO" %>
<%@ page import="com.taw.polybank.dto.BadgeDTO" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 01/04/2023
  Time: 18:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
    BadgeDTO actualBadge = (BadgeDTO) session.getAttribute("badge");
%>
<html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<head>
    <title>PolyBank - Transfer money</title>
</head>
<body>
<div class="container-sm text-center">
    <h1>Transfer money</h1>
    <div class="card" style="width: 600px; margin: auto; margin-top: 200px">
        <h3 class="card-header" style="background-color: aliceblue">Insert transference data</h3>
        <div class="card-body">
            <ul class="list-group list-group-flush">
                <li class="list-group-item">
                    <h5>Balance: <%= bankAccount.getBalance()%> <%=actualBadge.getName()%>
                    </h5>
                </li>
                <li class="list-group-item">
                    <c:if test="${error != null}">
                        <p style="color:red;">
                                ${error}
                        </p>
                    </c:if>
                    <form method="post" action="/atm/makeTransfer">
                        <div style="text-align: left">
                            <label for="amount">Amount: </label>
                            <input type="number" maxlength="6" name="amount" id="amount" min="0"
                                   max="<%= bankAccount.getBalance()%>"> <br>
                            <label for="receiver">Receiver IBAN: </label>
                            <input type="text" maxlength="34" size="34" id="receiver" name="receiver"> <br>
                            <label for="receiverName">Receiver name: </label>
                            <input type="text" maxlength="45" size="45" id="receiverName" name="receiverName"> <br><br>
                        </div>
                        <button type="submit" class="btn btn-primary">Transfer</button>
                    </form>
                </li>
                <li class="list-group-item">
                    <form action="/atm/enumerarAcciones" method="post">
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
