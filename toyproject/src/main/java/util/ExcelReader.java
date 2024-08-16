package util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import vo.LocationInfo;



public class ExcelReader {
    private static final String FILE_NAME = "location.xlsx";

    public LocationInfo findClosestLocation(double userLat, double userLon) throws IOException {
        LocationInfo closestLocation = null; 		// 사용자의 현재 위치에 해당하는 엑셀 행
        double closestDistance = Double.MAX_VALUE;  // 가장 가까운 거리

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
             Workbook wb = WorkbookFactory.create(in)) {

            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 헤더 행 스킵

                double lat = Double.parseDouble(getCellValueAsString(row.getCell(7))); // 위도
                double lon = Double.parseDouble(getCellValueAsString(row.getCell(6))); // 경도

                double distance = calculateDistance(userLat, userLon, lat, lon);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestLocation = new LocationInfo(
                        getCellValueAsString(row.getCell(0)), // 행정구역코드
                        getCellValueAsString(row.getCell(1)), // 도
                        getCellValueAsString(row.getCell(2)), // 시
                        getCellValueAsString(row.getCell(3)), // 동
                        getCellValueAsString(row.getCell(4)), // x 좌표
                        getCellValueAsString(row.getCell(5)), // y 좌표
                        getCellValueAsString(row.getCell(6)), // 경도
                        getCellValueAsString(row.getCell(7)),  // 위도
                        getCellValueAsString(row.getCell(8)),  // 중기기온코드
                        getCellValueAsString(row.getCell(9)),  // 측정소명
                        getCellValueAsString(row.getCell(10))  // 중기육상코드
                    );
                }
            }
        }
        return closestLocation;
    }

    // 엑셀 값을 String으로 변환
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return null;
        }
    }

    // 사용자의 위치와 가까운 위치 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 반지름(km)
        double latDistance = Math.toRadians(lat2 - lat1); // 위도 차이
        double lonDistance = Math.toRadians(lon2 - lon1); // 경도 차이
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // 두 지정 사이의 거리 반환
        return R * c;
    }
}
