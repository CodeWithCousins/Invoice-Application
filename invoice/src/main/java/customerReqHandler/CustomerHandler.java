package customerReqHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jdbc.SqlConnector;

public class CustomerHandler {
	
	public JSONArray GetCustomer(int customerId) throws SQLException, JSONException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps;
		if(customerId != 0)
		{
			ps = con.prepareStatement("SELECT * FROM Customers WHERE custId = ?");
			ps.setInt(1, customerId);
		}
		else
			ps = con.prepareStatement("SELECT * FROM Customers");
		
		ResultSet rs = ps.executeQuery();
		JSONArray custDetails = new JSONArray();
		
		while (rs.next()) {
			JSONObject custObject = new JSONObject();
			custObject.put("customerId", rs.getInt("custId"));
			custObject.put("customerName", rs.getString("custName"));
			custDetails.put(custObject);
        }
		con.close();
		return custDetails;
	}
	
	public void InsertCustomer(String name, long phnNo, String address) throws SQLException
	{

		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("INSERT INTO Customers(custName, custPhnNo, custAddress) values(?,?,?)");
		ps.setString(1, name);
		ps.setLong(2, phnNo);
		ps.setString(3, address);
		ps.executeUpdate();
		con.close();
	}
	
	public void UpdateCustomer(int id, String name, long phnNo, String address) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		
		if(name != "")
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Customers SET custName = ? WHERE custId = ?");
			ps.setString(1, name);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
		if(phnNo != 0)
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Customers SET custPhnNo = ? WHERE custId = ?");
			ps.setLong(1, phnNo);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
		if(address != "")
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Customers SET custAddress = ? WHERE custId = ?");
			ps.setString(1, address);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
		con.close();
	}
	public int DeleteCustomer(int id) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("DELETE FROM Customers WHERE custId = ?");
		ps.setInt(1, id);
		int res = ps.executeUpdate();
		con.close();
		return res;
	}

}
