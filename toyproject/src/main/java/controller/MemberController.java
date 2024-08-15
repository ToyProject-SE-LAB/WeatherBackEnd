package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dto.MemberDto;
import service.MemberService;

@WebServlet({"/memberLogin", "/memberJoin", "/memberIdCheck", "/memberInfo","/memberList", "/memberDelete", "/memberUpdate", "/memberLogout"})
public class MemberController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MemberService msvc;

    @Override
    public void init() throws ServletException {
        msvc = new MemberService(); // 서블릿 초기화 시 한 번만 생성
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getServletPath();
        System.out.println("url : " + url);

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        HttpSession session = request.getSession();

        switch (url) {
            case "/memberLogin":
                handleLogin(request, response, session);
                break;

            case "/memberJoin":
                handleJoin(request, response);
                break;

            case "/memberIdCheck":
                handleMemberIdCheck(request, response);
                break;

            case "/memberInfo":
                handleMemberInfo(request, response, session);
                break;
                
            case "/memberList":
            	handleMemberList(request, response);
            	break;
            	
            case "/memberDelete":
            	handleMemberDelete(request, response);
            	break;
            	
            case "/memberUpdate":
            	handleMemberUpdate(request, response);
            	break;
            	
            case "/memberLogout":
            	handleMemberLogout(request, response, session);
            	break;
            	
            default:
                response.sendRedirect("404.jsp");
                break;
        }
    }

 // 공통 스크립트 출력 메서드
    private void sendScriptResponse(HttpServletResponse response, String message, String redirectUrl) throws IOException {
        response.getWriter().print("<script>");
        response.getWriter().print("alert('" + message + "');");
        if (redirectUrl != null) {
            response.getWriter().print("location.href='" + redirectUrl + "';");
        } else {
            response.getWriter().print("history.back();");
        }
        response.getWriter().print("</script>");
    }

	// 로그인 메소드
    private void handleLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String inputId = request.getParameter("id");
        String inputPw = request.getParameter("pw");
        
        if (inputId == null || inputPw == null) {
            sendScriptResponse(response, "아이디와 비밀번호를 입력해주세요.", null);
            return;
        }

        String loginId = msvc.memberLogin(inputId, inputPw);

        if (loginId != null) {
            System.out.println("로그인 성공");
            session.setAttribute("loginId", loginId);
            response.sendRedirect("MainPage.jsp");
        } else {
        	System.out.println("로그인 성공");
            sendScriptResponse(response, "아이디 또는 비밀번호가 일치하지 않습니다.", null);
        }
    }

    // 회원가입 메소드
    private void handleJoin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String joinId = request.getParameter("id");
        String joinPw = request.getParameter("pw");
        String joinName = request.getParameter("name");
        String joinGender = request.getParameter("gender");
        String joinBirth = request.getParameter("birth");
        String joinEmailId = request.getParameter("emailId");
        String joinEmailDomain = request.getParameter("emailDomain");

        if (joinId == null || joinPw == null || joinName == null || joinGender == null || joinBirth == null || joinEmailId == null || joinEmailDomain == null) {
            sendScriptResponse(response, "모든 필드를 채워주세요.", null);
            return;
        }
        
        MemberDto joinMember = new MemberDto();
        joinMember.setId(joinId);
        joinMember.setPw(joinPw);
        joinMember.setName(joinName);
        joinMember.setGender(joinGender);
        joinMember.setBirth(joinBirth);
        joinMember.setEmail(joinEmailId+"@"+joinEmailDomain);
        
        int joinResult = msvc.memberJoin(joinMember);

        if (joinResult > 0) {
            response.sendRedirect("MainPage.jsp");
        } else {
            sendScriptResponse(response, "회원가입에 실패하였습니다.", null);
        }
    }

    // 아이디 중복 확인 메소드
    private void handleMemberIdCheck(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String inputId2 = request.getParameter("inputId");
    	
    	if (inputId2 == null || inputId2.trim().isEmpty()) {
            response.getWriter().write("아이디를 입력해주세요.");
            return;
        }
    	
    	boolean isAvailable = msvc.memberIdCheck(inputId2);
        if (isAvailable) {
            response.getWriter().write("사용 가능한 아이디입니다.");
        } else {
            response.getWriter().write("이미 사용 중인 아이디입니다.");
        }
    }
    
    // 회원정보 메소드
    private void handleMemberInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        String sessionLoginId = (String) session.getAttribute("loginId");

        if (sessionLoginId == null) {
            System.out.println("세션에서 로그인 ID를 가져올 수 없습니다.");
            response.sendRedirect("login.jsp");
            return;
        }

        MemberDto mInfo = msvc.memberInfo(sessionLoginId);
        if (mInfo == null) {
            System.out.println("회원 정보를 조회할 수 없습니다.");
            request.setAttribute("errorMessage", "회원 정보를 찾을 수 없습니다.");
            RequestDispatcher errorDispatcher = request.getRequestDispatcher("error.jsp");
            errorDispatcher.forward(request, response);
            return;
        }

        request.setAttribute("memberInfo", mInfo);
        RequestDispatcher dispatcher = request.getRequestDispatcher("MemberInfo.jsp");
        dispatcher.forward(request, response);
    }

    // 회원목록 조회 메소드
    private void handleMemberList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
        try {
        	ArrayList<MemberDto> memberList = msvc.memberList();
            
            // 회원 목록을 request 속성으로 설정
            request.setAttribute("memberList", memberList);
            
            // 회원 목록 페이지로 포워딩
            RequestDispatcher dispatcher = request.getRequestDispatcher("MemberList.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "요청 처리 중 오류가 발생했습니다.");
        }
    }

    // 회원정보 삭제 메소드
    private void handleMemberDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		String delId = request.getParameter("delId");
		
		int deleteResult = msvc.memberDelete(delId);
		if (deleteResult > 0) {
	        System.out.println("삭제 처리 결과 : 성공");
	        response.sendRedirect("memberList"); // 삭제 성공 시 회원 목록 페이지로 리다이렉트
	    } else {
	        System.out.println("삭제 처리 결과 : 실패");
	        sendScriptResponse(response, "회원 삭제에 실패하였습니다.", null);
	    }
    }
    
    // 회원정보 수정
    private void handleMemberUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
    	String updateId = request.getParameter("id"); 
		String updatePw = request.getParameter("pw");
		String updateName = request.getParameter("name");
		String updateGender = request.getParameter("gender");
		String updateBirth = request.getParameter("birth");	
		String updateEmailId = request.getParameter("emailId");	
		String updateEmailDomain = request.getParameter("emailDomain");	
		
		// 수정: 1, 수정실패: 0
		MemberDto updateMember = new MemberDto();
		updateMember.setId(updateId);
		updateMember.setPw(updatePw);
		updateMember.setName(updateName);
		updateMember.setGender(updateGender);
		updateMember.setBirth(updateBirth);
		updateMember.setEmail(updateEmailId + "@" + updateEmailDomain);
		
		int updateResult = msvc.updateMember(updateMember);
		
		// 회원정보 확인 페이지로 포워딩
		if(updateResult > 0) {
			sendScriptResponse(response, "회원정보가 수정되었습니다.", "memberInfo");
		} else {
			sendScriptResponse(response, "회원정보 수정에 실패하였습니다.", null);
		}
    	
    }
    
    // 로그아웃
    private void handleMemberLogout(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException  {
    	// 세션 무효화
    	session.invalidate();
    	
        String contextPath = request.getContextPath();
		response.sendRedirect(contextPath+"/MainPage.jsp?msg="
				+URLEncoder.encode("로그아웃 되었습니다.","UTF-8"));

    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}