package service;

import java.util.ArrayList;

import dao.MemberDao;
import dto.MemberDto;

public class MemberService {

    private MemberDao dao;

    // 기본 생성자
    public MemberService() {
        dao = new MemberDao(); // MemberDao 객체를 생성자에서 초기화
    }

    // 회원가입 기능 메소드
    public int memberJoin(MemberDto joinMember) {
        System.out.println("MemberService memberJoin() 호출");
        
        int insertResult = 0;
        try {
            insertResult = dao.insertMemberJoin(joinMember);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("회원가입 중 오류 발생: " + e.getMessage());
        }
        return insertResult;
    }

    // 로그인 기능 메소드
    public String memberLogin(String inputId, String inputPw) {
        System.out.println("MemberService memberLogin() 호출");
        
        String loginId = dao.selectMemberLogin(inputId, inputPw);
        return loginId;
    }
    
    // 아이디 중복확인 메소드
    public boolean memberIdCheck(String inputId2) {
		System.out.println("MemberService memberIdCheck() 호출");

		MemberDto mInfo = dao.selectMemberIdInfo(inputId2);
		
		// mInfo가 null이 아니면 중복된 아이디가 존재하는 것
        return mInfo == null;
	}

    // 회원정보 조회 메소드
    public MemberDto memberInfo(String sessionLoginId) {
        System.out.println("MemberService memberInfo() 호출");
        
        MemberDto mInfo = null;
        try {
            mInfo = dao.selectMemberInfo(sessionLoginId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("회원정보 조회 중 오류 발생: " + e.getMessage());
        }
        return mInfo;
    }
    
    // 회원목록 조회 메소드
    public ArrayList<MemberDto> memberList() {
		System.out.println("MemberService memberList() 호출");
		
		ArrayList<MemberDto> memberList = dao.selectMemberList();
		return memberList;
	}
    
    // 회원정보 삭제 메소드
    public int memberDelete(String delId) {
    	return dao.memberDelete(delId);
    }
    
    // 회원정보 수정 메소드
  	public int updateMember(MemberDto updateMember) {
  		System.out.println("MemberService updateMember() 호출");
  		
  		int updateResult = dao.updateMemberInfo(updateMember);
  		return updateResult;
  	}
}
