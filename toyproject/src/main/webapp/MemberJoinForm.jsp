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
	padding: 10px;
}
</style>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			console.log("JQUERY!");
		});
		
		function idCheck() {
            console.log("idCheck() 호출");
            var idVal = $('#inputId').val();
            if (!idVal) {
                alert("아이디를 입력하세요.");
                return;
            }
            console.log("idVal : " + idVal);

            $.ajax({
                type: "get",
                url: "${pageContext.request.contextPath}/memberIdCheck",
                data: { "inputId": idVal },
                async: false,
                success: function (result) {
                    console.log("확인결과 : " + result);
                    $('#idCheckResult').text(result);
                },
                error: function () {
                    console.log('중복확인 요청 실패!');
                    $('#idCheckResult').text('중복확인 요청 실패!');
                }
            });
        }
	
		function combineEmail() {
		    var emailId = $('#emailId').val();
		    var emailDomain = $('#domainSelect').val();
		    var fullEmail = emailId + '@' + emailDomain;
		    $('#fullEmail').val(fullEmail);
		}
	
		function selectDomain(domain) {
		    $('#emailDomain').val(domain);
		    combineEmail();
		}
	</script>
</head>
<body>
	<h1>MemberJoinForm.jsp</h1>
	<hr>
	<a href="MainPage.jsp">메인페이지</a>
	<a href="MemberLoginForm.jsp">로그인</a>
	<a href="MemberJoinForm.jsp">회원가입</a>
	<hr>
	<form action="memberJoin" method="post">
		<table>
			<tr>
				<th colspan="2">회원가입</th>
			</tr>
			<tr>
				<th>아이디</th>
				<td><input type="text" name="id" id="inputId"></td>
				<td> 
					<button type="button" onclick="idCheck()">중복확인</button>
					<span id="idCheckResult"></span> <!-- 중복 확인 결과를 표시할 영역 -->
				</td>
			</tr>
			<tr>
				<th>비밀번호</th>
				<td><input type="password" name="pw"></td>
			</tr>
			<tr>
				<th>이름</th>
				<td><input type="text" name="name"></td>
			</tr>
			<tr>
                <th>성별</th>
                <td>
                    <label>
                        <input type="radio" name="gender" value="male" required> 남성
                    </label>
                    <label>
                        <input type="radio" name="gender" value="female" required> 여성
                    </label>
                </td>
            </tr>
			<tr>
				<th>생년월일</th>
				<td><input type="date" name="birth"></td>
			</tr>
			<tr>
                <th>이메일</th>
                <td>
                    <input type="text" name="emailId" id="emailId" oninput="combineEmail()" required>
                    @
                    <input type="text" name="emailDomain" id="emailDomain" oninput="combineEmail()" required>
                    <select id="domainSelect" onchange="selectDomain(this.value)">
                        <option value="">직접입력</option>
                        <option value="naver.com">네이버</option>
                        <option value="daum.net">다음</option>
                        <option value="google.com">구글</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <input type="hidden" id="fullEmail" name="email">
                </td>
            </tr>
			<tr>
				<th colspan="3">
					<input type="submit" value="회원가입">
				</th>
			</tr>
		</table>
	</form>
</body>

</html>