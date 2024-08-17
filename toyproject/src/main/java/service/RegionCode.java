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
    private Map<String, String> midTempCode = new HashMap<>();
    private Map<String, String> midWeatherCode = new HashMap<>();
    private String apiKey;

    public RegionCode(String apiKey, String midTempCsv, String midWeatherCsv) {
        this.apiKey = apiKey;
        loadRegionCodes(midTempCsv, midTempCode);
        loadRegionCodes(midWeatherCsv, midWeatherCode);
    }

    private void loadRegionCodes(String csvFilePath, Map<String, String> regionCodeMap) {
        System.out.println("Attempting to load resource: " + csvFilePath);
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
            System.out.println("Loaded region codes from " + csvFilePath + ": " + regionCodeMap);
        } catch (IOException e) {
            System.err.println("Error loading resource: " + csvFilePath);
            e.printStackTrace();
        }
    }

    public String[] getRegionCodes(double latitude, double longitude) {
        String regionName = getRegionNameByCoordinates(latitude, longitude);
        System.out.println("Region name for coordinates (" + latitude + ", " + longitude + "): " + regionName);

        // Log the map contents and the result to verify
        System.out.println("MidTempCode Map: " + midTempCode);
        System.out.println("MidWeatherCode Map: " + midWeatherCode);
        
        String tempCode = getCodeForRegion(regionName, midTempCode, true);
        String weatherCode = getCodeForRegion(regionName, midWeatherCode, false);

        System.out.println("Temperature Code: " + tempCode);
        System.out.println("Weather Code: " + weatherCode);
        return new String[]{tempCode, weatherCode};
    }

    private String getCodeForRegion(String regionName, Map<String, String> regionCodeMap, boolean isTempCode) {
        if (isTempCode) {
            // For temperature code, we want to match names ending with '시'
            String simplifiedRegionName = simplifyRegionNameForTemp(regionName);
            return regionCodeMap.getOrDefault(simplifiedRegionName, "코드 없음");
        } else {
            // For weather code, use the region name directly
            String simplifiedRegionName = simplifyRegionNameForWeather(regionName);
            return regionCodeMap.getOrDefault(simplifiedRegionName, "코드 없음");
        }
    }

    private String simplifyRegionNameForTemp(String fullRegionName) {
        if (fullRegionName.endsWith("시")) {
            return fullRegionName;
        }
        return fullRegionName.replaceAll("(시|구|군)$", "");
    }

    private String simplifyRegionNameForWeather(String fullRegionName) {
        // Remove unnecessary parts, if any
        if (fullRegionName.endsWith("도") || fullRegionName.endsWith("도 ")) {
            return fullRegionName.trim();
        }
        // Handle specific cases for '도' endings
        String[] possibleSuffixes = {"도", "시", "구", "군"};
        for (String suffix : possibleSuffixes) {
            if (fullRegionName.endsWith(suffix)) {
                return fullRegionName.replace(suffix, "").trim();
            }
        }
        return fullRegionName.trim();
    }

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

            // Parse the JSON response to extract the region name
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject result = results.getJSONObject(0);
                JSONArray addressComponents = result.getJSONArray("address_components");
                String administrativeAreaLevel1 = null;
                String locality = null;
                for (int i = 0; i < addressComponents.length(); i++) {
                    JSONObject component = addressComponents.getJSONObject(i);
                    JSONArray types = component.getJSONArray("types");
                    String longName = component.getString("long_name");
                    
                    if (types.toString().contains("administrative_area_level_1")) {
                        administrativeAreaLevel1 = longName;
                    } else if (types.toString().contains("locality")) {
                        locality = longName;
                    }
                }

                // If locality is found, return it; otherwise, fall back to administrative area level 1
                if (locality != null) {
                    return extractSimpleRegionName(locality); // Return locality which is usually the city or town name
                } else if (administrativeAreaLevel1 != null) {
                    return extractSimpleRegionName(administrativeAreaLevel1); // Return administrative area if locality is not available
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
