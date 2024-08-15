package controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.ShortWeatherApi;
import main.java.vo.LocationInfo;
import util.ExcelReader;

@WebServlet("/shortweather")
public class ShortWeatherControll extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ExcelReader excelReader;
    private ShortWeatherApi shortApi;

    @Override
    public void init() throws ServletException {
        excelReader = new ExcelReader();
		shortApi = new ShortWeatherApi();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 사용자의 현재 위도와 경도 값을 Geolocation API로부터 받는다.
        double userLat = Double.parseDouble(request.getParameter("latitude"));
        double userLon = Double.parseDouble(request.getParameter("longitude"));

        // 가장 가까운 위치 정보를 얻는다.
        LocationInfo locationInfo = excelReader.findClosestLocation(userLat, userLon);
        if (locationInfo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found.");
            return;
        }

        String x = locationInfo.getX(); // x 좌표값
        String y = locationInfo.getY(); // y 좌표값

        try {
            // 날씨 정보를 가져온다.
        	Map<String, ShortWeatherInfo> shortDataList = shortApi.fetchData(x, y);
        	
            // JSP로 데이터 포워딩
            request.setAttribute("shortDataList", shortDataList); // 날씨 정보 전달
            request.setAttribute("locationInfo", locationInfo); // 위치 정보 전달
            request.getRequestDispatcher("/weatherData_form.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch weather data.");
        }
    }
}