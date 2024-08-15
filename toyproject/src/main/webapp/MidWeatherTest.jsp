<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
    <title>Mid Weather Data</title>
</head>
<body>
    <h1>Mid Weather Data</h1>
    <h3>현재 위치: ${locationInfo.province} ${locationInfo.city} ${locationInfo.district} ${locationInfo.midWeatherCode}</h3>
    <table border="1">
        <tr>
            <th>Date</th>
            <th>SKY</th>
            <th>POP</th>
        </tr>
        <c:forEach var="data" items="${weatherDataList}">
            <tr>
                <td>${data.date}</td>
                <td>${data.sky}</td>
                <td>${data.pop}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>