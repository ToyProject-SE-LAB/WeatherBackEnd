package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.WeatherApi;

@WebServlet("/weather")
public class WeatherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WeatherApi api = new WeatherApi();
		List<String[]> dataList = api.fetchData(apiUrl);
		
		request.setAttribute("dataList", dataList);
		
		// jsp로 포워딩
		request.getRequestDispatcher("/weatherData_form.jsp").forward(request, response);
	}

}
