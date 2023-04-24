<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.entity.*" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 02/04/2023
  Time: 12:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BankAccountEntity bankAccount = (BankAccountEntity) session.getAttribute("bankAccount");
    List<TransactionEntity> transactions = (List<TransactionEntity>) request.getAttribute("transactions");
%>
<html>
<head>
    <title>Polybank - Transactions</title>
</head>
<body>
<h1>Transactions of the account <%=bankAccount.getIban()%></h1>
<h3>Filter transactions</h3>
<form:form modelAttribute="filter" action="/atm/checkTransactions" method="post">
    <label for="timestampBegin">Filter by timestamp</label>
    <form:input type="date" path="timestampBegin" id="timestampBegin" name="timestampBegin"></form:input>
    <form:input type="date" path="timestampEnd" id="timestampEnd" name="timestampEnd"></form:input> <br>
    <label for="transactionOwner">Filter by transaction owner:</label>
    <form:input path="transactionOwner" id="transactionOwner" name="transactionOwner" size="45" maxlength="45"></form:input> <br>
    <label for="beneficiaryIban">Filter by beneficiary IBAN:</label>
    <form:input path="beneficiaryIban" id="beneficiaryIban" name="beneficiaryIban" size="34" maxlength="34"></form:input> <br>
    <label for="amount">Filter by amount (greater or equal than):</label>
    <form:input type="number" path="amount" id="amount" name="amount"></form:input> <br>
    <form:button type="submit">Filter</form:button>
</form:form>
<form action="/atm/enumerarAcciones" method="post">
    <button>Go back</button>
</form>
<table style="border-collapse: collapse; border: 1px solid">
    <tr style="border: 1px solid">
        <th rowspan="3" style="border: 1px solid">Timestamp</th>
        <th rowspan="3" style="border: 1px solid">Transaction owner</th>
        <th colspan="4" style="border: 1px solid">Currency exchange data</th>
        <th colspan="7" style="border: 1px solid">Payment data</th>
    </tr>
    <tr style="border: 1px solid">
        <th rowspan="2" style="border: 1px solid">Initial amount</th>
        <th rowspan="2" style="border: 1px solid">Initial badge</th>
        <th rowspan="2" style="border: 1px solid">Final amount</th>
        <th rowspan="2" style="border: 1px solid">Final badge</th>
        <th colspan="2" style="border: 1px solid">Beneficiary</th>
        <th colspan="4" style="border: 1px solid">Currency exchange</th>
        <th rowspan="2" style="border: 1px solid">Amount</th>
    </tr>
    <tr>
        <th style="border: 1px solid">Beneficiary IBAN</th>
        <th style="border: 1px solid">Beneficiary name</th>
        <th style="border: 1px solid">Initial amount</th>
        <th style="border: 1px solid">Initial badge</th>
        <th style="border: 1px solid">Final amount</th>
        <th style="border: 1px solid">Final badge</th>
    </tr>
    <%
        for (TransactionEntity transaction : transactions) {
    %>
    <tr style="border: 1px solid">
        <th style="border: 1px solid"><%=transaction.getTimestamp()%>
        </th>
        <th style="border: 1px solid"><%=transaction.getClientByClientId().getName()%> <%=transaction.getClientByClientId().getSurname()%>
        </th>
        <%
            if (transaction.getCurrencyExchangeByCurrencyExchangeId() != null) {
                CurrencyExchangeEntity currencyExchange = transaction.getCurrencyExchangeByCurrencyExchangeId();
        %>
        <th style="border: 1px solid"><%=currencyExchange.getInitialAmount()%></th>
        <th style="border: 1px solid"><%=currencyExchange.getBadgeByInitialBadgeId().getName()%></th>
        <th style="border: 1px solid"><%=currencyExchange.getFinalAmount()%></th>
        <th style="border: 1px solid"><%=currencyExchange.getBadgeByFinalBadgeId().getName()%></th>
        <%
        } else {
        %>
        <th style="border: 1px solid"></th>
        <th style="border: 1px solid"></th>
        <th style="border: 1px solid"></th>
        <th style="border: 1px solid"></th>
        <%
            }
        %>

        <%
            if (transaction.getPaymentByPaymentId() != null) {
                PaymentEntity payment = transaction.getPaymentByPaymentId();
        %>
        <%
            if (payment.getBenficiaryByBenficiaryId() != null) {
                BenficiaryEntity beneficiary = payment.getBenficiaryByBenficiaryId();
        %>
        <th style="border: 1px solid"><%=beneficiary.getIban()%></th>
        <th style="border: 1px solid"><%=beneficiary.getName()%></th>
        <%
        } else {
        %>
        <th style="border: 1px solid"></th>
        <th style="border: 1px solid"></th>
        <%
            }
        %>
        <%
            if (payment.getCurrencyExchangeByCurrencyExchangeId() != null) {
                CurrencyExchangeEntity paymentCurrencyExchange = payment.getCurrencyExchangeByCurrencyExchangeId();
        %>
        <th style="border: 1px solid"><%=paymentCurrencyExchange.getInitialAmount()%></th>
        <th style="border: 1px solid"><%=paymentCurrencyExchange.getBadgeByInitialBadgeId().getName()%></th>
        <th style="border: 1px solid"><%=paymentCurrencyExchange.getFinalAmount()%></th>
        <th style="border: 1px solid"><%=paymentCurrencyExchange.getBadgeByFinalBadgeId().getName()%></th>
        <%
        } else {
        %>
        <th style="border: 1px solid"></th>
        <th style="border: 1px solid"></th>
        <th style="border: 1px solid"></th>
        <th style="border: 1px solid"></th>
        <%
            }
        %>
        <th style="border: 1px solid"><%=payment.getAmount()%></th>
        <%
        } else {
        %>
        <th style="border: 1px solid"></th>
        <th style="border: 1px solid"></th>
        <th style="border: 1px solid"></th>
        <%
            }
        %>

    </tr>
    <%
        }
    %>
</table>
</body>
</html>
