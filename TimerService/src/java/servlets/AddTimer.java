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
public class AddTimer extends HttpServlet {

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
            out.println("<title>Servlet AddTimer</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddTimer at " + request.getContextPath() + "</h1>");
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
        String expiry = request.getParameter("date");
        PrintWriter pw = response.getWriter();
        response.setContentType("text/plain");
        
        if (!checkDuplicateTimer(email, expiry)) {
            if (addTimer(email, expiry)) {
                pw.println("0");
            } else {
                pw.println("2");    // Error
            }
        } else {
            pw.println("1");        // Duplicate timer
        }
    }
    
    public boolean addTimer(String email, String expiry) {
        try {
            Class.forName(App.DRIVER_CLASS);
            
            try (Connection con = DriverManager.getConnection(App.CONNECTION_STRING)) {
                PreparedStatement pst = con.prepareStatement("insert into " + App.TIMERS_TABLE + " values(?, ?)");
                
                pst.setString(1, email);
                pst.setString(2, expiry);
                
                boolean result = pst.executeUpdate() == 1;
                
                pst.close();
                
                return result;
            }
        } catch (Exception ex) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(this.getServletContext().getRealPath("/") + "/log.txt"));
                
                bw.write("Exception in addUser() - " + ex.toString() + "\n");
                
                bw.close();
            } catch (Exception e) {
                System.out.println("Could not write to log");
            }
            
            System.out.println("Exception in AddUser.java - " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        
        return true;
    }
    
    public boolean checkDuplicateTimer(String email, String expiry) {
        try {
            Class.forName(App.DRIVER_CLASS);
            
            try (Connection con = DriverManager.getConnection(App.CONNECTION_STRING)) {
                PreparedStatement pst = con.prepareStatement("select * from " + App.TIMERS_TABLE + " where email = ? and expiry = ?");
                
                pst.setString(1, email);
                pst.setString(2, expiry);
                
                ResultSet rs = pst.executeQuery();
                
                boolean result = rs.next();
                
                rs.close();
                pst.close();
                
                return result;
            }
        } catch (Exception ex) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(this.getServletContext().getRealPath("/") + "/log.txt"));
                
                bw.write("Exception in checkDuplicateTimer() - " + ex.toString() + "\n");
                
                bw.close();
            } catch (Exception e) {
                System.out.println("Could not write to log.");
            }
            
            System.out.println("Exception in AddTimer.java - " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        
        return true;
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
