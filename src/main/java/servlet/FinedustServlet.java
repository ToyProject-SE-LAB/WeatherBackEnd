package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.FinedustApi;

@WebServlet("/finedust")
public class FinedustServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String apiUrl = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty";


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FinedustApi api = new FinedustApi();
		List<String[]> dataList = api.fetchData(apiUrl);
		
		request.setAttribute("dataList", dataList);
		
		// jsp로 포워딩
		request.getRequestDispatcher("/finedusrData_form.jsp").forward(request, response);
	}

}
