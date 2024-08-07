<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
    <title>Weather Data</title>
</head>
<body>
    <h1>Weather Data</h1>
    <table border="1">
        <tr>
            <th>Date</th>
            <th>SKY</th>
            <th>PTY</th>
            <th>TMP</th>
            <th>REH</th>
            <th>WSD</th>
        </tr>
        <c:forEach var="data" items="${shortDataList}">
            <tr>
                <td>${data.value.date} ${data.value.time}</td>
                <td>${data.value.SKY}</td>
                <td>${data.value.PTY}</td>
                <td>${data.value.TMP}</td>
                <td>${data.value.REH}</td>
                <td>${data.value.WSD}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>