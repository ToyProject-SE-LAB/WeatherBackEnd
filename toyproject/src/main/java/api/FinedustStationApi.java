package api;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

public class FinedustStationApi {
    public String fetchData(String x, String y) throws IOException {
    	String stationName = null;
    	
    	try {
	        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList"); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=/kA0qzwVocsZ3Gt64ehI0l0NE87BtrVI21Lg1VvUsm/7C9Eq5JxrgejyiJNS6zinJX67naoxH57/0sXY4dx10A=="); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*json*/
	        urlBuilder.append("&" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(x, "UTF-8")); /*TM측정방식 X좌표*/
	        urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(y, "UTF-8")); /*TM측정방식 Y좌표*/
	        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.1", "UTF-8")); /*버전별 상세 결과 참고*/
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
            JSONArray items = body.getJSONArray("items");
     	    
            for(int i=0; i<items.length(); i++) {
            	JSONObject item = items.getJSONObject(i);
            	stationName = item.getString("stationName");
            }
     	        
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return stationName;
    }
}