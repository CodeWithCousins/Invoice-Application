package itemReqHandler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jdbc.SqlConnector;
import security.DateGenerator;

public class ItemHandler {

	public JSONArray GetItem(int itemId) throws SQLException, JSONException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps ;
		
		if(itemId != 0)
		{
			ps = con.prepareStatement("SELECT * FROM Items WHERE itemId = ?");
			ps.setInt(1, itemId);
		}
		else
		{
			ps = con.prepareStatement("SELECT * FROM Items");
		}
		
		ResultSet rs = ps.executeQuery();
		
		JSONArray itemDetails = new JSONArray();
		
		while (rs.next()) {
			JSONObject itemObject = new JSONObject();
			if(rs.getInt("itemId") != 0)
			{
				itemObject.put("itemId", rs.getInt("itemId"));
				itemObject.put("itemName", rs.getString("itemName"));
				itemObject.put("itemStock", rs.getInt("stock"));
				itemDetails.put(itemObject);
			}
        }
		con.close();
		return itemDetails;
	}
	
	public void InsertItem(String name, double costPrice, double sellingPrice, double tax, int stock) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		
		PreparedStatement ps = con.prepareStatement("INSERT INTO Items(itemName, costPrice, sellingPrice, tax, stock) values(?,?,?,?,?)");
		ps.setString(1, name);
		ps.setDouble(2, costPrice);
		ps.setDouble(3, sellingPrice);
		ps.setDouble(4, tax);
		ps.setInt(5, stock);
		
		ps.executeUpdate();
		con.close();
	}
	
	public void UpdateItem(int itemId, String itemName, double costPrice, double sellingPrice, double tax, int stock) throws SQLException
	{ 
		Connection con = SqlConnector.ConnectDb();
		
		if(itemName != "")
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Items SET itemName = ? WHERE itemId = ?");
			ps.setString(1, itemName);
			ps.setInt(2, itemId);
			
			ps.executeUpdate();
		}
		if(costPrice != 0)
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Items SET costPrice = ? WHERE itemId = ?");
			ps.setDouble(1, costPrice);
			ps.setInt(2, itemId);
			
			ps.executeUpdate();
		}
		if(sellingPrice != 0)
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Items SET sellingPrice = ? WHERE itemId = ?");
			ps.setDouble(1, sellingPrice);
			ps.setInt(2, itemId);
			
			ps.executeUpdate();
		}
		if(tax != 0)
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Items SET tax = ? WHERE itemId = ?");
			ps.setDouble(1, tax);
			ps.setInt(2, itemId);
			
			ps.executeUpdate();
		}
		if(stock != 0)
		{
			PreparedStatement ps = con.prepareStatement("UPDATE Items SET stock = ? WHERE itemId = ?");
			ps.setInt(1, stock);
			ps.setInt(2, itemId);
			
			ps.executeUpdate();
		}
		con.close();
	}
	
	public void DeleteItem(int id) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("DELETE FROM Items WHERE itemId = ?");
		ps.setInt(1, id);
		ps.executeUpdate();
		con.close();
	}
	
	public boolean IsItemAvailable(int itemId, int quantity) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("SELECT * FROM Items WHERE itemId = ?");
		ps.setInt(1, itemId);
		
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
			if(rs.getInt("stock") >= quantity)
			{
				con.close();
				return true;
			}
        }
		con.close();
		return false;
	}
	
	public void ReduceItemStock(int itemId, int quantity) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("SELECT * FROM Items WHERE itemId = ?");
		ps.setInt(1, itemId);
		
		ResultSet rs = ps.executeQuery();
		int stock =0;
		while (rs.next()) {
			stock = rs.getInt("stock");
        }
		
		stock = stock - quantity;
		PreparedStatement ps1 = con.prepareStatement("UPDATE Items SET stock = ? WHERE itemId = ?");
		ps1.setInt(1, stock);
		ps1.setInt(2, itemId);
		
		ps1.executeUpdate();
		con.close();
		
	}
	
	public double GetItemAmount(int itemId) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("SELECT * FROM Items WHERE itemId = ?");
		ps.setInt(1, itemId);
		
		ResultSet rs = ps.executeQuery();
		double amount =0;
		
		while (rs.next()) {
			amount = rs.getDouble("sellingPrice");
        }
		con.close();
		return amount;
	}
	
	public double GetItemTax(int itemId) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("SELECT * FROM Items WHERE itemId = ?");
		ps.setInt(1, itemId);
		
		ResultSet rs = ps.executeQuery();
		double tax =0;
		
		while (rs.next()) {
			tax = rs.getDouble("tax");
        }
		con.close();
		return tax;
	}
	
	
	
	public double GetTotalAmountWithoutTax(int invoiceId) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("select SUM(itemTotalWithoutTax) as amount from InvoiceItem where invoiceId = ?");
		ps.setInt(1, invoiceId);
		
		ResultSet rs = ps.executeQuery();
		double totalAmountWithoutTax =0;
		
		while (rs.next()) {
			totalAmountWithoutTax = rs.getDouble("amount");
        }
		con.close();
		return totalAmountWithoutTax;
	}
	
	
	public double GetTotalAmountWithTax(int invoiceId) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("select SUM(itemTotalWithTax) as amount from InvoiceItem where invoiceId = ?");
		ps.setInt(1, invoiceId);
		
		ResultSet rs = ps.executeQuery();
		double totalAmountWithTax =0;
		
		while (rs.next()) {
			totalAmountWithTax = rs.getDouble("amount");
        }
		con.close();
		return totalAmountWithTax;
	}
	
	public double GetCostPrice(int itemId) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("select * from Items where itemId = ?");
		ps.setInt(1, itemId);
		
		ResultSet rs = ps.executeQuery();
		double costPrice =0;
		
		while (rs.next()) {
			costPrice = rs.getDouble("costPrice");
        }
		con.close();
		return costPrice;
	}
	
}
