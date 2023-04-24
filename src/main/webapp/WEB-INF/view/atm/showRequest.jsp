<%@ page import="com.taw.polybank.entity.RequestEntity" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 15/04/2023
  Time: 11:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<RequestEntity> requests = (List<RequestEntity>) request.getAttribute("requests");
%>
<html>
<head>
    <title>Polybank - Unban Request</title>
</head>
<body>
    <table style="border-collapse: collapse; border: 1px solid">
        <tr style="border: 1px solid">
            <th style="border: 1px solid">Client who requested unban</th>
            <th style="border: 1px solid">Bank account banned</th>
            <th style="border: 1px solid">Employee managing the petition</th>
            <th style="border: 1px solid">Timestamp</th>
            <th style="border: 1px solid">Description</th>
            <th style="border: 1px solid">Approved</th>
            <th style="border: 1px solid">Solved</th>
        </tr>
        <%
            for (RequestEntity unbanRequest : requests){
        %>
        <tr style="border: 1px solid">
            <td style="border: 1px solid"><%=unbanRequest.getClientByClientId().getName() +" "+ unbanRequest.getClientByClientId().getSurname()%></td>
            <td style="border: 1px solid"><%=unbanRequest.getBankAccountByBankAccountId().getIban()%></td>
            <td style="border: 1px solid"><%=unbanRequest.getEmployeeByEmployeeId().getName()%></td>
            <td style="border: 1px solid"><%=unbanRequest.getTimestamp()%></td>
            <td style="border: 1px solid"><%=unbanRequest.getDescription()%></td>
            <td style="border: 1px solid">
            <%
                if(unbanRequest.getApproved() != null){
            %>
                <%=unbanRequest.getApproved() == (byte) 1 ? "True" : "False"%>
            <%
                }
            %>
            </td>
            <td style="border: 1px solid"><%=unbanRequest.getSolved() == (byte) 1 ? "True" : "False"%></td>
        </tr>
        <%
            }
        %>
    </table>
    <form action="/atm/enumerarAcciones" method="post">
        <button>Go back</button>
    </form>
</body>
</html>
