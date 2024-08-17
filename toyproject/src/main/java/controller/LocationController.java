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
        String apiKey = "YOUR_GOOGLE_API_KEY";
        geoApiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
        
        // CodeLoaderServlet에서 CodeLoader 객체를 가져옵니다.
        CodeLoaderServlet codeLoaderServlet = (CodeLoaderServlet) getServletContext().getServlet("CodeLoaderServlet");
        codeLoader = codeLoaderServlet.getCodeLoader();
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
        	double latitude = Double.parseDouble(request.getParameter("latitude"));
            double longitude = Double.parseDouble(request.getParameter("longitude"));

            // 위도와 경도를 사용해 중기예보코드 가져오기
            String midTempCode = regionCode.getMidTempCode(latitude, longitude);
            
            // HttpSession에 중기예보구역코드 저장
            HttpSession session = request.getSession();
            session.setAttribute("forecastCode", midTempCode);
            
            out.println("중기예보구역코드: " + midTempCode);

        } catch (NumberFormatException e) {
        	 out.println("Error: Invalid latitude or longitude format");
        }
	}

	private void handleMidWeatherLocation(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
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