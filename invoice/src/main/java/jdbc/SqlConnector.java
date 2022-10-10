package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;


public class SqlConnector {

	public static String url      = "jdbc:mysql://localhost:3306/ZBooks";
	public static String username = "root";
	public static String password = "root";
	public static String driver   = "com.mysql.cj.jdbc.Driver";

	public static Connection ConnectDb()
	{

		Connection con = null;

		try
		{
			Class.forName(SqlConnector.driver);
			con = DriverManager.getConnection(SqlConnector.url,SqlConnector.username,SqlConnector.password);
		} 
		catch (Exception e) 
		{
			System.out.println(e);
		}
		return con;
	}

}
