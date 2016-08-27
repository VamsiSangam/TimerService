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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Vamsi
 */
public class Validate extends HttpServlet {

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
            out.println("<title>Servlet Validate</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Validate at " + request.getContextPath() + "</h1>");
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
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if (validatePassword(email, password)) {
            HttpSession s = request.getSession();
            
            s.setAttribute("email", email);
            s.setAttribute("password", password);
            response.sendRedirect("/TimerService/User/");
        } else {
            response.sendRedirect("/TimerService/");
        }
    }
    
    public boolean validatePassword(String email, String password) {
        try {
            Class.forName(App.DRIVER_CLASS);
            
            try (Connection con = DriverManager.getConnection(App.CONNECTION_STRING)) {
                PreparedStatement pst = con.prepareStatement("select * from " + App.USERS_TABLE + " where email = ? and password = ?");
                
                pst.setString(1, email);
                pst.setString(2, password);
                
                ResultSet rs = pst.executeQuery();
                boolean result = rs.next();
                
                rs.close();
                pst.close();
                
                return result;
            }
        } catch (Exception ex) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(this.getServletContext().getRealPath("/") + "/log.txt"));
                
                bw.write("Exception in validatePassword() - " + ex.toString() + "\n");
                
                bw.close();
            } catch (Exception e) {
                System.out.println("Could not write to log");
            }
            
            System.out.println("Exception in Validate.java - " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        
        return false;
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
