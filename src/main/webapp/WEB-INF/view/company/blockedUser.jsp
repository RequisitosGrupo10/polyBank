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
</head>
<body>
<jsp:include page="corporateHeader.jsp" />

<c:if test="${message != null}" >
    <h4 style="color:red;">
            ${message}
    </h4>
</c:if>


<form method="post" action="/company/user/allegation">
    <label for="msg">Allegation message</label>
    <br/>
    <textarea cols="50" rows="4" maxlength="100" name="msg" id="msg"></textarea>
    <br/>
    <button class="prettyButton" type="submit" name="submit" value="submit">Submit allegation</button>
</form>
</body>
</html>
