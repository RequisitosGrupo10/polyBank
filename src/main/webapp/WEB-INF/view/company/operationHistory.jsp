<%@ page import="com.taw.polybank.entity.TransactionEntity" %>
<%@ page import="java.util.List" %>
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
<form:form>

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
            String currency = t.getPaymentByPaymentId().getBenficiaryByBenficiaryId().getBadge();
            if (t.getCurrencyExchangeByCurrencyExchangeId() != null) {
                currency = t.getCurrencyExchangeByCurrencyExchangeId().getBadgeByInitialBadgeId().getName();
            }
        %>
        <th><%=currency%></th>

        <td><%=t.getPaymentByPaymentId().getBenficiaryByBenficiaryId().getName()%></td>
        <td><%=t.getPaymentByPaymentId().getBenficiaryByBenficiaryId().getIban()%></td>
        <%
            Double receivedAmount = null;
            if(t.getCurrencyExchangeByCurrencyExchangeId() != null){
                receivedAmount = t.getCurrencyExchangeByCurrencyExchangeId().getFinalAmount();
            }else{
                receivedAmount = t.getPaymentByPaymentId().getAmount();
            }

        %>
        <td><%=receivedAmount%></td>
        <td><%=t.getPaymentByPaymentId().getBenficiaryByBenficiaryId().getBadge()%></td>

    </tr>

    <%
        }
    %>
</table>


</body>
</html>
