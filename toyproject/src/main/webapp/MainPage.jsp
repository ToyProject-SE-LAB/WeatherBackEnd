<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Main Page</title>
</head>
<body>
    <% 
        // 로그인 ID를 세션에서 가져옴
        String loginId = (String) session.getAttribute("loginId");
    %>

    <h1>MainPage.jsp</h1>
    <hr>
    <a href="MainPage.jsp">메인페이지</a>
    
    <% if (loginId != null) { %>
        <!-- 로그인된 사용자를 위한 링크 -->
        <a href="memberInfo">내 정보 확인</a>
        <a href="memberList">회원목록</a>
        <a href="memberLogout?loginId=<%=loginId %>">로그아웃</a>
    <% } else { %>
        <!-- 비로그인 사용자를 위한 링크 -->
        <a href="MemberLoginForm.jsp">로그인</a>
        <a href="MemberJoinForm.jsp">회원가입</a>
    <% } %>
    
    <hr>
    
    <!-- 로그인 ID를 표시 -->
    <h2>로그인 ID: <%= loginId != null ? loginId : "로그인 필요" %></h2>
    <h2>${sessionScope.loginId}</h2>
</body>
</html>
