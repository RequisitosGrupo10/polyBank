<%@ page import="com.taw.polybank.dto.TransactionDTO" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<table class="table">
    <tr>
        <th scope="col">Id</th>
        <th scope="col">Timestamp</th>
        <th scope="col">Type</th>
        <th scope="col">Amount</th>
    </tr>
    <% List<TransactionDTO> transactions = (List<TransactionDTO>) request.getAttribute("transactions");%>
    <% for (TransactionDTO transactionDTO : transactions) { %>
    <tr>
        <td><%= transactionDTO.getId()%></td>
        <td><%= transactionDTO.getTimestamp()%></td>
        <% if ( transactionDTO.getPaymentByPaymentId() != null) { %>
        <td>Payment</td>
        <td><%=transactionDTO.getPaymentByPaymentId().getAmount()%></td>
        <% } else{ %>
        <td>Currency Exchange</td>
        <td><%=transactionDTO.getCurrencyExchangeByCurrencyExchangeId().getInitialAmount()%></td>
        <%}%>
    </tr>
    <% } %>
</table>