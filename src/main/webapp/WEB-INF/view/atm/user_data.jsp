<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.taw.polybank.entity.ClientEntity" %>
<%@ page import="com.taw.polybank.entity.BankAccountEntity" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 27/03/2023
  Time: 13:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ClientEntity cliente = (ClientEntity) session.getAttribute("client");
    List<BankAccountEntity> bankAccounts = (List<BankAccountEntity>) request.getAttribute("bankAccounts");
%>
<html>
<head>
    <title>Polybank - Cliente</title>
</head>
<body>
<h1>Welcome, <%=cliente.getName()%> <%=cliente.getSurname()%>
</h1>
<h3>What do you want to do?</h3>
<form action="/atm/editarDatos" method="get">
    <button type="submit">Modify my data</button> <br>
</form>
<form action="/atm/enumerarAcciones" method="post">
    <label for="bankAccount">Select a bank account:</label>
    <select id="bankAccount" name="bankAccount">
        <%
            for (BankAccountEntity bankAccount : bankAccounts){
        %>
        <option value=<%=bankAccount.getId()%>><%=bankAccount.getIban()%></option>
        <%
            }
        %>
    </select>
    <button type="submit">Make/audit transactions</button>
</form>
</body>
</html>
