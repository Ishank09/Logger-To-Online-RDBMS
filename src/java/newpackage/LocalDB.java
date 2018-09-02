package newpackage;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
/*
Java program to have computations in local database. thus this program will handle all the local
database computations, from setting up connection to other values.
--------------------------------------------------------------------------------
DataBase details

CREATED	18-06-2018 18:52
LAST_DDL_TIME	19-06-2018 18:51
OWNER	HR
TABLE_NAME	FAILURETABLE
TABLESPACE_NAME	USERS
--------------------------------------------------------------------------------
SQL For database

  CREATE TABLE "HR"."FAILURETABLE" 
   (	"FROMDATE" VARCHAR2(20 BYTE), 
	"FAILUREORNOT" NUMBER, 
	"TOTALREQUEST" NUMBER, 
	"SERIALNO" NUMBER
   ) ;
CREATE OR REPLACE TRIGGER "HR"."SERIAL_BIR" 
BEFORE INSERT ON failuretable 
FOR EACH ROW
BEGIN
  SELECT serial_seq.NEXTVAL
  INTO   :new.serialno
  FROM   dual;
END;
--------------------------------------------------------------------------------
Triggers USed
SERIAL_BIR	BEFORE EACH ROW	HR	INSERT	ENABLED	FAILURETABLE
--------------------------------------------------------------------------------


Triggers are used to make sure that the serial number which is used to extract last
row have unique and continnues values.
 */

/**
 *
 * @author Administrator
 */
public class LocalDB
{
    //JDBC connection
    synchronized public Connection jdbcConnection() throws Exception
    {
        Class.forName("oracle.jdbc.driver.OracleDriver");  
        Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:root","hr","root"); 
        return con;
    }
    
    //prepared statement for inserting values in local table for error correction
    synchronized public void preparedStatement(Connection con,String fromDate,int failureOrNot,int daysPassed) throws Exception
    { 
        PreparedStatement stmt=con.prepareStatement("insert into failuretable values(?,?,?,?)"); 
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        System.out.println(fromDate);
        stmt.setString(1, fromDate.replace("%20"," "));
        stmt.setInt(2,failureOrNot);
        stmt.setInt(3,noOfRecords(con)+1);
        stmt.setInt(4,daysPassed);
        int i=stmt.executeUpdate(); 
    }
    
    //function to return max serial no
    synchronized public int maxSerialNo() throws Exception
    {
        Connection con=jdbcConnection();
        Statement stmt=con.createStatement();  
        ResultSet rs=stmt.executeQuery("select max(serialno)as p from failuretable");  
        int ans=0;
        while(rs.next())  
        {
               ans=(rs.getInt("p"));
        }
        return ans;
    }
    
    //updating last record, setting failure as 1, if the server gets down
    synchronized public void updateLastRecord(Connection con) throws Exception
    {
        System.out.println("Updated last record");
        Statement stmt=con.createStatement();  
        int result=stmt.executeUpdate("UPDATE failuretable SET failureOrNot = 1 where serialno = "+maxSerialNo());  
    }
    
    //no of records 
    synchronized public int noOfRecords(Connection con) throws Exception
    {
        Statement stmt=con.createStatement();  
        ResultSet rs=stmt.executeQuery("select count(serialno)as p from failuretable");  
        int ans=0;
        while(rs.next())  
        {
               ans=(rs.getInt("p"));
        }
        return ans;
    }
    
    //retrive last data in case the server get closed 
    synchronized public String retriveLastData(Connection con) throws Exception
    {
        Statement stmt=con.createStatement();  
        ResultSet rs=stmt.executeQuery("select fromDate from failuretable where serialno = "+maxSerialNo());  
        String ans = null;
        while(rs.next())  
        {
               ans=(rs.getString("fromDate"));
        }
        return ans;
    }
    
    //retrive days passed of last data
    synchronized public int retriveDayPassed(Connection con) throws Exception
    {
        Statement stmt=con.createStatement();  
        ResultSet rs=stmt.executeQuery("select dayPassed from failuretable where serialno = "+maxSerialNo());  
        int ans = 0;
        while(rs.next())  
        {
               ans=(rs.getInt("dayPassed"));
        }
        return ans;
    }
    
}