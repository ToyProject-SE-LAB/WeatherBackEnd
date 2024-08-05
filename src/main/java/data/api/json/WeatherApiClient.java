package data.api.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherApiClient {
    public static void main(String[] args) throws IOException {
    	try {
    		// 현재 날짜(yyyymmdd) 얻기
    		LocalDate currentDate = LocalDate.now();
    		String baseDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    		System.out.println(baseDate);
    		
	        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=/kA0qzwVocsZ3Gt64ehI0l0NE87BtrVI21Lg1VvUsm/7C9Eq5JxrgejyiJNS6zinJX67naoxH57/0sXY4dx10A=="); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
	        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*요청자료형식 JSON*/
	        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /*현재날짜*/
	        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); /*0500 시각*/
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
	        
	        // System.out.println(sb.toString());
	        
	     // 데이터 파싱
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONObject response = jsonObject.getJSONObject("response");
			JSONObject body = response.getJSONObject("body");
			JSONObject items = body.getJSONObject("items");
	        JSONArray itemArray = items.getJSONArray("item");
			
	        // 예보 시간별로 데이터를 저장할 맵
	        Map<String, Map<String, String>> forecastData = new TreeMap<>();
	        
			for (int i = 0; i < itemArray.length(); i++) {
				JSONObject item = itemArray.getJSONObject(i);
				String fcstDate = item.getString("fcstDate"); // 측정값
                String fcstTime = item.getString("fcstTime"); // 날짜
			    String fcstValue = item.getString("fcstValue"); // 예보 값
			    String category = item.getString("category"); // 자료구분문자
			    
			    // 예보 시간 키
			    String dateTimeKey = fcstDate + " " + fcstTime;
			    Map<String, String> dataMap = forecastData.getOrDefault(dateTimeKey, new HashMap<>());
			    dataMap.put(category, fcstValue);
			    forecastData.put(dateTimeKey, dataMap);
			}
			    // 저장된 데이터 출력
			for(String dateTimeKey : forecastData.keySet()) {
			    Map<String, String> dataMap = forecastData.get(dateTimeKey);
			    String sky = dataMap.get("SKY"); // 하늘상태
			    String tmp = dataMap.get("TMP"); // 온도
			    String pop = dataMap.get("POP"); // 강수확률
			    String wsd = dataMap.get("WSD"); // 풍속
			    String reh = dataMap.get("REH"); // 습도
			    
			    // 하늘상태 데이터
			    String skyCondition = "";
			    if(sky != null) {
			    	if(sky.equals("1")) {
			    		skyCondition = "맑음";
			    	} else if(sky.equals("2")) {
			    		skyCondition = "구름많음";
			    	} else if(sky.equals("3")) {
			    		skyCondition = "흐림";
			    	}
			    }
			    
			    String resultTmp = (tmp == null) ? "" : ("온도: " + tmp + "°C");
	            String resultPop = (pop == null) ? "" : ("강수확률: " + pop + "%");
	            String resultWsd = (wsd == null) ? "" : ("풍속: " + wsd + "m/s");
	            String resultReh = (reh == null) ? "" : ("습도: " + reh + "%");
			    
	            // 콘솔 출력
			    System.out.println("날짜: " + dateTimeKey);
			    System.out.println("하늘상태: " + skyCondition);
			    System.out.println(resultTmp);
			    System.out.println(resultPop);
			    System.out.println(resultWsd);
			    System.out.println(resultReh);
			}
    	} catch(IOException e) {
    		e.printStackTrace();
    	}

    }
}