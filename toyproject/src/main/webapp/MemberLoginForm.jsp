<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
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
	<h1>MemberLoginForm.jsp</h1>
	<form action="${pageContext.request.contextPath }/memberLogin" method="post" onsubmit="return loginFormCheck(this)">
		<table>
				<tr>
					<th colspan="2">로그인페이지</th>
				</tr>
				<tr>
					<th>아이디</th>
					<td>
					<input type="text" name="id">
					</td>
				</tr>
				<tr>
					<th>비밀번호</th>
					<td><input type="password" name="pw"></td>
				</tr>
				
				<tr>
					<th colspan="2">
					<input type="submit" value="로그인">
					</th>
				</tr>
			</table>
	</form>
</body>
</html>