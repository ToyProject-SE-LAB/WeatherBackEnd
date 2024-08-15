<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dto.MemberDto" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원 정보</title>
    <style>
        table, th, td {
            border-collapse: collapse;
        }

        th, td {
            border: 2px solid black;
            padding: 10px;
        }
    </style>
</head>
<body>
    <h1>회원 정보</h1>
    <hr>
    
    <% 
    MemberDto memberInfo = (MemberDto) request.getAttribute("memberInfo");
    
    if (memberInfo != null) { 
        // 이메일을 '@'를 기준으로 분리
        String[] emailParts = memberInfo.getEmail().split("@");
        String emailId = emailParts.length > 0 ? emailParts[0] : "";
        String emailDomain = emailParts.length > 1 ? emailParts[1] : "";
	%>
        <form action="memberUpdate" method="post">
            <table>
                <tr>
                    <th colspan="2">회원정보</th>
                </tr>
                <tr>
                    <th>아이디</th>
                    <td><input type="text" name="id" value="<%= memberInfo.getId() %>" readonly></td>
                </tr>
                <tr>
                    <th>비밀번호</th>
                    <td><input type="text" name="pw" value="<%= memberInfo.getPw() %>"></td>
                </tr>
                <tr>
                    <th>이름</th>
                    <td><input type="text" name="name" value="<%= memberInfo.getName() %>"></td>
                </tr>
                <tr>
                    <th>성별</th>
                    <td><input type="text" name="gender" value="<%= memberInfo.getGender() %>"></td>
                </tr>
                <tr>
                    <th>생년월일</th>
                    <td><input type="date" name="birth" value="<%= memberInfo.getBirth() %>"></td>
                </tr>
                <tr>
                    <th>이메일</th>
                    <td>
                        <input type="text" name="emailId" id="emailId" value="<%= emailId %>">
                        @
                        <input type="text" name="emailDomain" id="emailDomain" value="<%= emailDomain %>">
                    </td>
                </tr>
                <tr>
                    <th colspan="2">
                        <input type="submit" value="정보 수정">
                    </th>
                </tr>
            </table>
        </form>
    <% 
        } else { 
    %>
        <p>회원 정보가 없습니다.</p>
    <% 
        } 
    %>
</body>
</html>
