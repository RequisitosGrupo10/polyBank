<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<fieldset>
    <form:form modelAttribute="filtro">
        <form:label path="type">Type Of Transaction</form:label>
        <form:select multiple="false" path="type">
            <form:option value="" label="" />
            <form:option value="Payment" label="Payment"/>
            <form:option value="CurrencyExchange" label="Currency Exchange"/>
        </form:select>
        <br>
        <form:label path="amount">Minimum amount</form:label>
        <form:input path="amount"></form:input>
        <br>
        <form:label path="sorting">Sort by:</form:label>
        <br>
        <span class="px-2">Ascendant date</span><form:radiobutton path="sorting" value="timestamp_asc"></form:radiobutton>
        <br>
        <span class="px-2">Descendant date</span><form:radiobutton path="sorting" value="timestamp_desc"></form:radiobutton>
        <br>
        <form:button>Submit</form:button>
    </form:form>
</fieldset>