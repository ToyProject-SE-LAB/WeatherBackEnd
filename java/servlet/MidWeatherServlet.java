package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.MidWeatherApi;
import main.java.vo.MidWeatherInfo;

@WebServlet("/midweather")
public class MidWeatherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String weather_apiUrl = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MidWeatherApi weatherApi = new MidWeatherApi();
		
		List<MidWeatherInfo> weatherDataList;
		try {
			weatherDataList = weatherApi.midWeatherData(weather_apiUrl);
			
			request.setAttribute("weatherDataList", weatherDataList);
		
			// jsp로 포워딩
			request.getRequestDispatcher("/MidWeatherTest.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}