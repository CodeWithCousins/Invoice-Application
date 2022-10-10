package security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jdbc.SqlConnector;

public class KeySetter {
	
	public void SetEncryptionTokenInDb(String key, int id) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		
		PreparedStatement ps = con.prepareStatement("UPDATE Users SET token = ? WHERE id = ?");
		ps.setString(1, key);
		ps.setInt(2, id);
		
		ps.executeUpdate();
	}

}
