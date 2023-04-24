<%@ page import="com.taw.polybank.entity.BadgeEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.entity.BankAccountEntity" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 01/04/2023
  Time: 18:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
    BadgeEntity actualBadge = (BadgeEntity) session.getAttribute("badge");
%>
<html>
<head>
    <title>PolyBank - Transfer money</title>
</head>
<body>
<h1>Transfer money</h1>
<h3>Balance: <%= bankAccount.getBalance()%> <%=actualBadge.getName()%>
</h3>
<form method="post" action="/atm/makeTransfer">
    <label for="amount">Amount: </label>
    <input type="text" maxlength="6" name="amount" id="amount">
    <label for="receiver">Receiver IBAN: </label>
    <input type="text" maxlength="34" size="34" id="receiver" name="receiver"> <br>
    <label for="receiverName">Receiver name: </label>
    <input type="text" maxlength="45" size="45" id="receiverName" name="receiverName"> <br>
    <button type="submit">Transfer</button>
</form>
<form action="/atm/enumerarAcciones" method="post">
    <button>Go back</button>
</form>
</body>
</html>
