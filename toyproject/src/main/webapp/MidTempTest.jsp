<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
    <title>Mid Temp Data</title>
</head>
<body>
    <h1>Mid Temp Data</h1>
 
    <table border="1">
        <tr>
            <th>Date</th>
            <th>최고</th>
            <th>최저</th>
        </tr>
        <c:forEach var="data" items="${tempDataList}">
            <tr>
                <td>${data.date}</td>
                <td>${data.taMax}</td>
                <td>${data.taMin}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>