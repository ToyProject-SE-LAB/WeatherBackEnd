package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import main.java.vo.ShortWeatherVO;

public class ShortWeatherApi {
	
	// 기상 데이터 종류
	enum WeatherValue {
		SKY, PTY, TMP, REH, WSD
	}

	public Map<String, ShortWeatherVO> fatchData(String apiUrl) throws Exception {
		// 시간순으로 데이터 저장
        Map<String, ShortWeatherVO> forecastData = new TreeMap<>();
		
    	try {    		
    		// 현재 날짜(yyyymmdd) 얻기
    		LocalDate currentDate = LocalDate.now();
    		String baseDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    		
    		
	        StringBuilder urlBuilder = new StringBuilder(apiUrl); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=/kA0qzwVocsZ3Gt64ehI0l0NE87BtrVI21Lg1VvUsm/7C9Eq5JxrgejyiJNS6zinJX67naoxH57/0sXY4dx10A=="); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
	        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*요청자료형식 JSON*/
	        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /*현재날짜*/
	        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0200", "UTF-8")); /*0500 시각*/
	        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("55", "UTF-8")); /*예보지점의 X 좌표값*/
	        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /*예보지점의 Y 좌표값*/
	        
	        URL url = new URL(urlBuilder.toString());
	        
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-type", "application/json");
	        System.out.println("Response code: " + conn.getResponseCode());
	        
	        BufferedReader rd;
	        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
	            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        } else {
	            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	        }
	        
	        StringBuilder sb = new StringBuilder();
	        String line;
	        while ((line = rd.readLine()) != null) {
	            sb.append(line);
	        }
	        
	        rd.close();
	        conn.disconnect();
	        
	        //System.out.println(sb.toString());
	        
	     // 데이터 파싱
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONObject response = jsonObject.getJSONObject("response");
			JSONObject body = response.getJSONObject("body");
			JSONObject items = body.getJSONObject("items");
	        JSONArray itemArray = items.getJSONArray("item");
				        
			for (int i = 0; i < itemArray.length(); i++) {
				JSONObject item = itemArray.getJSONObject(i);
				String fcstDate = item.getString("fcstDate"); // 날짜
                String fcstTime = item.getString("fcstTime"); // 시간
                String fcstValue = item.getString("fcstValue"); // 예보 값
			    String category = item.getString("category"); // 자료구분문자
			    String dateTimeKey = fcstDate + " " + fcstTime;
			    
			    WeatherValue weatherValue;
                try {
                	weatherValue = WeatherValue.valueOf(category);
                } catch (IllegalArgumentException e) {
                	// 정의되지 않은 category 값일 경우
                    continue; 
                }
                
                ShortWeatherVO weather = forecastData.getOrDefault(dateTimeKey, new ShortWeatherVO());
                
                // 데이터를 해당 필드에 저장
			    switch(weatherValue) {
			    	case SKY: // 하늘상태
			    		weather.setSKY(fcstValue);
			    		break;
			    	case PTY: // 강수형태
			    		weather.setPTY(fcstValue);
			    		break;
			    	case TMP: // 온도
			    		weather.setTMP(fcstValue);
			    		break;
			    	case REH: //습도
			    		weather.setREH(fcstValue);
			    		break;
			    	case WSD: // 풍속
			    		weather.setWSD(fcstValue);
			    		break;
			    }
			    
			    weather.setDate(fcstDate);
                weather.setTime(fcstTime);

                forecastData.put(dateTimeKey, weather);
			}
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	return forecastData;
    }
}