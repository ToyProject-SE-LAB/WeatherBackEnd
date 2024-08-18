package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegionCode {
    private Map<String, String> midTempCode = new HashMap<>();		// 중기기온지역코드 
    private Map<String, String> midWeatherCode = new HashMap<>();	// 중기육상지역코드
    private String apiKey;

    public RegionCode(String apiKey, String midTempCsv, String midWeatherCsv) {
        this.apiKey = apiKey;
        loadRegionCodes(midTempCsv, midTempCode);
        loadRegionCodes(midWeatherCsv, midWeatherCode);
    }

    // csv파일에서 지역코드 정보 로드
    private void loadRegionCodes(String csvFilePath, Map<String, String> regionCodeMap) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(csvFilePath);
        if (is == null) {
            System.out.println("Resource not found: " + csvFilePath);
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String regionName = parts[0].trim();
                    String regionCode = parts[1].trim();
                    regionCodeMap.put(regionName, regionCode);
                } else {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading resource: " + csvFilePath);
            e.printStackTrace();
        }
    }

    // 지역명에 대응하는 육상, 기온지역코드를 배열로 반환
    public String[] getRegionCodes(double latitude, double longitude) {
        String regionName = getRegionNameByCoordinates(latitude, longitude);
        System.out.println("Region name for coordinates (" + latitude + ", " + longitude + "): " + regionName);

        String tempCode = getCodeForRegion(regionName, midTempCode, true);
        String weatherCode = getCodeForRegion(regionName, midWeatherCode, false);

        System.out.println("기온코드: " + tempCode);
        System.out.println("육상코드: " + weatherCode);
        return new String[]{tempCode, weatherCode};
    }

    // 지역명에 해당하는 지역코드 반환
    private String getCodeForRegion(String regionName, Map<String, String> regionCodeMap, boolean isTempCode) {
        String[] parts = regionName.split(" ");
        if (parts.length == 2) {
            if (isTempCode) {
                // 시 이름 사용
                String city = parts[1];
                return regionCodeMap.getOrDefault(city, "코드 없음");
            } else {
                // 도 이름 사용
                String province = parts[0];
                return regionCodeMap.getOrDefault(province, "코드 없음");
            }
        }
        return "코드 없음";
    }

    // 지역명 찾기
    private String getRegionNameByCoordinates(double latitude, double longitude) {
        String requestUrl = String.format(
            "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&key=%s&language=ko",
            latitude, longitude, apiKey
        );

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 파싱
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject result = results.getJSONObject(0);
                JSONArray addressComponents = result.getJSONArray("address_components");
                String administrativeAreaLevel1 = null;  // 도
                String locality = null;  // 시
                
                for (int i = 0; i < addressComponents.length(); i++) {
                    JSONObject component = addressComponents.getJSONObject(i);
                    JSONArray types = component.getJSONArray("types");
                    String longName = component.getString("long_name");

                    if (types.toString().contains("administrative_area_level_1")) {
                        administrativeAreaLevel1 = longName;  // 도 이름 저장
                    } else if (types.toString().contains("locality")) {
                        locality = extractSimpleRegionName(longName);  // 시 이름 저장
                    }
                }

                if (locality != null && administrativeAreaLevel1 != null) {
                	// XX도 XX시 형태로 반환
                    return administrativeAreaLevel1 + " " + locality; 
                } else if (administrativeAreaLevel1 != null) {
                    // 시 이름이 없으면 도 이름만 반환
                	return administrativeAreaLevel1;  
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
    
    private String extractSimpleRegionName(String fullRegionName) {
        if (fullRegionName.endsWith("시") || fullRegionName.endsWith("구") || fullRegionName.endsWith("군")) {
            return fullRegionName.replaceAll("(시|구|군)$", "");
        }
        return fullRegionName;
    }
}
