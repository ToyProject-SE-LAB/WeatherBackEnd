package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.MidTempApi;
import main.java.vo.MidTempInfo;

@WebServlet("/midtemp")
public class MidTempServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String temp_apiUrl = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MidTempApi tempApi = new MidTempApi();
		
		List<MidTempInfo> tempDataList;
		try {
			tempDataList = tempApi.midTempData(temp_apiUrl);
			
			request.setAttribute("tempDataList", tempDataList);
		
			// jsp로 포워딩
			request.getRequestDispatcher("/MidTempTest.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}