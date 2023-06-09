package controller.userservlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoadAppliedJobs extends HttpServlet {

Connection conn;
    public void init() throws ServletException {
            ServletContext context = getServletContext();
            conn = (Connection)context.getAttribute("connection");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {	
                if (conn != null) {
                    HttpSession session = request.getSession();
                    Integer userID = (Integer)session.getAttribute("logged-id");
                    
                    //get jobseeker id
                    String query = "SELECT * FROM JOBSEEKERS WHERE USER_ID = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setInt(1, userID);
                    ps.executeQuery();
                    ResultSet jobseeker = ps.executeQuery();
                    int seekerID = 0;
                    if(jobseeker.next()){
                        seekerID = jobseeker.getInt("SEEKER_ID");
                    }


                    if(request.getParameter("status")==null || request.getParameter("status").equals("")){
                    //get all JOB APPLICATIONS with the JOBSEEKERID
                        query = "SELECT * FROM APPLICATIONS "
                                    + "INNER JOIN JOBSEEKERS ON JOBSEEKERS.SEEKER_ID = APPLICATIONS.SEEKER_ID "
                                    + "INNER JOIN EMPLOYERS ON EMP_ID = EMPLOYER_ID "
                                    + "INNER JOIN JOBS ON JOBS.JOB_ID = APPLICATIONS.JOB_ID "
                                    + "INNER JOIN STATUSES ON APP_STATUS = STATUS_ID "
                                    + "INNER JOIN TYPES ON JOB_TYPE = TYPE_ID "
                                    + "INNER JOIN LEVELS ON JOB_LEVEL = LEVEL_ID "
                                    + "WHERE APPLICATIONS.SEEKER_ID = ?"
                        ;   
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, seekerID);
                    }else{
                        query = "SELECT * FROM APPLICATIONS "
                                    + "INNER JOIN JOBSEEKERS ON JOBSEEKERS.SEEKER_ID = APPLICATIONS.SEEKER_ID "
                                    + "INNER JOIN EMPLOYERS ON EMP_ID = EMPLOYER_ID "
                                    + "INNER JOIN JOBS ON JOBS.JOB_ID = APPLICATIONS.JOB_ID "
                                    + "INNER JOIN STATUSES ON APP_STATUS = STATUS_ID "
                                    + "INNER JOIN TYPES ON JOB_TYPE = TYPE_ID "
                                    + "INNER JOIN LEVELS ON JOB_LEVEL = LEVEL_ID "
                                    + "WHERE APPLICATIONS.SEEKER_ID = ? AND APP_STATUS = ?"
                        ;   
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, seekerID);
                        ps.setInt(2, Integer.parseInt(request.getParameter("status")));
                    }

                    ResultSet applications = ps.executeQuery();

                    request.setAttribute("applications", applications);
                    request.getRequestDispatcher("applied-jobs.jsp").forward(request,response);
                } else {
                    request.setAttribute("error-message", "Connection Error");
                    request.getRequestDispatcher("error.jsp").forward(request,response);
                }
        } catch (SQLException sqle){
                request.setAttribute("error-message", sqle.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request,response);
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
        processRequest(request, response);
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