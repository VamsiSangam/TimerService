/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import classes.App;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Vamsi
 */
public class CheckTimer extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CheckTimer</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CheckTimer at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession s = request.getSession();
        String email = s.getAttribute("email").toString();
        PrintWriter pw = response.getWriter();
        response.setContentType("text/plain");

        if (email != null) {
            pw.println(getRemainingTime(email));
        } else {
            pw.println("Unauthorised request.");
        }
    }

    public long getRemainingTime(String email) {
        long result;

        try {
            Class.forName(App.DRIVER_CLASS);

            try (Connection con = DriverManager.getConnection(App.CONNECTION_STRING)) {
                PreparedStatement pst = con.prepareStatement("select top 1 expiry from " + App.TIMERS_TABLE + " where email = ? and expiry > dateadd(minute, 330, sysutcdatetime()) order by expiry");

                pst.setString(1, email);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                    Date proper = df.parse(rs.getString(1));
                    long expiry = proper.getTime() / 1000;
                    long now = (new Date().getTime() + (330 * 60 * 1000)) / 1000;

                    result = expiry - now;
                } else {
                    result = -1;    // no timers
                }

                rs.close();
                pst.close();
            }
        } catch (Exception ex) {
            result = -1;    // exception

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(this.getServletContext().getRealPath("/") + "/log.txt"));

                bw.write("Exception in getRemainingTime() - " + ex.toString() + "\n");

                bw.close();
            } catch (Exception e) {
                System.out.println("Could not write to log.");
            }

            System.out.println("Exception in CheckTimer.java - " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
