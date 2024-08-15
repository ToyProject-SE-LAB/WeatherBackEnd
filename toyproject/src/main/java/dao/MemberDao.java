package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.MemberDto;

public class MemberDao {

    // 데이터베이스 연결 메소드
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/toyproject", "root", "3907");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC 드라이버 로딩 실패", e);
        }
    }

    // 회원가입 insert 메소드
    public int insertMemberJoin(MemberDto joinMember) {        
        String sql = "INSERT INTO members(id, pw, name, gender, birth, email) VALUES(?, ?, ?, ?, ?, ?)";
        int insertResult = 0;

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, joinMember.getId());
            pstmt.setString(2, joinMember.getPw());
            pstmt.setString(3, joinMember.getName());
            pstmt.setString(4, joinMember.getGender());
            pstmt.setString(5, joinMember.getBirth());
            pstmt.setString(6, joinMember.getEmail());
            
            insertResult = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("회원가입 중 오류 발생: " + e.getMessage());
        }

        return insertResult;
    }

    // 로그인 select 메소드
    public String selectMemberLogin(String inputId, String inputPw) {        
        String sql = "SELECT id FROM members WHERE id = ? AND pw = ?";
        String loginId = null;

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, inputId);
            pstmt.setString(2, inputPw);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loginId = rs.getString("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("로그인 중 오류 발생: " + e.getMessage());
        }

        return loginId;
    }
    
    // 아이디 중복확인 메소드
    public MemberDto selectMemberIdInfo(String inputId2) {		
		String sql = "SELECT * FROM MEMBERS "+ "WHERE ID = ?";
		MemberDto mInfo = null;
		
		try (Connection con = getConnection();
	             PreparedStatement pstmt = con.prepareStatement(sql)) {
			
			pstmt.setString(1, inputId2);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				mInfo = new MemberDto();
				mInfo.setId(rs.getString("id"));
				mInfo.setPw(rs.getString("pw"));
				mInfo.setName(rs.getString("name"));
				mInfo.setGender(rs.getString("gender"));
				mInfo.setBirth(rs.getString("birth"));
				mInfo.setEmail(rs.getString("email"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("아이디 중복확인 중 오류 발생: " + e.getMessage());
		}
		return mInfo;
	}

    // 회원정보 select 메소드
    public MemberDto selectMemberInfo(String sessionLoginId) {        
        String sql = "SELECT * FROM members WHERE id = ?";
        MemberDto mInfo = null;

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, sessionLoginId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    mInfo = new MemberDto();
                    mInfo.setId(rs.getString("id"));
                    mInfo.setPw(rs.getString("pw"));
                    mInfo.setName(rs.getString("name"));
                    mInfo.setGender(rs.getString("gender"));
                    mInfo.setBirth(rs.getString("birth"));
                    mInfo.setEmail(rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("회원정보 조회 중 오류 발생: " + e.getMessage());
        }

        return mInfo;
    }
    
    // 회원목록 select 메소드
    public ArrayList<MemberDto> selectMemberList() {		
		String sql = "SELECT id, pw, name, gender, DATE_FORMAT(birth, '%Y-%m-%d') as birth, email " + "FROM members";
		ArrayList<MemberDto> memberList = new ArrayList<MemberDto>();
		 try (Connection con = getConnection();
	             PreparedStatement pstmt = con.prepareStatement(sql)) {

			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				MemberDto mInfo = new MemberDto();
				mInfo.setId(rs.getString("id"));
                mInfo.setPw(rs.getString("pw"));
                mInfo.setName(rs.getString("name"));
                mInfo.setGender(rs.getString("gender"));
                mInfo.setBirth(rs.getString("birth"));
                mInfo.setEmail(rs.getString("email"));
				memberList.add(mInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("회원목록 조회 중 오류 발생: " + e.getMessage());
		}
		return memberList;
	}
    
    // 회원정보 delete 메소드
    public int memberDelete(String delId) {    	
		String sql = "DELETE FROM members WHERE id = ?";
		int result = 0;

		try (Connection con = getConnection();
	             PreparedStatement pstmt = con.prepareStatement(sql)) {
			
			pstmt.setString(1, delId);
			result = pstmt.executeUpdate();
        } catch(Exception e) {
        	e.printStackTrace();
        	System.out.println("회원정보 삭제 중 오류 발생: " + e.getMessage());
        }
        return result;
    }
    
    // 회원정보 update 메소드
    public int updateMemberInfo(MemberDto updateMember) {
        int result = 0;
        StringBuilder sql = new StringBuilder("UPDATE members SET ");
        List<Object> parameters = new ArrayList<>();
        
        if (updateMember.getPw() != null && !updateMember.getPw().isEmpty()) {
            sql.append("pw = ?, ");
            parameters.add(updateMember.getPw());
        }
        if (updateMember.getGender() != null && !updateMember.getGender().isEmpty()) {
            sql.append("gender = ?, ");
            parameters.add(updateMember.getGender());
        }
        if (updateMember.getName() != null && !updateMember.getName().isEmpty()) {
        	sql.append("name = ?, ");
        	parameters.add(updateMember.getName());
        }
        if (updateMember.getBirth() != null && !updateMember.getBirth().isEmpty()) {
        	sql.append("birth = ?, ");
        	parameters.add(updateMember.getBirth());
        }

        // 마지막에 붙은 불필요한 ", "를 제거하고 WHERE 절 추가
        if (parameters.isEmpty()) {
            return result;
        }

        sql.setLength(sql.length() - 2);  // ", " 제거
        sql.append(" WHERE id = ?");
        parameters.add(updateMember.getId());

        try (Connection con = getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("회원정보 수정 중 오류 발생: " + e.getMessage());
        }

        return result;
    }

}
