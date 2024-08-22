package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import api.FinedustApi;
import api.FinedustStationApi;
import api.MidTempApi;
import api.MidWeatherApi;
import api.ShortWeatherApi;
import service.Coordinate;
import service.RegionCode;

@WebServlet("/Weather")
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
		 try {
		        // 위도와 경도 추출
		        double latitude = Double.parseDouble(request.getParameter("latitude"));
		        double longitude = Double.parseDouble(request.getParameter("longitude"));

		        // 위도와 경도를 x, y 좌표로 변환
		        Coordinate.Coord coord = Coordinate.convertLatLonToXY(latitude, longitude);
		        String x = String.valueOf(coord.getX());
		        String y = String.valueOf(coord.getY());

		        // 각 API 호출
		        String finedustStationName = finedustStationApi.fetchData(x, y);
		        String finedustJsonData = finedustApi.fetchDataAsJson(finedustStationName);
		        String shortWeatherJsonData = shortWeatherApi.fetchDataAsJson(x, y);
		        String midWeatherJsonData = midWeatherApi.fetchDataAsJson(regionCode.getRegionCodes(latitude, longitude)[1]);
		        String midTempJsonData = midTempApi.fetchDataAsJson(regionCode.getRegionCodes(latitude, longitude)[0]);
		        
		        System.out.println(shortWeatherJsonData);
		        
		        // JSON 파싱
		        JSONArray finedustArray = new JSONArray(finedustJsonData);
		        JSONArray shortWeatherArray = new JSONArray(shortWeatherJsonData);
		        JSONArray midWeatherArray = new JSONArray(midWeatherJsonData);
		        JSONArray midTempArray = new JSONArray(midTempJsonData);
		        
		        // 단기예보 배열(현재 ~ 12시간 후)
		        JSONArray shortArray = new JSONArray();
		        
		        // 현재 바람 배열
		        JSONObject wind = new JSONObject();
		        
		        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
	            LocalDateTime now = LocalDateTime.now();
		        
	            // 데이터 시작 인덱스 찾기
	            int startIndex = -1;
	            for (int i = 0; i < shortWeatherArray.length(); i++) {
	                JSONObject item = shortWeatherArray.getJSONObject(i);
	                String date = item.optString("date");

	                LocalDateTime forecastDateTime = LocalDateTime.parse(date, dateTimeFormatter);

	                if (!forecastDateTime.isBefore(now)) {
	                    startIndex = i;
	                    break;
	                }
	            }
	            
	            // 단기예보 저장(기온, 하늘상태)
	            if (startIndex != -1) {
	                for (int i = startIndex-1; i < startIndex + 12; i++) {
	                    JSONObject shortWeatherItem = shortWeatherArray.getJSONObject(i);
	                    JSONObject item = new JSONObject();
	                    item.put("description", shortWeatherItem.optString("description"));
	                    item.put("temp", shortWeatherItem.optString("temp"));
	                    shortArray.put(item);
	                }

	                // 현재 바람 배열 저장(풍속, 풍향)
	                int index = startIndex - 1;
	                if (index >= 0 && index < shortWeatherArray.length()) {
	                    JSONObject shortWeatherItem = shortWeatherArray.getJSONObject(index);
	                    wind.put("windSpeed", shortWeatherItem.optString("wsd", ""));
	                    wind.put("windDirection", shortWeatherItem.optString("vec", ""));
	                }
	            }

	            // 일주일 날씨 배열 생성
	            JSONObject[] weeklyArray = new JSONObject[7];
	            
	            // 오늘과 내일의 날짜를 LocalDate로 정의
	            LocalDate todayDate = LocalDate.now();
	            LocalDate tomorrowDate = todayDate.plusDays(1);

	            // 날짜 포맷 정의
	            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            String today = todayDate.format(dateFormatter);
	            String tomorrow = tomorrowDate.format(dateFormatter);
	            
	            // 일일 최저 및 최고 온도
	            String todayLow = "";
	            String todayHigh = "";
	            String tomorrowLow = "";
	            String tomorrowHigh = "";
	            String todayWeather = "";
	            String tomorrowWeather = "";

	            // 오늘과 내일의 날씨 정보를 추출하여 weeklyArray에 저장
	            for (int i = 0; i < shortWeatherArray.length(); i++) {
	                JSONObject item = shortWeatherArray.getJSONObject(i);
	                String dateTime = item.optString("date", "");
	                String description = item.optString("description", "");
	                String low= item.optString("low", "");
	                String high = item.optString("high", "");
	                
	              
	                LocalDateTime itemDateTime;
	                try {
	                    // 아이템의 날짜와 시간을 LocalDateTime으로 변환
	                    itemDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
	                } catch (Exception e) {
	                    System.err.println("Failed to parse date: " + dateTime);
	                    continue; // 날짜 파싱 실패 시, 다음 아이템으로 넘어감
	                }

	                // 아이템 날짜를 LocalDate로 변환
	                LocalDate itemDate = itemDateTime.toLocalDate();
	                
	                if (itemDate.equals(todayDate)) {
	                    if (!low.isEmpty()) {
	                        todayLow = low.replace(".0", ""); // .0 제거
	                        System.out.println("Set todayLow: " + todayLow);
	                    }
	                    if (!high.isEmpty()) {
	                        todayHigh = high.replace(".0", ""); // .0 제거
	                        todayWeather = description;
	                        System.out.println("Set todayHigh: " + todayHigh);
	                    }
	                } else if (itemDate.equals(tomorrowDate)) {
	                    if (!low.isEmpty()) {
	                        tomorrowLow = low.replace(".0", ""); // .0 제거
	                        System.out.println("Set tomorrowLow: " + tomorrowLow);
	                    }
	                    if (!high.isEmpty()) {
	                        tomorrowHigh = high.replace(".0", ""); // .0 제거
	                        tomorrowWeather = description;
	                        System.out.println("Set tomorrowHigh: " + tomorrowHigh);
	                    }
	                }
	            }
	                
				// 오늘과 내일의 정보 설정
		        if (!todayLow.isEmpty() && !todayHigh.isEmpty()) {
		            JSONObject todayObject = new JSONObject();
		            todayObject.put("weather", todayWeather);
		            todayObject.put("low", todayLow);
		            todayObject.put("high", todayHigh);
		            weeklyArray[0] = todayObject;
		        }
		         
		        if (!tomorrowLow.isEmpty() && !tomorrowHigh.isEmpty()) {
		            JSONObject tomorrowObject = new JSONObject();
		            tomorrowObject.put("weather", tomorrowWeather); 
		            tomorrowObject.put("low", tomorrowLow);
		            tomorrowObject.put("high", tomorrowHigh);
		            weeklyArray[1] = tomorrowObject;
		        }
			    
		        // 3~7일의 날씨 정보 저장
			    for(int i = 0; i < 5; i++) {
			    	JSONObject item = new JSONObject();
			        if (i < midWeatherArray.length() && i < midTempArray.length()) {
			        	JSONObject midWeatherItem = midWeatherArray.getJSONObject(i);
			            JSONObject midTempItem = midTempArray.getJSONObject(i);
		
			            item.put("weather", midWeatherItem.optString("weather", ""));
			            item.put("low", midTempItem.optString("low", ""));
			            item.put("high", midTempItem.optString("high", ""));
			            weeklyArray[i + 2] = item;
			        }
			    }
			    
			    // 좌표 정보 저장
			    JSONObject location = new JSONObject();
			    location.put("경도", longitude);
			    location.put("위도", latitude);
			    
			    // 미세먼지 정보 저장
			    JSONObject finedust = new JSONObject();
			    JSONObject finedustItem = finedustArray.getJSONObject(0);
			    finedust.put("pm2.5", finedustItem.optString("pm25Grade", ""));
			    finedust.put("pm10", finedustItem.optString("pm10Grade", ""));

	            // JSON 객체 생성
	            JSONObject responseJson = new JSONObject();
		        
	            responseJson.put("region", regionCode.getRegionCodes(latitude, longitude)[2]); // 지역명
	            responseJson.put("shortWeather", shortArray); // 단기 예보 배열 (temp와 description만 포함)
	            responseJson.put("weeklyArray", weeklyArray); // 일주일 예보 배열
		        responseJson.put("coordinate", location);
		        responseJson.put("finedust", finedust); // 미세먼지 배열
		        responseJson.put("wind", wind); // 바람 배열
		        
		        System.out.println(responseJson.toString(2));

		        // 응답 설정
		        response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        response.getWriter().write(responseJson.toString());

		    } catch(Exception e) {
		        e.printStackTrace();
		    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
