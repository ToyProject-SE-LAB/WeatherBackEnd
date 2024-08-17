package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import service.RegionCode;

@WebServlet("/regionCodeServlet")
public class RegionCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private RegionCode regionCode;

    @Override
    public void init() throws ServletException {
        super.init();
        String midWeatherCsvFilePath = "/midWeatherCode.csv";
        String midTempCsvFilePath = "/midTempCode.csv";
        regionCode = new RegionCode(midWeatherCsvFilePath, midTempCsvFilePath);
        getServletContext().setAttribute("regionCode", regionCode);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html><body>");
            out.println("<h2>코드 로더 초기화 완료</h2>");
            out.println("</body></html>");
        }
    }
    
    public RegionCode getRegionCode() {
        return regionCode;
    }
}
