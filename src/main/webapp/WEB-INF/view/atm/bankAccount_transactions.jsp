<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.*" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 02/04/2023
  Time: 12:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BankAccountDTO bankAccount = (BankAccountDTO) session.getAttribute("bankAccount");
    List<TransactionDTO> transactions = (List<TransactionDTO>) request.getAttribute("transactions");
%>
<html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<head>
    <title>Polybank - Transactions</title>
</head>
<body>
<div class="container-sm text-center">
    <h1>Transactions of the account <%=bankAccount.getIban()%>
    </h1>
    <div class="card">
        <h3 class="card-header" style="background-color: aliceblue">Filter transactions</h3>
        <div class="card-body">
            <form:form modelAttribute="filter" action="/atm/checkTransactions" method="post">
                <div style="text-align: left">
                    <label for="timestampBegin">Filter by timestamp</label>
                    <form:input type="date" path="timestampBegin" id="timestampBegin"
                                name="timestampBegin"></form:input>
                    <form:input type="date" path="timestampEnd" id="timestampEnd" name="timestampEnd"></form:input> <br>
                    <label for="transactionOwner">Filter by transaction owner:</label>
                    <form:input path="transactionOwner" id="transactionOwner" name="transactionOwner" size="45"
                                maxlength="45"></form:input> <br>
                    <label for="beneficiaryIban">Filter by beneficiary IBAN:</label>
                    <form:input path="beneficiaryIban" id="beneficiaryIban" name="beneficiaryIban" size="34"
                                maxlength="34"></form:input> <br>
                    <label for="amount">Filter by amount (greater or equal than):</label>
                    <form:input type="number" path="amount" id="amount" name="amount"></form:input> <br>
                </div>
                <form:button type="submit" class="btn btn-primary">Filter</form:button>
                <button class="btn btn-danger" formmethod="get" formaction="/atm/checkTransactions">Remove filters</button>
            </form:form>

            <form action="/atm/enumerarAcciones" method="post">
                <button class="btn btn-secondary">Go back</button>
            </form>
        </div>
    </div>
    <table class="table table-hover" style="border-collapse: collapse; border: 1px solid; text-align: center; margin-top: 20px">
        <tr style="border: 1px solid; background-color: dodgerblue">
            <th rowspan="3" style="border: 1px solid">Timestamp</th>
            <th rowspan="3" style="border: 1px solid">Transaction owner</th>
            <th colspan="4" style="border: 1px solid">Currency exchange data</th>
            <th colspan="7" style="border: 1px solid">Payment data</th>
        </tr>
        <tr style="border: 1px solid; background-color: dodgerblue">
            <th rowspan="2" style="border: 1px solid">Initial amount</th>
            <th rowspan="2" style="border: 1px solid">Initial badge</th>
            <th rowspan="2" style="border: 1px solid">Final amount</th>
            <th rowspan="2" style="border: 1px solid">Final badge</th>
            <th colspan="2" style="border: 1px solid">Beneficiary</th>
            <th colspan="4" style="border: 1px solid">Currency exchange</th>
            <th rowspan="2" style="border: 1px solid">Amount</th>
        </tr>
        <tr style="background-color: dodgerblue">
            <th style="border: 1px solid">Beneficiary IBAN</th>
            <th style="border: 1px solid">Beneficiary name</th>
            <th style="border: 1px solid">Initial amount</th>
            <th style="border: 1px solid">Initial badge</th>
            <th style="border: 1px solid">Final amount</th>
            <th style="border: 1px solid">Final badge</th>
        </tr>
        <%
            for (TransactionDTO transaction : transactions) {
        %>
        <tr style="border: 1px solid">
            <td style="border: 1px solid; background-color: aliceblue"><%=transaction.getTimestamp()%>
            </td>
            <td style="border: 1px solid"><%=transaction.getClientByClientId().getName()%> <%=transaction.getClientByClientId().getSurname()%>
            </td>
            <%
                if (transaction.getCurrencyExchangeByCurrencyExchangeId() != null) {
                    CurrencyExchangeDTO currencyExchange = transaction.getCurrencyExchangeByCurrencyExchangeId();
            %>
            <td style="border: 1px solid; background-color: aliceblue"><%=currencyExchange.getInitialAmount()%>
            </td>
            <td style="border: 1px solid"><%=currencyExchange.getBadgeByInitialBadgeId().getName()%>
            </td>
            <td style="border: 1px solid; background-color: aliceblue"><%=currencyExchange.getFinalAmount()%>
            </td>
            <td style="border: 1px solid"><%=currencyExchange.getBadgeByFinalBadgeId().getName()%>
            </td>
            <%
            } else {
            %>
            <td style="border: 1px solid; background-color: aliceblue"></td>
            <td style="border: 1px solid"></td>
            <td style="border: 1px solid; background-color: aliceblue"></td>
            <td style="border: 1px solid"></td>
            <%
                }
            %>

            <%
                if (transaction.getPaymentByPaymentId() != null) {
                    PaymentDTO payment = transaction.getPaymentByPaymentId();
            %>
            <%
                if (payment.getBenficiaryByBenficiaryId() != null) {
                    BenficiaryDTO beneficiary = payment.getBenficiaryByBenficiaryId();
            %>
            <td style="border: 1px solid; background-color: aliceblue"><%=beneficiary.getIban()%>
            </td>
            <td style="border: 1px solid"><%=beneficiary.getName()%>
            </td>
            <%
            } else {
            %>
            <td style="border: 1px solid; background-color: aliceblue"></td>
            <td style="border: 1px solid"></td>
            <%
                }
            %>
            <%
                if (payment.getCurrencyExchangeByCurrencyExchangeId() != null) {
                    CurrencyExchangeDTO paymentCurrencyExchange = payment.getCurrencyExchangeByCurrencyExchangeId();
            %>
            <td style="border: 1px solid; background-color: aliceblue"><%=paymentCurrencyExchange.getInitialAmount()%>
            </td>
            <td style="border: 1px solid"><%=paymentCurrencyExchange.getBadgeByInitialBadgeId().getName()%>
            </td>
            <td style="border: 1px solid; background-color: aliceblue"><%=paymentCurrencyExchange.getFinalAmount()%>
            </td>
            <td style="border: 1px solid"><%=paymentCurrencyExchange.getBadgeByFinalBadgeId().getName()%>
            </td>
            <%
            } else {
            %>
            <td style="border: 1px solid; background-color: aliceblue"></td>
            <td style="border: 1px solid"></td>
            <td style="border: 1px solid; background-color: aliceblue"></td>
            <td style="border: 1px solid"></td>
            <%
                }
            %>
            <td style="border: 1px solid; background-color: aliceblue"><%=payment.getAmount()%>
            </td>
            <%
            }
            %>
        </tr>
        <%
            }
        %>
    </table>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
</body>
</html>
