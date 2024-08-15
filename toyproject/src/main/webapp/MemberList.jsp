<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
table, th, td {
	border-collapse: collapse;
		border: 2px solid black;
}

th, td {
	padding: 10px;
}
</style>
<script type="text/javascript">
	function deleteMember(delId) {
		console.log("deleteMember() 호출");
		console.log("delId: " + delId);
		location.href = "memberDelete?delId=" + delId;
	}
</script>
</head>
<body>
	<h1>MemberList.jsp</h1>
	<hr>
	<a href="MainPage.jsp">메인페이지</a>
	<hr>
	<table>
		<tr>
			<th>아이디</th>
			<th>비밀번호</th>
			<th>이름</th>
			<th>성별</th>
			<th>생년월일</th>
			<th>이메일</th>
			<th>삭제</th>
		</tr>
		<c:forEach items="${requestScope.memberList }" var="member">
		<tr>
			<td>${member.id}</td>
			<td>${member.pw}</td>
			<td>${member.name}</td>
			<td>${member.gender}</td>
			<td>${member.birth}</td>
			<td>${member.email}</td>
			<td>
                <button onclick="deleteMember('${member.id}')">삭제</button>
            </td> 	
		</tr>
		</c:forEach>		
	</table>
</body>
</html>