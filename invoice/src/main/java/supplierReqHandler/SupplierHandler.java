package supplierReqHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jdbc.SqlConnector;

public class SupplierHandler {

	public JSONArray GetSupplier(int id) throws SQLException, JSONException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps ;
		if(id != 0)
		{
			ps = con.prepareStatement("SELECT * FROM Suppliers WHERE id = ?");
			ps.setInt(1, id);
		}
		else
			ps = con.prepareStatement("SELECT * FROM Suppliers");
		
		ResultSet rs = ps.executeQuery();
		JSONArray supplierDetails = new JSONArray();
		
		while (rs.next()) {
			JSONObject supplierObject = new JSONObject();
			supplierObject.put("supplierId", rs.getInt("id"));
			supplierObject.put("supplierName", rs.getString("name"));
			supplierObject.put("taxID", rs.getString("taxId"));
			supplierObject.put("phnNo", rs.getLong("phnNo"));
			supplierObject.put("address", rs.getString("address"));
			supplierDetails.put(supplierObject);
        }
		con.close();
		return supplierDetails;
	}
	
	public void InsertSupplier(String name, String taxId, long phnNo, String address) throws SQLException
	{

		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("INSERT INTO Suppliers(name, taxId, phnNo, address) values(?,?,?,?)");
		ps.setString(1, name);
		ps.setString(2, taxId);
		ps.setLong(3, phnNo);
		ps.setString(4, address);
		
		ps.executeUpdate();
		con.close();
	}
	
	public int DeleteSupplier(int id) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		
		int res = 0;
		PreparedStatement ps = con.prepareStatement("DELETE FROM Suppliers WHERE id = ?");
		ps.setInt(1, id);
		res = ps.executeUpdate();
		con.close();
		return res;
	}
	
	public void UpdateSupplier(int id, String taxId, String name, long phnNo, String address) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		if(name != "")
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Suppliers SET name = ? WHERE id = ?");
			ps.setString(1, name);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
		if(taxId != "")
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Suppliers SET taxId = ? WHERE id = ?");
			ps.setString(1, taxId);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
		if(phnNo != 0)
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Suppliers SET phnNo = ? WHERE id = ?");
			ps.setLong(1, phnNo);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
		if(address != "")
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Suppliers SET address = ? WHERE id = ?");
			ps.setString(1, address);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
		con.close();
	}
	
	public int GetSupplierId(String taxId) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("SELECT * FROM Suppliers WHERE taxId = ?");
		ps.setString(1, taxId);
		ResultSet rs = ps.executeQuery();
		int supplierId = 0;
		
		while (rs.next()) {
			if(rs.getInt("id") != 0)
				supplierId = rs.getInt("id");
        }
		return supplierId;
		
	}
}
