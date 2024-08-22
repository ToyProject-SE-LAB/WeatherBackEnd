package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import vo.ShortWeatherInfo;

public class ShortWeatherApi {
	
	// 기상 데이터 종류
	enum WeatherValue {
		SKY, PTY, TMP, WSD, VEC, TMN, TMX
	}
	
	public ShortWeatherInfo[] fetchData(String x, String y) throws Exception {
		Map<String, ShortWeatherInfo> weatherMap = new TreeMap<>();
		
    	try {    		
    		// 현재 날짜(yyyymmdd) 얻기
    		LocalDate currentDate = LocalDate.now();
            String base_date = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    		
    		// 현재 시간을 기준으로 base_time 얻기
            String base_time = getClosestBaseTime(LocalTime.now());
            
            
	        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=/kA0qzwVocsZ3Gt64ehI0l0NE87BtrVI21Lg1VvUsm/7C9Eq5JxrgejyiJNS6zinJX67naoxH57/0sXY4dx10A=="); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
	        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*요청자료형식 JSON*/
	        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(base_date, "UTF-8")); /*현재날짜*/
	        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(base_time, "UTF-8")); /*0500 시각*/
	        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(x, "UTF-8")); /*예보지점의 X 좌표값*/
	        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(y, "UTF-8")); /*예보지점의 Y 좌표값*/
	        
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
	        
	        // 하늘상태 코드
	        Map<String, String> skyCodeMap = new HashMap<>();
	        skyCodeMap.put("1", "맑음");
	        skyCodeMap.put("3", "구름많음");
	        skyCodeMap.put("4", "흐림");

	        // 강수형태 코드
	        Map<String, String> ptyCodeMap = new HashMap<>();
	        ptyCodeMap.put("0", "없음");
	        ptyCodeMap.put("1", "비");
	        ptyCodeMap.put("2", "비/눈");
	        ptyCodeMap.put("3", "눈");
	        ptyCodeMap.put("4", "소나기");
	        
	        // 데이터 파싱
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONObject response = jsonObject.getJSONObject("response");
			JSONObject body = response.getJSONObject("body");
			JSONObject items = body.getJSONObject("items");
	        JSONArray itemArray = items.getJSONArray("item");
	        
	        // 현재 시간과 48시간 후 시간 계산
	        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
	        LocalDateTime baseDateTime = LocalDateTime.parse(base_date+base_time, FORMATTER);
	        LocalDateTime dayLater = baseDateTime.plusHours(48);
	        
			for (int i = 0; i < itemArray.length(); i++) {
				JSONObject item = itemArray.getJSONObject(i);
				String fcstDate = item.getString("fcstDate"); // 날짜
                String fcstTime = item.getString("fcstTime"); // 시간
                String fcstValue = item.getString("fcstValue"); // 예보 값
			    String category = item.getString("category"); // 자료구분문자
			    String dateTimeKey = fcstDate + fcstTime;

			    
			    // 날짜 및 시간을 LocalDateTime으로 변환
	            LocalDateTime forecastDateTime = LocalDateTime.parse(dateTimeKey, FORMATTER);

	            // 측정 시간과 48시간 후 시간 사이의 데이터만 저장
	            if (forecastDateTime.isAfter(baseDateTime) && forecastDateTime.isBefore(dayLater)) {
	                WeatherValue weatherValue;
	               try {
	                    weatherValue = WeatherValue.valueOf(category);
	                } catch (IllegalArgumentException e) {
	                    // 정의되지 않은 category 값일 경우
	                    continue;
	                }

                ShortWeatherInfo weather = weatherMap.getOrDefault(dateTimeKey, new ShortWeatherInfo());
                String description = weather.getDescription();
                weather.setDate(dateTimeKey);
                
                // 데이터를 해당 필드에 저장
			    switch(weatherValue) {
			    	case SKY: // 하늘 상태
			    		description = skyCodeMap.getOrDefault(fcstValue, "알 수 없음");
			    		break;
			    	case PTY: // 강수 형태
			    		String ptyValue = ptyCodeMap.getOrDefault(fcstValue, "알 수 없음");
			    		description = description.isEmpty() ? ptyValue : description + ", " + ptyValue;
			    		break;
			    	case TMP: // 온도
			    		weather.setTemp(fcstValue);
			    		break;
			    	case VEC: // 풍향
			    		weather.setVec(fcstValue);
			    		break;
			    	case WSD: // 풍속
			    		weather.setWsd(fcstValue);
			    		break;
			    	case TMN: // 일 최저기온
			    		weather.setLow(fcstValue);
			    		break;
			    	case TMX: // 일 최고기온
			    		weather.setHigh(fcstValue);
			    		break;
			    }
			    
			    // sky와 pty값을 결합
			    if(weatherValue == WeatherValue.SKY || weatherValue == WeatherValue.PTY) {
			    	weather.setDescription(description);
			    }
			    
                // 리스트에 데이터 추가
			    weatherMap.put(dateTimeKey, weather);
	            }
			}

    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	ShortWeatherInfo[] weatherArray = weatherMap.values().toArray(new ShortWeatherInfo[0]);
    	return weatherArray;
    }
	
	// json으로 변환
	public String fetchDataAsJson(String x, String y) throws Exception {
		ShortWeatherInfo[] weatherArray = fetchData(x, y);
		        
		if (weatherArray == null) {
			return new JSONObject().toString(); // 빈 JSON 객체 반환
		}

		JSONArray jsonArray = new JSONArray();
		for (ShortWeatherInfo info : weatherArray) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("date", info.getDate());
		    jsonObject.put("description", info.getDescription());
		    jsonObject.put("temp", info.getTemp());
		    jsonObject.put("wsd", info.getWsd());
		    jsonObject.put("vec", info.getVec());
		    jsonObject.put("low", info.getLow());
		    jsonObject.put("high", info.getHigh());
		    jsonArray.put(jsonObject);
		}

		return jsonArray.toString();
	}

	// 현재 시각을 기준으로 base_time 구하기
	private String getClosestBaseTime(LocalTime currentTime) {
	    // Base times 배열 정의
	    final String[] BASE_TIMES = {"0200", "2300"};

	    // 현재 시각에서 1시간 전 시각
	    LocalTime adjustedTime = currentTime.minusHours(1);
	    
	    // 가장 가까운 base_time을 초기화
	    String closestBaseTime = BASE_TIMES[0];
	    LocalTime closestTime = LocalTime.parse(closestBaseTime, DateTimeFormatter.ofPattern("HHmm"));

	    for (String baseTime : BASE_TIMES) {
	        LocalTime baseLocalTime = LocalTime.parse(baseTime, DateTimeFormatter.ofPattern("HHmm"));

	        // adjustedTime가 baseLocalTime보다 이전일 때
	        if (adjustedTime.isBefore(baseLocalTime)) {
	            // 가장 가까운 이전 base_time을 찾기 위한 조건
	            if (adjustedTime.isAfter(closestTime)) {
	                closestBaseTime = closestTime.format(DateTimeFormatter.ofPattern("HHmm"));
	            }
	            break; // 현재 시간 이후의 base_time은 필요 없음
	        }

	        // 가장 가까운 base_time을 계속 업데이트
	        closestBaseTime = baseTime;
	        closestTime = baseLocalTime;
	    }

	    //System.out.println("Closest Base Time: " + closestBaseTime);

	    return closestBaseTime;
	}
}