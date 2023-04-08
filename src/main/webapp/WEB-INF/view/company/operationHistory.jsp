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
        <th>Transaction</th>
        <th>CurrencyExchange</th>
    </tr>
    <tr>

    </tr>

    <%
        for (TransactionEntity t : transactionList) {
    %>

    <tr>
      <td><%=t.getClientByClientId().getName()%></td>
      <td><%=t.getClientByClientId().getSurname()%></td>
      <td><%=t.getClientByClientId().getDni()%></td>
      <td><%=t.getTimestamp().toLocalDateTime()%></td>
      <td><%=t.getCurrencyExchangeByCurrencyExchangeId()%></td>
    </tr>

    <%
        }
    %>
</table>


</body>
</html>
