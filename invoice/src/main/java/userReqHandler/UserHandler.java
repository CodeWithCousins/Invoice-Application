package userReqHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jdbc.SqlConnector;

public class UserHandler {
	
	public void InsertUser(String userName, String email, int organizationId, String city, long phnNo, String credents) throws SQLException
	{

		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("INSERT INTO Users(organizationId, userName, email, city, phoneNumber, credentials) values(?,?,?,?,?,?)");
		ps.setInt(1, organizationId);
		ps.setString(2, userName);
		ps.setString(3, email);
		ps.setString(4, city);
		ps.setLong(5, phnNo);
		ps.setString(6, credents);
		ps.executeUpdate();
		con.close();
	}
	
	public JSONArray GetUser(int userId, int organization_id) throws SQLException, JSONException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps;
		if(userId != 0)
		{
			ps = con.prepareStatement("SELECT * FROM Users WHERE id = ? and organizationId = ?");
			ps.setInt(1, userId);
			ps.setInt(2, organization_id);
		}
		else
		{
			ps = con.prepareStatement("SELECT * FROM Users WHERE organizationId = ?");
			ps.setInt(1, organization_id);
		}
		
		ResultSet rs = ps.executeQuery();
		JSONArray userDetails = new JSONArray();
		
		while (rs.next()) {
			JSONObject userObject = new JSONObject();
			userObject.put("userId", rs.getInt("id"));
			userObject.put("phnNo", rs.getLong("phoneNumber"));
			userObject.put("userName", rs.getString("userName"));
			userDetails.put(userObject);
        }
		con.close();
		System.out.print("hiiii");
		return userDetails;
	}
	
	public int DeleteUser(int id, int organizationId) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("DELETE FROM Users WHERE id = ? and organizationId = ?");
		ps.setInt(1, id);
		ps.setInt(2, organizationId);
		int res = ps.executeUpdate();
		con.close();
		return res;
	}

}