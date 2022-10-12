package auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import jdbc.SqlConnector;
import security.Base;
import security.DateGenerator;

public class Authentication {
	
	public int IsValidUser(String encLoginCredentials) throws SQLException
	{
		if(!encLoginCredentials.contains("Basic "))
			return 0;
		
		int id = 0;
		Connection con = SqlConnector.ConnectDb();
		encLoginCredentials = encLoginCredentials.replace("Basic ","");
		PreparedStatement ps = con.prepareStatement("SELECT * FROM Users WHERE credentials = ?");
		ps.setString(1, encLoginCredentials);
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
            id = Integer.parseInt(rs.getString("id"));
        }
		
		con.close();
		return id;
	}
	
	public int IsValidCredentials(String token, int organizationId) throws SQLException, ParseException, JSONException
	{
		if(!token.contains("Bearer "))
			return 0;
		
		token = token.replace("Bearer ","");
		String[] seperatedToken = token.split("\\.");
		
		Base base64 = new Base();
		String decodedJsonExpTime = base64.Decode(seperatedToken[1]); // Decoded to string

		JSONObject json = new JSONObject(decodedJsonExpTime);  
		System.out.println(json.getString("expired at"));
		int id = json.getInt("id");
		DateGenerator dateGenerator = new DateGenerator();
		if(dateGenerator.TokenExpiryValidator(json.getString("expired at"))) // Checks the token validity
		{
			Connection con = SqlConnector.ConnectDb();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM Users WHERE token = ? and organizationId = ? and id = ?");
			ps.setString(1, token);
			ps.setInt(2, organizationId);
			ps.setInt(3, id);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				if(rs.getInt("id") != 0)
				{
					System.out.println("validdd");
					con.close();
					return id;
				}
	        }
		}
		
		return id;
	}
}
