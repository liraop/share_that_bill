package dbutils;

import java.sql.*;

public class DBhandler {
	
	private Connection con = null;
    private Statement st = null;
    private ResultSet rs = null;

    private String url;
    private String user;
    private String password;
    
    
    public DBhandler(){
    	this.url = "jbdc:mysql://sql4.freesqldatabase.com:3306/sql457251";
    	this.user = "sql457251";
    	this.password = "wX2*aK7%";
    }
    
    
    
    
}
