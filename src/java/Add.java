/*
This Servlet is used as main backend, which get called from JSP UI(index.jsp) 
this Servlet is used to call the main function for autonomous work f the system.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import newpackage.LocalDB;
import newpackage.Trial;
import newpackage.CompanyReporterChange;
import org.apache.catalina.tribes.util.Arrays;

/**
 *
 * @author Administrator
 */
@WebServlet(urlPatterns = {"/Add"})
public class Add extends HttpServlet 
{
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
   {
        try
        {
            if(request.getParameter("name")==null)
            {}
            else
            {
                String name[]=request.getParameterValues("name");
                String type[]=new String[name.length];
                for(int i=0;i<name.length;i++)
                {
                    type[i]=isInteger(name[i]);
                    
                    new CompanyReporterChange().addCol(name[i],type[i]);
                }
            }
            LocalDB objDB=new LocalDB();
            Connection con=objDB.jdbcConnection();
            String tempStartPart=request.getParameter("fromDate");
            con.close();
            new Trial().automationFunction(tempStartPart);
        }
        catch (Exception ex) 
        {
            System.out.println("error  "+ex);
        }
          
   }
   
   //function to find if the given column have inteegr values or not.
   public String isInteger(String s) 
   {
        String integerCol[]={"req_id","thread_id","_zl_source","time_taken","zuid","server_port"};
        if(Arrays.toString(integerCol).indexOf(s)>=0)
            return "NUMBER";
        else
            return "PLAIN";
   }
}