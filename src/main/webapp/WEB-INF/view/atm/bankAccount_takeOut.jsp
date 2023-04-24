<%@ page import="com.taw.polybank.entity.BadgeEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.entity.BankAccountEntity" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 01/04/2023
  Time: 20:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
  BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
  BadgeEntity actualBadge = (BadgeEntity) session.getAttribute("badge");
  List<BadgeEntity> badges = (List<BadgeEntity>) request.getAttribute("badges");
%>
<head>
    <title>PolyBank - TakeOut</title>
</head>
<body>
<h1>Take out money</h1>
<h3>Balance: <%= bankAccount.getBalance()%> <%=actualBadge.getName()%></h3>
<form method="post" action="/atm/takeOut">
  <label for="amount">Amount: </label>
  <input type="text" maxlength="6" name="amount" id="amount">
  <select name="badge">
    <%
      for (BadgeEntity badge : badges){
    %>
    <option value="<%=badge.getId()%>" <%if(badge.getId() == actualBadge.getId()){%> selected <%}%> ><%=badge.getName()%></option>
    <%
      }
    %>
  </select> <br>
  <button type="submit">TakeOut</button>
</form>
<form action="/atm/enumerarAcciones" method="post">
  <button>Go back</button>
</form>
</body>
</html>
