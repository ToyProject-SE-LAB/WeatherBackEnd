package servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.ShortWeatherApi;
import main.java.vo.ShortWeatherVO;

@WebServlet("/weather")
public class WeatherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String short_apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ShortWeatherApi shortApi = new ShortWeatherApi();
		
		Map<String, ShortWeatherVO> shortDataList;
		try {
			shortDataList = shortApi.fatchData(short_apiUrl);
			
			request.setAttribute("shortDataList", shortDataList);
		
			// jsp로 포워딩
			request.getRequestDispatcher("/WeatherData_form.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}