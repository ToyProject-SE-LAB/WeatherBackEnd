package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.FinedustApi;
import api.FinedustStationApi;
import api.MidTempApi;
import api.MidWeatherApi;
import api.ShortWeatherApi;
import service.Coordinate;
import service.RegionCode;

@WebServlet({ "/finedust", "/midtemp", "/midweather", "/shortweather" })
public class WeatherController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private RegionCode regionCode;
	private FinedustApi finedustApi;
	private FinedustStationApi finedustStationApi;
    private ShortWeatherApi shortWeatherApi;
    private MidWeatherApi midWeatherApi;
    private MidTempApi midTempApi;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            String apiKey = "AIzaSyAEOsOkR5QXRzz4Kjk2QDcJg3jpqdINwEE";

            // 클래스패스에서 리소스 파일 경로 지정
            String midTempCsv = "midTempCode.csv";
            String midWeatherCsv = "midWeatherCode.csv";

            // RegionCode 객체 초기화
            regionCode = new RegionCode(apiKey, midTempCsv, midWeatherCsv);

            // API 객체 초기화
            finedustApi = new FinedustApi();
            finedustStationApi = new FinedustStationApi();
            shortWeatherApi = new ShortWeatherApi();
            midWeatherApi = new MidWeatherApi();
            midTempApi = new MidTempApi();
        } catch (Exception e) {
            throw new ServletException("Error initializing WeatherController", e);
        }
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

	private void handleFinedust(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			double latitude = Double.parseDouble(request.getParameter("latitude")); // 위도
	        double longitude = Double.parseDouble(request.getParameter("longitude")); // 경도
	        
	        // 위도와 경도를 x, y 좌표로 변환
	        Coordinate.Coord coord = Coordinate.convertLatLonToXY(latitude, longitude);
	        String x = String.valueOf(coord.getX());
	        String y = String.valueOf(coord.getY());

            
            String stationName = finedustStationApi.fetchData(x, y); // 측정소명
            
            String jsonData = finedustApi.fetchDataAsJson(stationName);
            System.out.println(jsonData);

            // JSON 응답 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonData);   
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleShortWeather(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			double latitude = Double.parseDouble(request.getParameter("latitude")); // 위도
	        double longitude = Double.parseDouble(request.getParameter("longitude")); // 경도

	        // 위도와 경도를 x, y 좌표로 변환
	        Coordinate.Coord coord = Coordinate.convertLatLonToXY(latitude, longitude);
	        String x = String.valueOf(coord.getX());
	        String y = String.valueOf(coord.getY());
	        
            // 날씨 정보를 가져온다.
        	String jsonData = shortWeatherApi.fetchDataAsJson(x, y);
        	System.out.println(jsonData);
        	
        	// JSON 응답 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonData);  

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void handleMidWeather(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			double latitude = Double.parseDouble(request.getParameter("latitude"));
            double longitude = Double.parseDouble(request.getParameter("longitude"));
			
            // 위도와 경도를 사용하여 지역 코드 얻기
            String[] regionCodes = regionCode.getRegionCodes(latitude, longitude); // 중기예보코드값
            String midWeatherCode = regionCodes[1];
            System.out.println(midWeatherCode);
            
            String jsonData = midWeatherApi.fetchDataAsJson(midWeatherCode);
            System.out.println(jsonData);
            // JSON 응답 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonData);  
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void handleMidTemp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			double latitude = Double.parseDouble(request.getParameter("latitude"));
            double longitude = Double.parseDouble(request.getParameter("longitude"));
			
            // 위도와 경도를 사용하여 지역 코드 얻기
            String[] regionCodes = regionCode.getRegionCodes(latitude, longitude); // 중기예보코드값
            String midTempCode = regionCodes[0];
            System.out.println(midTempCode);
            
            String jsonData = midTempApi.fetchDataAsJson(midTempCode);
            System.out.println(jsonData);
            // JSON 응답 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonData);  
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
