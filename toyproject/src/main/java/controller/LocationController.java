package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.maps.GeoApiContext;

import service.Coordinate;
import service.RegionCode;

@WebServlet({ "/shortlocation", "/finedustLocation", "/midweatherLocation", "/midtempLocation" })
public class LocationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private GeoApiContext geoApiContext;
	private RegionCode regionCode;

    @Override
    public void init() throws ServletException {
    	super.init();
        // Google API 키 설정
        String apiKey = "AIzaSyAEOsOkR5QXRzz4Kjk2QDcJg3jpqdINwEE";
        geoApiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
        
        // RegionCode 객체 가져오기
        regionCode = (RegionCode) getServletContext().getAttribute("regionCode");
        if (regionCode == null) {
            throw new ServletException("RegionCode object not found in context. Please initialize RegionCodeServlet first.");
        }
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getServletPath();
        System.out.println("url : " + url);

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        switch(url) {
        	case "/shortlocation":
        		handleShortLocation(request, response);
        		break;
        		
        	case "/finedustLocation":
        		handleFinedustLocation(request, response);
        		break;
        		
        	case "/midweatherLocation":
        		handleMidWeatherLocation(request, response);
        		break;
        		
        	case "/midtempLocation":
        		handleMidTempLocation(request, response);
        		break;
        		
        	default:
                response.sendRedirect("404.jsp");
                break;
        }
	}

	private void handleMidTempLocation(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String region = request.getParameter("region");
            if (region == null || region.isEmpty()) {
                out.println("Error: Missing or empty 'region' parameter.");
                return;
            }

            String midTempCode = regionCode.getMidTempCode(region);

            out.println("<html><body>");
            out.println("<h2>중기 기온 코드</h2>");
            out.println("중기 기온 코드: " + (midTempCode != null ? midTempCode : "해당 지역의 코드 없음"));
            out.println("</body></html>");
        } catch (NumberFormatException e) {
        	 out.println("Error: Invalid latitude or longitude format");
        }
	}

	private void handleMidWeatherLocation(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String region = request.getParameter("region");
            if (region == null || region.isEmpty()) {
                out.println("Error: Missing or empty 'region' parameter.");
                return;
            }

            String midWeatherCode = regionCode.getMidWeatherCode(region);

            out.println("<html><body>");
            out.println("<h2>중기 육상 코드</h2>");
            out.println("중기 육상 코드: " + (midWeatherCode != null ? midWeatherCode : "해당 지역의 코드 없음"));
            out.println("</body></html>");
        } catch (NumberFormatException e) {
        	 out.println("Error: Invalid latitude or longitude format");
        }
	}

	private void handleFinedustLocation(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	private void handleShortLocation(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            double latitude = Double.parseDouble(request.getParameter("latitude"));
            double longitude = Double.parseDouble(request.getParameter("longitude"));

            // 좌표 변환
            Coordinate.Coord coord = Coordinate.convertLatLonToXY(latitude, longitude);

            // JSON 응답
            PrintWriter out = response.getWriter();
            out.print("{");
            out.print("\"x\": " + coord.getX() + ",");
            out.print("\"y\": " + coord.getY());
            out.print("}");
            out.flush();
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid latitude or longitude");
        }

	}
}