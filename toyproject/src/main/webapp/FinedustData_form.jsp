<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Finedust Data</title>
</head>
<body>
	<h1>Finedust Data</h1>
    <table border="1">
        <tr>
            <th>DateTime</th>
            <th>pm10Grade</th>
            <th>pm25Grade</th>
        </tr>
        <c:forEach var="data" items="${dataList}">
            <tr>
                <td>${data.date}</td>
                <td>${data.pm10Grade}</td>
                <td>${data.pm25Grade}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>