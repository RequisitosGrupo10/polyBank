<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.taw.polybank.entity.CompanyEntity" %>
<%@ page import="com.taw.polybank.entity.BankAccountEntity" %><%--
<%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 07/04/2023
  Time: 23:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Exchange money of ${company.name}</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
    <%
        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        BankAccountEntity companyBankAccount = company.getBankAccountByBankAccountId();

    %>
</head>
<body>
<jsp:include page="corporateHeader.jsp"/>

<div id="transactionWindow">
    <h1>Money Exchange</h1>
    <p>Balance: <%=companyBankAccount.getBalance()%> <%=companyBankAccount.getBadgeByBadgeId().getName()%></p>

    <form:form modelAttribute="badge" method="post" action="/company/user/makeExchange">
        <form:label path="id">Desired currency*</form:label>
        <form:select path="id" items="${badgeList}" itemLabel="name" itemValue="id"/>
        <form:button class="prettyButton">Exchange</form:button>
    </form:form>
<p style="font-size: 6pt">* This action will exchange all available funds to desired currency</p>

</div>

<div id="blockedAccountWindow">
    <h1>Your account is not active</h1>
    <p>Contact your bank in order to activate your account.</p>
</div>

<script>

    const transactionWindow = document.getElementById("transactionWindow");
    const blockedAccountWindow = document.getElementById("blockedAccountWindow");
    const isActiveAccount = <%=companyBankAccount.getActive() == (byte) 1%>


        window.onload = function () {
            if (isActiveAccount == 1) {
                blockedAccountWindow.remove();
            } else {
                transactionWindow.remove();
            }
        };

</script>


</body>
</html>
