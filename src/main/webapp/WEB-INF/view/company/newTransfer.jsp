<%@ page import="com.taw.polybank.entity.CompanyEntity" %>
<%@ page import="com.taw.polybank.entity.BankAccountEntity" %><%--
  Created by IntelliJ IDEA.
  User: panva
  Date: 07/04/2023
  Time: 12:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>New Transaction for ${company.name}</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">
    <%
        CompanyEntity company = (CompanyEntity) session.getAttribute("company");
        BankAccountEntity companyBankAccount = company.getBankAccountByBankAccountId();

    %>
</head>
<body>
<jsp:include page="corporateHeader.jsp"/>


<div id="transactionWindow">
  <h1>Make Transfer</h1>
    <p>Balance: <%=companyBankAccount.getBalance()%> <%=companyBankAccount.getBadgeByBadgeId().getName()%></p>
    <form action="/company/user/processTransfer" method="post">
        <label for="beneficiary">Name of recipient: </label>
        <input id="beneficiary" type="text" name="beneficiary" maxlength="45" size="45"/>
        <br/>
        <label for="iban">IBAN of recipient: </label>
        <input id="iban" type="text" name="iban" maxlength="34" size="34"/>
        <br/>
        <label for="amount">Amount: </label>
        <input id="amount" type="text" name="amount" maxlength="10" size="10">
        <br/>
        <button class="prettyButton" type="submit" >Make transaction</button>

    </form>
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
