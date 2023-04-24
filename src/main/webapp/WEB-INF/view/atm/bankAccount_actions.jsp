<%@ page import="com.taw.polybank.entity.BankAccountEntity" %>
<%@ page import="com.taw.polybank.entity.BadgeEntity" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 01/04/2023
  Time: 17:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
    BadgeEntity badge = (BadgeEntity) session.getAttribute("badge");
%>
<html>
<head>
    <title>PolyBank - Account Actions</title>
</head>
<body>
<h1>Account actions</h1>
<h3>Balance: <%= bankAccount.getBalance()%> <%=badge.getName()%>
</h3>
<form method="post">
    <%
        if (bankAccount.getActive() != (byte) 1) {
    %>
    <button type="submit" formaction="/atm/makeTransfer" formmethod="get">Transfer money</button>
    <br>
    <button type="submit" formaction="/atm/takeOut" formmethod="get">Take out money</button>
    <br>
    <button type="submit" formaction="/atm/checkTransactions" formmethod="get">Check previous transactions</button>
    <br>
    <%
        } else{
    %>
    <button type="submit" formaction="/atm/requestUnban" formmethod="get">See/Make unban petitions</button>
    <br>
    <%
        }
    %>
</form>
<form action="/atm/" method="get">
    <button>Go back</button>
</form>
</body>
</html>
