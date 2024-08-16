package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.FinedustApi;
import vo.LocationInfo;
import util.ExcelReader;

@WebServlet("/finedust")
public class FinedustController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ExcelReader excelReader;
    private FinedustApi finedustApi;

    @Override
    public void init() throws ServletException {
        excelReader = new ExcelReader();
        finedustApi = new FinedustApi();
    }
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 사용자의 현재 위도와 경도 값을 Geolocation API로부터 받는다.
        double userLat = Double.parseDouble(request.getParameter("latitude"));
        double userLon = Double.parseDouble(request.getParameter("longitude"));

        // 가장 가까운 위치 정보를 얻는다.
        LocationInfo locationInfo = excelReader.findClosestLocation(userLat, userLon);
        if (locationInfo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found.");
            return;
        }
		
        String stationName = locationInfo.getStationName(); // 측정소명
        
        try {
        	List<String[]> dataList = finedustApi.fetchData(stationName);
			
			request.setAttribute("dataList", dataList);
			request.setAttribute("locationInfo", locationInfo); // 위치 정보 전달
			// jsp로 포워딩
			request.getRequestDispatcher("/FinedustData_form.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}