<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<style>
    table, th, td {
        border:1px solid black;
        text-align: center;
    }
</style>
<head>
    <title>Project 4 Web dashboard</title>
</head>
<body>
<h1 style="text-align: center">Project 4 Web dashboard</h1><br>
<h3>How many lookup and send message requests were sent?</h3>
<table>
    <thead>
    <tr>
        <th>Lookup</th>
        <th>Send message</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td><%= request.getAttribute("lookupCount")%></td>
        <td><%= request.getAttribute("sendMessageCount")%></td>
    </tr>
    </tbody>
</table>

<h3>List all the countries and the count of all the phone numbers looked up using the Twilio API specific to each country? </h3>
<table>
    <thead>
    <tr>
        <th>Country code</th>
        <th>Count</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.countryData}" var="country">
        <tr>
            <td>${country._id}</td>
            <td>${country.count}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<h3>List all phone numbers and the count of messages sent to those phone numbers?</h3>
<table>
    <thead>
    <tr>
        <th>Number</th>
        <th>Count</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.max}" var="maxNumber">
        <tr>
            <td>${maxNumber._id}</td>
            <td>${maxNumber.count}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<h3>Lookup Logs</h3>
<table>
    <thead>
    <tr>
        <th>Lookup Number</th>
        <th>Caller Name</th>
        <th>Caller type</th>
        <th>Country code</th>
        <th>Carrier name</th>
        <th>Carrier type</th>
        <th>Request Agent</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.lookupLog}" var="log">
        <tr>
            <c:choose>
                <c:when test="${log.phone_number != null}">
                    <td>${log.phone_number}</td>
                </c:when>
                <c:otherwise>
                    <td>Unknown</td>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${log.caller_name != null}">
                    <c:choose>
                        <c:when test="${log.caller_name.caller_name != null}">
                            <td>${log.caller_name.caller_name}</td>
                        </c:when>
                        <c:otherwise>
                            <td>Unknown</td>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${log.caller_name.caller_type != null}">
                            <td>${log.caller_name.caller_type}</td>
                        </c:when>
                        <c:otherwise>
                            <td>Unknown</td>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <td>Unknown</td>
                    <td>Unknown</td>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${log.country_code != null}">
                    <td>${log.country_code}</td>
                </c:when>
                <c:otherwise>
                    <td>Unknown</td>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${log.carrier != null}">
                    <c:choose>
                        <c:when test="${log.carrier.name != null}">
                            <td>${log.carrier.name}</td>
                        </c:when>
                        <c:otherwise>
                            <td>Unknown</td>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${log.carrier.type != null}">
                            <td>${log.carrier.type}</td>
                        </c:when>
                        <c:otherwise>
                            <td>Unknown</td>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <td>Unknown</td>
                    <td>Unknown</td>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${log.agent != null}">
                    <td>${log.agent}</td>
                </c:when>
                <c:otherwise>
                    <td>Unknown</td>
                </c:otherwise>
            </c:choose>
        </tr>
    </c:forEach>
    </tbody>
</table>

<h3>Send message Logs</h3>
<table>
    <thead>
    <tr>
        <th>To</th>
        <th>Date sent</th>
        <th>Message</th>
        <th>Request Agent</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.sendMessageLog}" var="smlog">
        <tr>
            <c:choose>
                <c:when test="${smlog.to != null}">
                    <td>${smlog.to}</td>
                </c:when>
                <c:otherwise>
                    <td>Unknown</td>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${smlog.date_created != null}">
                    <td>${smlog.date_created}</td>
                </c:when>
                <c:otherwise>
                    <td>Unknown</td>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${smlog.body != null}">
                    <td>${smlog.body}</td>
                </c:when>
                <c:otherwise>
                    <td>Unknown</td>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${smlog.agent != null}">
                    <td>${smlog.agent}</td>
                </c:when>
                <c:otherwise>
                    <td>Unknown</td>
                </c:otherwise>
            </c:choose>
        </tr>
    </c:forEach>
    </tbody>
</table><br><br>
</body>
</html>