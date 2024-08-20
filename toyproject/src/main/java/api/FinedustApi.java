package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import vo.FinedustInfo;


public class FinedustApi {
	public FinedustInfo[] fetchData(String stationName) throws IOException {
		// 리스트 생성
    	FinedustInfo[] finedustArray = null;
		
		try {
	        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=/kA0qzwVocsZ3Gt64ehI0l0NE87BtrVI21Lg1VvUsm/7C9Eq5JxrgejyiJNS6zinJX67naoxH57/0sXY4dx10A=="); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*json*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
	        urlBuilder.append("&" + URLEncoder.encode("stationName","UTF-8") + "=" + URLEncoder.encode(stationName, "UTF-8")); /*측정소 이름*/
	        urlBuilder.append("&" + URLEncoder.encode("dataTerm","UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간(1일: DAILY, 1개월: MONTH, 3개월: 3MONTH)*/
	        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.0", "UTF-8")); /*버전별 상세 결과 참고*/
	        
	        // URL 객체 생성
	        URL url = new URL(urlBuilder.toString());
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-type", "application/json");
	        
	        // 응답 코드 확인
	        System.out.println("Response code: " + conn.getResponseCode());
	        
	        // 응답 데이터 읽기
	        BufferedReader rd;
	        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
	            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        } else {
	            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	        }
	        
	        // 응답 결과를 StringBuilder에 저장
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

	        finedustArray = new FinedustInfo[items.length()];
	        
	        for (int i = 0; i < items.length(); i++) {
	            JSONObject item = items.getJSONObject(i);
	            String date = item.getString("dataTime"); // 측정일
	            String pm10Grade = item.getString("pm10Grade"); // 미세먼지 등급
	            String pm25Grade = item.getString("pm25Grade"); // 초미세먼지 등급

	            FinedustInfo finedustInfo = new FinedustInfo(date, pm10Grade, pm25Grade);
                finedustArray[i] = finedustInfo;
	        }
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
		return finedustArray;
    }
	
	// json으로 변환
	public String fetchDataAsJson(String stationName) throws IOException {
		FinedustInfo[] finedustArray = fetchData(stationName);
	        
	    if (finedustArray == null) {
	    	return new JSONObject().toString(); // 빈 JSON 객체 반환
	    }

	    JSONArray jsonArray = new JSONArray();
	    	for (FinedustInfo info : finedustArray) {
	    		JSONObject jsonObject = new JSONObject();
	            jsonObject.put("date", info.getDate());
	            jsonObject.put("pm10Grade", info.getPm10Grade());
	            jsonObject.put("pm25Grade", info.getPm25Grade());
	            jsonArray.put(jsonObject);
	        }

	        return jsonArray.toString();
	    }
}