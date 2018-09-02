/*
Servlet to do backend computations to generate checkbox and lables in jsp UI.
This is based on MVC archtecture, where UI is jsp and controller is Servlet.
This servlet is used to compute no of default columns and set difference of defaults columns and 
all columns in logger.
*/

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashSet;
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

/**
 *
 * @author Administrator
 */
@WebServlet(urlPatterns = {"/DefaultColumn"})
public class DefaultColumn extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        try
        {
            //String array s is storing all column name from reporter
            CompanyReporterChange obj=new CompanyReporterChange();
            String s[]=obj.getAllColumn();
            
            HashSet<String> mySet = new HashSet<String>(Arrays.asList(s));
            //setting attribute for the array of String of all columns name in Reporter
            request.setAttribute("reporterColArray", mySet);
            
            //In trial class(getKeyLogger function) its returning all the column name which we are getting from logger, Storing in HashSet
            Trial ob=new Trial();
            HashSet hs=ob.getKeyLogger();     
            //remove all (set difference) is used to find out that which of the columns are not in reporter, thus giving user option to only insert 
            //the columns which are not in reporter
            hs.removeAll(mySet);
            //reporterColSet is used to set the value of columns that can be added
            request.setAttribute("reporterColSet", hs);
            
            
            
            //temp start will check if the failureTable is having a record ot not, if not the user input will be taken, or else default value will be given
            LocalDB objDB=new LocalDB();
            Connection con=objDB.jdbcConnection();
            String tempStartPart = null;
            
            //No of records is used to get no of records in, according to that user input of from date was there
            int noOfRecordsFailuretable=objDB.noOfRecords(con);
            request.setAttribute("noOfRecordsFailuretable", noOfRecordsFailuretable);
            if(noOfRecordsFailuretable==0)
            {
            }
            else
            {
                objDB.updateLastRecord(con);
                tempStartPart=objDB.retriveLastData(con);
            }
            request.setAttribute("tempStartPart", tempStartPart);
            //sending request to index.jsp where all the other UI part are there
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        catch (Exception ex) 
        {
            Logger.getLogger(DefaultColumn.class.getName()).log(Level.SEVERE, null, ex);
        }

    
    }
}
