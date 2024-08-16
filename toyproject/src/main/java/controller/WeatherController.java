package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.FinedustApi;
import api.MidTempApi;
import api.MidWeatherApi;
import api.ShortWeatherApi;
import util.ExcelReader;
import vo.LocationInfo;
import vo.MidTempInfo;
import vo.MidWeatherInfo;
import vo.ShortWeatherInfo;

@WebServlet({ "/finedust", "/midtemp", "/midweather", "/shortweather" })
public class WeatherController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private ExcelReader excelReader;
	private FinedustApi finedustApi;
    private ShortWeatherApi shortWeatherApi;
    private MidWeatherApi midWeatherApi;
    private MidTempApi midTempApi;

    @Override
    public void init() throws ServletException {
        excelReader = new ExcelReader();
        finedustApi = new FinedustApi(); 
        shortWeatherApi = new ShortWeatherApi(); 
        midWeatherApi = new MidWeatherApi(); 
        midTempApi = new MidTempApi();  
    }
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getServletPath();
        System.out.println("url : " + url);

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        switch(url) {
        	case "/finedust":
        		handleFinedust(request, response);
        		break;
        		
        	case "/shortweather":
        		handleShortWeather(request, response);
        		break;
        		
        	case "/midweather":
        		handleMidWeather(request, response);
        		break;
        		
        	case "/midtemp":
        		handleMidTemp(request, response);
        		break;
        		
        	default:
                response.sendRedirect("404.jsp");
                break;
        }
	}

	// 위치 정보 찾는 메소드
	private LocationInfo getLocationInfoFromRequest(HttpServletRequest request) throws IOException {
		// 사용자의 현재 위도와 경도 값을 Geolocation API로부터 받는다.
		double latitude = Double.parseDouble(request.getParameter("latitude"));
        double longitude = Double.parseDouble(request.getParameter("longitude"));
        return excelReader.findClosestLocation(latitude, longitude);
    }

	private void handleFinedust(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
            // 위치 정보를 찾는 메소드 호출
            LocationInfo locationInfo = getLocationInfoFromRequest(request);
            if (locationInfo == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found.");
                return;
            }
            
            String stationName = locationInfo.getStationName(); // 측정소명
            

            List<String[]> dataList = finedustApi.fetchData(stationName);
    			
    		request.setAttribute("dataList", dataList);
    		request.setAttribute("locationInfo", locationInfo); // 위치 정보 전달
  			// jsp로 포워딩
    		request.getRequestDispatcher("/FinedustData_form.jsp").forward(request, response);       
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleShortWeather(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
            // 위치 정보를 찾는 메소드 호출
            LocationInfo locationInfo = getLocationInfoFromRequest(request);
            if (locationInfo == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found.");
                return;
            }
            
            String x = locationInfo.getX(); // x 좌표값
            String y = locationInfo.getY(); // y 좌표값
            
            // 날씨 정보를 가져온다.
        	Map<String, ShortWeatherInfo> shortDataList = shortWeatherApi.fetchData(x, y);
        	
            // JSP로 데이터 포워딩
            request.setAttribute("shortDataList", shortDataList); // 날씨 정보 전달
            request.setAttribute("locationInfo", locationInfo); // 위치 정보 전달
            request.getRequestDispatcher("/weatherData_form.jsp").forward(request, response);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void handleMidWeather(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
            // 위치 정보를 찾는 메소드 호출
            LocationInfo locationInfo = getLocationInfoFromRequest(request);
            if (locationInfo == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found.");
                return;
            }
            
            String midCode = locationInfo.getMidWeatherCode(); // 중기예보코드값
            
            List<MidWeatherInfo> weatherDataList = midWeatherApi.midWeatherData(midCode);
			
			request.setAttribute("weatherDataList", weatherDataList);
			request.setAttribute("locationInfo", locationInfo); // 위치 정보 전달
			// jsp로 포워딩
			request.getRequestDispatcher("/MidWeatherTest.jsp").forward(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void handleMidTemp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
            // 위치 정보를 찾는 메소드 호출
            LocationInfo locationInfo = getLocationInfoFromRequest(request);
            if (locationInfo == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found.");
                return;
            }
            
            String midCode = locationInfo.getMidTempCode(); // 중기기온코드값
            
            List<MidTempInfo> tempDataList = midTempApi.midTempData(midCode);
			
			request.setAttribute("tempDataList", tempDataList);
			request.setAttribute("locationInfo", locationInfo); // 위치 정보 전달
			// jsp로 포워딩
			request.getRequestDispatcher("/MidTempTest.jsp").forward(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
