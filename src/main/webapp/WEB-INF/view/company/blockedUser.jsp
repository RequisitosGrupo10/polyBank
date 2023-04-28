<%@ page import="com.taw.polybank.entity.RequestEntity" %>
<%@ page import="java.util.List" %>

<%--
  Created by IntelliJ IDEA.
  User: Illya Rozumovskyy
  Date: 06/04/2023
  Time: 22:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome ${client.name}</title>
    <link rel="stylesheet" type="text/css" href="../../../commonStyle.css">

    <%
        List<RequestEntity> requests = (List<RequestEntity>) request.getAttribute("requests");
        boolean alreadyRequested = requests.size() > 0;
    %>

</head>
<body>

<c:if test="${message != null}" >
    <h4 style="color:red;">
            ${message}
    </h4>
</c:if>

<div id="newRequest">

    <form method="post" action="/company/user/allegation">
        <label for="msg">Allegation message</label>
        <br/>
        <textarea cols="50" rows="4" maxlength="100" name="msg" id="msg"></textarea>
        <br/>
        <button class="prettyButton" type="submit" name="submit" value="submit">Submit allegation</button>
    </form>

</div>

<div id="listRequests">
    <h2>Your requests:</h2>
    <table border="1">
        <tr>
            <th>Time</th>
            <th>Description</th>
            <th>Employee</th>
        </tr>
        <%
            for(RequestEntity r : requests){
        %>
        <tr>
            <td><%=r.getTimestamp()%></td>
            <td><%=r.getDescription()%></td>
            <td><%=r.getEmployeeByEmployeeId().getName()%></td>
        </tr>
        <%
            }
        %>
    </table>
</div>

<a class="prettyButton" href="/company/user/logout">Exit</a>

<script>
    const newRequest = document.getElementById("newRequest");
    const listRequest = document.getElementById("listRequests")
    const showList = <%=alreadyRequested%>

    window.addEventListener('load', requestForm, false);

    function requestForm(){
        if(showList) {
            newRequest.style.setProperty("display", "none");
        }else {
            listRequest.style.setProperty("display", "none");
        }
    };
</script>

</body>
</html>
