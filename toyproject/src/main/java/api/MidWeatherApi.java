package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONObject;

import vo.MidWeatherInfo;

public class MidWeatherApi {
    public List<MidWeatherInfo> midWeatherData(String regId) throws IOException {
    	List<MidWeatherInfo> weatherList = new ArrayList<>();
    	try {
	    	// 현재 날짜(yyyymmddHHmm) 얻기
			String base_date = getBaseDate(LocalDate.now(), LocalTime.now());
			System.out.println(base_date);
			
	        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst"); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=/kA0qzwVocsZ3Gt64ehI0l0NE87BtrVI21Lg1VvUsm/7C9Eq5JxrgejyiJNS6zinJX67naoxH57/0sXY4dx10A=="); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
	        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*요청자료형식 json*/
	        urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8")); /*11B0000 서울, 인천, 경기도 11D10000 등 (활용가이드 하단 참고자료 참조)*/
	        urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(base_date, "UTF-8")); /*-일 2회(06:00,18:00)회 생성 되며 발표시각을 입력 YYYYMMDD0600(1800)-최근 24시간 자료만 제공*/
	        
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
	        
	        System.out.println(sb.toString());
	        
	        // 데이터 파싱
	     	JSONObject jsonObject = new JSONObject(sb.toString());
	     	JSONObject response = jsonObject.getJSONObject("response");
	     	JSONObject body = response.getJSONObject("body");
	     	JSONObject items = body.getJSONObject("items");
	     	JSONArray itemArray = items.getJSONArray("item");
	     	
	     	HashMap<String, String> sky = new HashMap<>(); // 하늘상태
	     	HashMap<String, String> pop = new HashMap<>(); // 강수확률
	     	
	     	// 파싱 및 HashMap에 저장
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.getJSONObject(i);
                Iterator<String> keys = item.keys();
                
                // 모든 키 순회
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = item.get(key);
                    String day;
                    String str = "";

                    // 하늘상태 값 저장
                    if (key.startsWith("wf")) {
                        try {
                            day = key.substring(2, key.lastIndexOf("m") - 1); // 날짜
                            str = key.substring(key.lastIndexOf("m") - 1); // 오전 or 오후 저장
                        } catch (Exception e) {
                            continue;
                        }
                    }

                    // 강수확률 값 저장
                    if (key.startsWith("rnSt")) {
                        try {
                            day = key.substring(4, key.lastIndexOf("m") - 1);
                            str = key.substring(key.lastIndexOf("m") - 1);
                        } catch (Exception e) {
                        	continue;
                        }
                        
                        // 값을 String으로 변환
                        String weatherValue = value instanceof Integer ? String.valueOf((Integer) value) : value.toString();
                        sky.put(day + str, weatherValue);
                        pop.put(day + str, weatherValue);
                    }
                }
            }
            
            for (String key : sky.keySet()) {
                String skyWeather = sky.get(key);
                String date = key;
                String popValue = pop.getOrDefault(date, "0"); // 기본값 "0"

                MidWeatherInfo weatherInfo = new MidWeatherInfo(date, skyWeather, popValue);
                weatherList.add(weatherInfo);
            }
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return weatherList;
    }

	private String getBaseDate(LocalDate currentDate, LocalTime currentTime) {
		// 날짜 포맷터 및 시간 포맷터
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
		
		// 기준 시간 설정
		LocalTime midnight = LocalTime.MIDNIGHT; // 00:00
        LocalTime sixAM = LocalTime.of(6, 0);    // 06:00
        LocalTime sixPM = LocalTime.of(18, 0);    // 18:00

        // 초기값 설정
        LocalDate baseDate = currentDate;
        LocalTime baseTime = LocalTime.MIDNIGHT; 
        
        if(currentTime.isAfter(midnight) && currentTime.isBefore(sixAM)) {
        	baseDate = currentDate.minusDays(1);
        	baseTime = sixPM;
        } else if(currentTime.isAfter(sixAM) && currentTime.isBefore(sixPM)) {
        	baseTime = sixAM;
        } else if (currentTime.isAfter(sixPM) && currentTime.isBefore(LocalTime.of(23, 59, 59))){
        	baseTime = sixPM;
        }
		
        // 최종 날짜와 시간을 문자열로 변경
        String formattedDate = baseDate.format(dateFormatter);
        String formattedTime = baseTime.format(timeFormatter);
        
		return formattedDate + formattedTime;
	}
}