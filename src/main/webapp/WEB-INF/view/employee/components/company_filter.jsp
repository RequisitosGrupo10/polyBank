
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<fieldset>
    <form:form modelAttribute="filtro">
        <form:label path="name">Name:</form:label>
        <form:input path="name"></form:input>
        <form:button>Submit</form:button>
    </form:form>
</fieldset>