package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.Coordinate;

@WebServlet("/CoordinateServlet")
public class CoordinateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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