package newpackage;
/*
This class is used as beans to compute all functions related to ZOHO Reporter
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
import static newpackage.Trial.dayPassed;
import org.json.simple.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class CompanyReporterChange {

    //to check if JSONArray is initialised only once
    static int count = 0;
    static JSONArray ja;
    static String pre;
    //function to make table every date days
    synchronized public void checkDates(String toDateTime,int date)
    {
        if(dayPassed>=date )
        {
            dayPassed=0;
            rename(pre);
            pre=toDateTime;
            insertIntoReporter("New");
        }
        else
        {
            insertIntoReporter("New");
        }
    }
    
    synchronized public void rename(String toDate)
    {
        try
        {
            String email = "ishankvasania09@gmail.com";
            String dbname = "Database - 1";
            String authtoken = "b77730e74aea3fe561402aef6b86a3d3";
            Map config = new HashMap();
            ReportClient rc = new ReportClient(authtoken);
            String uri = rc.getURI(email,dbname);
            rc.renameView(uri,"New",toDate.replace("%20"," "),"Description",config);
        }
        catch(Exception e)
        {
            System.out.println("feferferf");
        }
    }
    
    //Function to insert the JSON file data into reporter
    synchronized public void insertIntoReporter(String toDateTime) 
    {
        try
            {
            Thread.sleep(1000);
            System.out.println("fwfwefwefwefewfwfw   fve   "+toDateTime);
            String email = "ishankvasania09@gmail.com";
            String dbname = "Database - 1";
            String tbname = toDateTime.replace("%20"," ");
            String authtoken = "b77730e74aea3fe561402aef6b86a3d3";
            Map config = new HashMap();
            File csvFile = new File("C:\\test.json");
            ReportClient rc = new ReportClient(authtoken);
            String uri = rc.getURI(email, dbname, tbname);
            config.put("ZOHO_AUTO_IDENTIFY", "true");
            config.put("ZOHO_ON_IMPORT_ERROR", "ABORT");
            config.put("ZOHO_CREATE_TABLE", "true");
            config.put("ZOHO_IMPORT_FILETYPE ", "JSON");
            Object result = rc.importData(uri, "APPEND", csvFile, config, false);
        }
        catch(Exception e)
        {
            System.out.println("insertIntoReporter   "+e);
        }
    }

    //Function to add columns in zoho Reporter
    synchronized public void addCol(String name, String type) throws Exception 
    {
        LocalDB obj=new LocalDB();
        Connection con=obj.jdbcConnection();
        String lastTable=obj.retriveLastData(con);
        String url = "https://reportsapi.zoho.com/api/ishankvasania09@gmail.com/Database%20-%201/Zoho?ZOHO_ACTION=ADDCOLUMN&ZOHO_OUTPUT_FORMAT=XML&ZOHO_ERROR_FORMAT=XML&ZOHO_API_VERSION=1.0&authtoken=b77730e74aea3fe561402aef6b86a3d3&ZOHO_COLUMNNAME=" + name.replace(" ", "%20") + "&ZOHO_DATATYPE=" + type.replace(" ", "%20");
        new Trial().getRequest(url);
        con.close();
        System.out.println("\nCOLUMN ADDED    ");
    }
    
    
    
    //Function to get name of all the columns in zoho Reporter, using getRequest function to send the get request
    synchronized public String[] getAllColumn() throws Exception 
    {
        LocalDB obj=new LocalDB();
        Connection con=obj.jdbcConnection();
        //String lastTable=obj.retriveLastData(con);
        String colArray[] = null;
        String url = "https://reportsapi.zoho.com/api/ishankvasania09@gmail.com/Database%20-%201?ZOHO_ACTION=DATABASEMETADATA&ZOHO_OUTPUT_FORMAT=JSON&ZOHO_ERROR_FORMAT=JSON&ZOHO_API_VERSION=1.0&authtoken=b77730e74aea3fe561402aef6b86a3d3&ZOHO_METADATA=ZOHO_CATALOG_INFO";
        String main = new Trial().getRequest(url);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(main);
        json = (JSONObject) parser.parse(json.get("response").toString());
        json = (JSONObject) parser.parse(json.get("result").toString());
        Object obj12 = parser.parse(json.get("views").toString());
        JSONArray array = (JSONArray) obj12;
        int len = array.size();
        for (int i = 0; i < len; i++) 
        {
            json = (JSONObject) parser.parse(array.get(i).toString());
            String temp = json.get("tableName").toString();
            if (temp.equals("Zoho")) 
            {
                obj12 = parser.parse(json.get("columns").toString());
                array = (JSONArray) obj12;
                int n = array.size();
                colArray = new String[n - 1];
                for (int j = 1; j < n; j++) 
                {
                    json = (JSONObject) parser.parse(array.get(j).toString());
                    colArray[j - 1] = json.get("columnName").toString();
                }
                break;
            }
        }
        return colArray;
    }

    //Function to make a JSON array out of JSON Object, to insert into the file named test.json in C Drive
    synchronized public void createFile(String parameters[], String allColName[]) throws Exception 
    {
        JSONObject allColumns = new JSONObject();
        int n = parameters.length;
        for (int i = 0; i < n; i++) {
            if (i == 8) 
            {
                Date d = new Date(Long.parseLong(parameters[8]));
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdfDate.format(d);
                allColumns.put(allColName[i], date);
            } 
            else 
            {
                allColumns.put(allColName[i], parameters[i]);
            }
        }
      //  Thread.sleep(600);
        if (count == 0) {
            ja = new JSONArray();
        }
        System.out.println(allColumns.toString());
        ja.add(allColumns);
        count = 100;
    }
    //Function to write all the arrays into File and truncating it everytime.
    synchronized public void appendFile() throws IOException 
    {
        try (FileWriter file = new FileWriter("C:\\test.json")) 
        {

            file.write(ja.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
