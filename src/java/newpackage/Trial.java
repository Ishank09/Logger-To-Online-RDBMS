package newpackage;

/* Program to Get Usefull Data from LOGGER and inserting it to ZOHO Reporter for study.Fully autometic
User have to Enter the from date and afetr it the program will run continuesly and will extract data 
from logger and insert into Zoho Reporter. The user is given option to add columns in between program.
This class is used as beans to compute all functions related to ZOHO LOGGER
*/

import com.adventnet.zoho.client.report.ReportClient;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.time.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Trial {
    
    String temporary_URL;
    static int dayPassed;
    //Common date format used in zoho Reporter
    static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
     //to check for next day, adding 1 day to fromDate and setting hour,minute,secound as 0
    static public Date nextDay(String tempStartPart) throws Exception
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFormat.parse(tempStartPart));
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        String aa=(dateFormat.format(cal.getTime()));
        Date newTime= dateFormat.parse(aa);
        //System.out.println(newTime);
        return newTime;
    }
    
    //to check fromDate and toDate, tempStartPart is fromDate and ans is toDate
    synchronized public String checkDates(String tempStartPart) throws Exception
    {
        Date date = new Date();
        //String toDate=dateFormat.format(date);
        String xxx=dateFormat.format(date);
        Date currentDate= dateFormat.parse(xxx);
        String ans=null; 
        System.out.println("tempstartpart    "+tempStartPart);
        Date newTime=nextDay(tempStartPart);
        System.out.println(currentDate+"  "+newTime);
        if (currentDate.after(newTime))
        {
            ans=dateFormat.format(newTime);
        }
        else
        {
            ans=dateFormat.format(currentDate);
        }
        return ans.replace(" ", "%20");
    }
    
    //function for automation of the program, fromdate and todate will continuesly change in it, making the program as infinite running program
    //without manual interpretation
    synchronized public void automationFunction(String tempStartPart) throws Exception 
    {
        LocalDB objDB=new LocalDB();
        Connection con=objDB.jdbcConnection();
        if(objDB.noOfRecords(con)==0)
            dayPassed=100;
        else
            dayPassed=objDB.retriveDayPassed(con);
        while (true) 
        {
            Trial http = new Trial();
            Date date = new Date();
            //Checking dates
            String toDateTime = checkDates(tempStartPart);
            http.temporary_URL = "&toDateTime=" + toDateTime + "&authtoken=49efb78df354b445eea72e816241099c";
            http.temporary_URL =  "fromDateTime="+tempStartPart.replace(" ","%20")+ http.temporary_URL;
            http.getDataFromLogger(tempStartPart.replace(" ","%20"));
            System.out.println("break start");
            //In every 15 minute the program will take data from logger
            //Thread.sleep(3000);
            System.out.println("break stop");
            new CompanyReporterChange().appendFile();
            //new CompanyReporterChange().insertIntoReporter(toDateTime);
            
            //toDate is name of table and the other parameter represent that
            //in show many days new Table must be created
            new CompanyReporterChange().checkDates(toDateTime,2);
            dayPassed++;
            //inserting the to date into Local DB
            objDB.preparedStatement(con, toDateTime, 0,dayPassed);
            String fromDateTemp=dateFormat.format(date);
            tempStartPart = (toDateTime.replace("%20"," "));
            
        }
    }

    

    //Function for having a col. extraction the data from JSON array of logger
    synchronized public String addColString(String colName, JSONObject jsonObject) 
    {
        return String.valueOf(jsonObject.get(colName));
    }
    
    //Get all keys from JSON of Logger
    synchronized public HashSet getKeyLogger()
    {
        String allCol[]={ "server_name","_zl_group_name","bytes_in","lb_ssl_remote_ip","device_category","lb_ip","req_id","path","thread_id","protocol","remote_ip","_zl_source","browser_version","user_agent","headers","ticket_digest","method","os","_zl_timestamp","service_name","os_version","_zl_host","zoid","lb_http_version","params","request_uri","time_taken","zuid","browser_name","bytes_out","build_id","service","_zl_location","server_port","account","status" };
   
        HashSet<String> mySet = new HashSet<String>(Arrays.asList(allCol)); 
        return mySet;
    }

    // HTTP GET request
    synchronized public String getRequest(String url) throws Exception 
    {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        int responseCode = con.getResponseCode();
        // System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    //Function to get information from Logger, using getRequest function to send the get request
    synchronized public void getDataFromLogger(String toDateTime) throws Exception 
    {
        String fixed_URL ="";   //TBC URL for live logs
        fixed_URL = fixed_URL + temporary_URL;
        System.out.println(fixed_URL);
        //data from logger
        LocalDB objDB=new LocalDB();
        Connection con=objDB.jdbcConnection();
        
        String allCol[] = new CompanyReporterChange().getAllColumn();

        String parameters[] = new String[allCol.length];
        String main = new Trial().getRequest(fixed_URL);
        System.out.println("main    " + main);
        JSONParser parser = new JSONParser();
        Object obj12 = parser.parse(main);
         
        JSONArray array = (JSONArray) obj12;
        int len = array.size();
        int flag=0;
        for (int i = 0; i < len; i++) {
            System.out.println(array.get(i));
            Object obj123 = JSONValue.parse(array.get(i).toString());
            JSONObject jsonObject = (JSONObject) obj123;
            //Thread.sleep(350);
            System.out.println(Arrays.toString(allCol));
            for (int j = 0; j < allCol.length; j++) {
               // Thread.sleep(200);
                parameters[j] = addColString(allCol[j], jsonObject);
                if(allCol[j].equals("path"))
                {
                    parameters[j]=parameters[j].replaceAll("/[0-9]+","");
                }
            }
            //Thread.sleep(400);
            new CompanyReporterChange().createFile(parameters, allCol);
            
        }
    }
}