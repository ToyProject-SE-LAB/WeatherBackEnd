package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import vo.MidTempInfo;

public class MidTempApi {
	public MidTempInfo[] midTempData(String regId) throws IOException {
		MidTempInfo[] tempArray = null;
    	try {
	    	// 현재 날짜(yyyymmdd) 얻기
			String base_date = getBaseDate(LocalDate.now(), LocalTime.now());
			System.out.println(base_date);
			
    		StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=/kA0qzwVocsZ3Gt64ehI0l0NE87BtrVI21Lg1VvUsm/7C9Eq5JxrgejyiJNS6zinJX67naoxH57/0sXY4dx10A=="); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*요청자료형식 json*/
            urlBuilder.append("&" + URLEncoder.encode("regId","UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8")); /*11B10101 서울, 11B20201 인천 등 ( 별첨엑셀자료 참고)*/
            urlBuilder.append("&" + URLEncoder.encode("tmFc","UTF-8") + "=" + URLEncoder.encode(base_date, "UTF-8")); /*-일 2회(06:00,18:00)회 생성 되며 발표시각을 입력- YYYYMMDD0600(1800) 최근 24시간 자료만 제공*/
	        
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

	     	HashMap<String, String> taMax = new HashMap<>(); // 최고기온
	     	HashMap<String, String> taMin = new HashMap<>(); // 최저기온
	     	
	     	// 문자열 패턴 정의 ((\\d+): 숫자, $: 숫자 뒤에 추가적인 문자없음)
	     	Pattern taMaxPattern = Pattern.compile("taMax(\\d+)$");
            Pattern taMinPattern = Pattern.compile("taMin(\\d+)$");
            
	     	// HashMap에 저장
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.getJSONObject(i);
                Iterator<String> keys = item.keys();
                
                // 모든 키 순회
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = item.get(key);
                    Matcher matcher; //날짜(day) 추출 변수
                    
                    // 최고기온 값 저장
                    if (key.startsWith("taMax")) {
                        matcher = taMaxPattern.matcher(key);
                        if (matcher.find()) {
                            String day = matcher.group(1); // day 부분 추출
                            taMax.put(day, value.toString());
                        }
                    }
                    
                    // 최저기온 값 저장
                    if (key.startsWith("taMin")) {
                        matcher = taMinPattern.matcher(key);
                        if (matcher.find()) {
                            String day = matcher.group(1); // day 부분 추출
                            taMin.put(day, value.toString());
                        }
                    }
                }
            }
            tempArray = new MidTempInfo[taMax.size()];
            
            // 배열에 데이터 저장
            int i = 0;
            for (String key : taMax.keySet()) {
                String date = key;
                String tempMax = taMax.getOrDefault(key, "N/A");
                String tempMin = taMin.getOrDefault(key, "N/A");

                tempArray[i] = new MidTempInfo(date, tempMax, tempMin);
                i++;
            }

    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return tempArray;
    }
	
	// json으로 변환
	public String fetchDataAsJson(String stationName) throws IOException {
		MidTempInfo[] tempArray = midTempData(stationName);
		        
		if (tempArray == null) {
			return new JSONObject().toString(); // 빈 JSON 객체 반환
		}

		JSONArray jsonArray = new JSONArray();
		for (MidTempInfo info : tempArray) {
			JSONObject jsonObject = new JSONObject();
		    jsonObject.put("high", info.getTaMax());
		    jsonObject.put("low", info.getTaMin());
		    jsonArray.put(jsonObject);
		}

		return jsonArray.toString();
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