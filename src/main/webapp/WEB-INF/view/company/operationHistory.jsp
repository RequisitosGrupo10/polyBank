<%@ page import="com.taw.polybank.entity.TransactionEntity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.entity.CurrencyExchangeEntity" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 08/04/2023
  Time: 11:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%
        List<TransactionEntity> transactionList = (List<TransactionEntity>) request.getAttribute("transactionList");
    %>

    <title>Operation history of ${client.name}</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
</head>
<body>
<jsp:include page="corporateHeader.jsp"/>
<h2>Operation history of ${company.name} company</h2>

<h3>Filters</h3>
<form:form modelAttribute="transactionFilter" method="post" action="/company/user/operationHistory">
    <form:label path="senderId">Sender id:</form:label>
    <form:input path="senderId" size="45" maxlength="45" class="formElement" onclick="this.select()"/>
    <form:label path="recipientName">Recipient name</form:label>
    <form:input path="recipientName" size="45" maxlength="45" class="formElement" onclick="this.select()"/>
    <br/>
    <form:label path="minAmount">Amount send greater than</form:label>
    <form:input type="number" path="minAmount" size="10" maxlength="10" class="formElement" onclick="this.select()"/>
    <form:label path="maxAmount">Amount send smaller than</form:label>
    <form:input type="number" path="maxAmount" size="10" maxlength="10" class="formElement" onclick="this.select()"/>
    <br/>
    <form:label path="transactionAfter">Date after</form:label>
    <form:input type="date" path="transactionAfter"/>
    <form:label path="transactionAfter">Date before</form:label>
    <form:input type="date" path="transactionBefore"/>

    <br/>
    <form:button class="prettyButton" type="submit" value="filter" name="filter">Filter</form:button>
</form:form>

<table border="1">
    <tr>
        <th rowspan="2">Time</th>
        <th colspan="5">Sender</th>
        <th colspan="4">Recipient</th>
    </tr>
    <tr>
        <th>Name</th>
        <th>Surname</th>
        <th>ID</th>
        <th>Amount send</th>
        <th>Currency</th>

        <th>Name</th>
        <th>Iban</th>
        <th>Amount</th>
        <th>Currency</th>

    </tr>

    <%
        for (TransactionEntity t : transactionList) {
    %>

    <tr>
        <td><%=t.getTimestamp()%></td>

        <td><%=t.getClientByClientId().getName()%></td>
        <td><%=t.getClientByClientId().getSurname()%></td>
        <td><%=t.getClientByClientId().getDni()%></td>
        <td><%=t.getPaymentByPaymentId().getAmount()%></td>
        <%
            CurrencyExchangeEntity currencyExchange = t.getCurrencyExchangeByCurrencyExchangeId();
            String currency = t.getPaymentByPaymentId().getBenficiaryByBenficiaryId().getBadge();
            if (currencyExchange!= null) {
                currency = currencyExchange.getBadgeByInitialBadgeId().getName();
            }
        %>
        <th><%=currency%></th>

        <td><%=t.getPaymentByPaymentId().getBenficiaryByBenficiaryId().getName()%></td>
        <td><%=t.getPaymentByPaymentId().getBenficiaryByBenficiaryId().getIban()%></td>
        <%
            Double receivedAmount = null;
            if(currencyExchange != null){
                receivedAmount = currencyExchange.getFinalAmount();
            }else{
                receivedAmount = t.getPaymentByPaymentId().getAmount();
            }

        %>
        <td><%=receivedAmount%></td>
        <%
            String finalBadge = "";
            if(currencyExchange != null){
                finalBadge = currencyExchange.getBadgeByFinalBadgeId().getName();
            }else {
                finalBadge = t.getPaymentByPaymentId().getBenficiaryByBenficiaryId().getBadge();
            }
        %>
        <td><%=finalBadge%></td>

    </tr>

    <%
        }
    %>
</table>


</body>
</html>
