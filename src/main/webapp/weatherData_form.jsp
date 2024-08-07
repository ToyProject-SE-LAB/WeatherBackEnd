<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>날씨</title>
</head>
<body>
	<h3>날씨 데이터</h3>
	<table border="1">
        <thead>
            <tr>
                <th>날짜</th>
                <th>온도</th>
                <th>강수확률</th>
                <th>풍속</th>
                <th>습도</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="data" items="${dataList}">
                <tr>
                    <td>${data[0]}</td>
                    <td>${data[1]}</td>
                    <td>${data[2]}</td>
                    <td>${data[3]}</td>
                    <td>${data[4]}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>