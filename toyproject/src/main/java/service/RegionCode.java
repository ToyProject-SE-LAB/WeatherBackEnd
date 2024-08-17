package service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RegionCode {

    private Map<String, String> midWeatherCodeMap; // 중기육상코드 저장
    private Map<String, String> midTempCodeMap; // 중기기온코드 저장

    public RegionCode(String midLWeatherCsvFilePath, String midTempCsvFilePath) {
    	midWeatherCodeMap = new HashMap<>();
        midTempCodeMap = new HashMap<>();
        loadCodes(midLWeatherCsvFilePath, midTempCsvFilePath);
    }

    private void loadCodes(String midWeatherFilePath, String midTempFilePath) {
        loadCsv(midWeatherFilePath, midWeatherCodeMap);
        loadCsv(midTempFilePath, midTempCodeMap);
    }

    private void loadCsv(String filePath, Map<String, String> codeMap) {
        try (InputStream is = getClass().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String region = parts[0].trim();
                    String code = parts[1].trim();
                    codeMap.put(region, code);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 중기육상코드 반환
    public String getMidWeatherCode(String region) {
        return midWeatherCodeMap.get(region);
    }

    // 중기기온코드 반환
    public String getMidTempCode(String region) {
        return midTempCodeMap.get(region);
    }
}
