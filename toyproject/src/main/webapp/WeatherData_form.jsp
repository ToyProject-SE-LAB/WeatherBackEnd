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
    <h1>현재 위치 날씨정보</h1>
    <h3>현재 위치: ${locationInfo.province} ${locationInfo.city} ${locationInfo.district}</h3>

    <table border="1">
        <tr>
            <th>Date</th>
            <th>SKY</th>
            <th>PTY</th>
            <th>POP</th>
            <th>TMP</th>
            <th>REH</th>
            <th>WSD</th>
            <th>TMN</th>
            <th>TMX</th>
        </tr>
        <c:forEach var="entry" items="${shortDataList}">
            <tr>
                <td>${entry.key}</td>
                <td>${entry.value.SKY}</td>
                <td>${entry.value.PTY}</td>
                <td>${entry.value.POP}</td>
                <td>${entry.value.TMP}</td>
                <td>${entry.value.REH}</td>
                <td>${entry.value.WSD}</td>
                <td>${entry.value.TMN}</td>
                <td>${entry.value.TMX}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>