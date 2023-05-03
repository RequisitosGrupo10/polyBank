<%@ page import="java.util.List" %>
<%@ page import="com.taw.polybank.dto.RequestDTO" %><%--
  Created by IntelliJ IDEA.
  User: lucia
  Date: 15/04/2023
  Time: 11:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<RequestDTO> requests = (List<RequestDTO>) request.getAttribute("requests");
%>
<html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
<head>
    <title>Polybank - Unban Requests</title>
</head>
<body>
<div class="container-sm text-center">
    <h1>Unban requests</h1>
    <table class="table table-hover" style="border-collapse: collapse; border: 1px solid; text-align: center">
        <tr style="border: 1px solid; background-color: dodgerblue">
            <th style="border: 1px solid">Client who requested unban</th>
            <th style="border: 1px solid">Bank account banned</th>
            <th style="border: 1px solid">Employee managing the petition</th>
            <th style="border: 1px solid">Timestamp</th>
            <th style="border: 1px solid">Description</th>
            <th style="border: 1px solid">Approved</th>
            <th style="border: 1px solid">Solved</th>
        </tr>
        <%
            for (RequestDTO unbanRequest : requests) {
        %>
        <tr style="border: 1px solid">
            <td style="border: 1px solid; background-color: aliceblue"><%=unbanRequest.getClientByClientId().getName() + " " + unbanRequest.getClientByClientId().getSurname()%>
            </td>
            <td style="border: 1px solid"><%=unbanRequest.getBankAccountByBankAccountId().getIban()%>
            </td>
            <td style="border: 1px solid; background-color: aliceblue"><%=unbanRequest.getEmployeeByEmployeeId().getName()%>
            </td>
            <td style="border: 1px solid"><%=unbanRequest.getTimestamp()%>
            </td>
            <td style="border: 1px solid; background-color: aliceblue"><%=unbanRequest.getDescription()%>
            </td>
            <td style="border: 1px solid">
                <%
                    if (unbanRequest.isSolved()) {
                %>
                <%=unbanRequest.isApproved() ? "True" : "False"%>
                <%
                    }
                %>
            </td>
            <td style="border: 1px solid; background-color: aliceblue"><%=unbanRequest.isSolved() ? "True" : "False"%>
            </td>
        </tr>
        <%
            }
        %>
    </table>
    <form action="/atm/enumerarAcciones" method="post">
        <button class="btn btn-secondary">Go back</button>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
</body>
</html>
