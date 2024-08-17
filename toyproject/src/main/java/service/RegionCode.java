package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class RegionCode {
    private Map<String, String> midTempCode = new HashMap<>();
    private Map<String, String> midWeatherCode = new HashMap<>();
    private GeoApiContext geoApiContext;

    public RegionCode(String apiKey, String midTempCsv, String midWeatherCsv) {
        this.geoApiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
        loadRegionCodes(midTempCsv, midTempCode);
        loadRegionCodes(midWeatherCsv, midWeatherCode);
    }

    private void loadRegionCodes(String csvFilePath, Map<String, String> regionCodeMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String regionName = parts[0].trim();
                    String regionCode = parts[1].trim();
                    regionCodeMap.put(regionName, regionCode);
                }
            }
            System.out.println("Loaded region codes from " + csvFilePath + ": " + regionCodeMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getRegionCodes(double latitude, double longitude) {
        String regionName = getRegionNameByCoordinates(latitude, longitude);
        System.out.println("Region name for coordinates (" + latitude + ", " + longitude + "): " + regionName);

        String code1 = midTempCode.getOrDefault(regionName, "코드 없음");
        String code2 = midWeatherCode.getOrDefault(regionName, "코드 없음");
        return new String[]{code1, code2};
    }

    private String getRegionNameByCoordinates(double latitude, double longitude) {
        LatLng location = new LatLng(latitude, longitude);
        try {
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, location).await();
            if (results.length > 0) {
                for (GeocodingResult result : results) {
                    System.out.println("Full result: " + result.formattedAddress);
                    for (com.google.maps.model.AddressComponent component : result.addressComponents) {
                        System.out.println("Component: " + component.longName);
                        System.out.println("Types: " + Arrays.toString(component.types));
                        if (component.types.length > 0) {
                            if (component.types[0].equals("administrative_area_level_1")) {
                                return component.longName;
                            }
                            if (component.types[0].equals("locality")) {
                                return component.longName;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}
