<%-- 
    Document   : index
    Created on : 22 Jun, 2018, 11:13:23 AM
    Author     : Administrator
index.jsp in UI part of the project, dynemically information is taken from DefaultColumns and are framed in 
form of UI in JSP.
The form submission is redirectind to Add.servlet where the calling of main functions are there(backend).
--%>

<%@page import="java.sql.Connection"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashSet"%>
<%@ page import="newpackage.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Logger To Reporter</title>
        
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    </head>
    <body>
        <div class="container">
            <h3>Col already in table are:</h3><br />
            <%--
            Scriptlet to print the defaults Columns
            --%>
        <% 
            HashSet<String> s = (HashSet<String>) request.getAttribute("reporterColArray");
            Iterator iterator = s.iterator();
            while (iterator.hasNext()) 
            {
                out.println(iterator.next().toString()+"<br />");
            } 
        %>
        <br><br>   <h4> Want to insert Col? Check the boxes if you want to enter it. else submit </h4>
        <%--
            Form having checkboxes for adding table.
        --%>
        <form action="Add" method="GET">
            <%--
                Scriptlet to make checkBoxes of columns to be added
            --%>
            <%
                HashSet<String> hs = (HashSet<String>) request.getAttribute("reporterColSet");
                 iterator = hs.iterator();
                while (iterator.hasNext()) 
                {
                    String temp = iterator.next().toString();
                    out.print("<input type=\"checkbox\" name= \"name\" value=\""+temp+"\">"+temp+"<br>");
                } 
            %>
            <%--
                Scriptlet to have textfiled for from date, if the textbox have null value then the user will have to write FromDate
                otherwise no need to write fromDate, it will be default entry.
            --%>
            <%
                int no=Integer.parseInt(request.getAttribute("noOfRecordsFailuretable").toString());
                String tempStartPart=(String)request.getAttribute("tempStartPart");
                out.println("<input type=\"text\" name=\"fromDate\" value=\""+tempStartPart+"\"> ");
            %>
        <input type="submit" value="Submit">
        </form> 
        </div>
    </body>
</html>
