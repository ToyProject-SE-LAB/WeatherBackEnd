<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>미세먼지 </title>
</head>
<body>
	<h3>미세먼지 데이터</h3>
	<table border="1">
        <thead>
            <tr>
                <th>측정일</th>
                <th>미세먼지(PM10 Grade)</th>
                <th>초미세먼지(PM2.5 Grade)</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="data" items="${dataList}">
                <tr>
                    <td>${data[0]}</td>
                    <td>${data[1]}</td>
                    <td>${data[2]}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>