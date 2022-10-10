package invoiceReqHandler;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import itemReqHandler.ItemHandler;
import jdbc.SqlConnector;
import security.DateGenerator;


public class InvoiceHandler {

	public int GetLatestInvoiceId() throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		
		PreparedStatement ps = con.prepareStatement("SELECT * from Invoice order by invoiceId desc limit 1");
        ResultSet rs = ps.executeQuery();
        int invoiceId=0;
        while(rs.next()){
        	invoiceId = rs.getInt("invoiceId");        	
         }
		
        con.close();
		return invoiceId;
	}
	
	public void InsertInvoicetItem(int invoiceId, int itemId, int quantity) throws SQLException
	{
		ItemHandler itemHandler = new ItemHandler();
		
		double tax = itemHandler.GetItemTax(itemId);
		double itemAmount = itemHandler.GetItemAmount(itemId);
		
		double totalAmountWithoutTax = itemAmount * quantity;
		double totalAmountWithTax = totalAmountWithoutTax + ((itemAmount *( tax/100.0) ) * quantity);
		
		Connection con = SqlConnector.ConnectDb();
		
		PreparedStatement ps = con.prepareStatement("Insert into InvoiceItem values(?,?,?,?,?)");
		ps.setInt(1, invoiceId);
		ps.setInt(2, itemId);
		ps.setInt(3, quantity);
		ps.setDouble(4, totalAmountWithoutTax);
		ps.setDouble(5, totalAmountWithTax);
		
		ps.executeUpdate();
		con.close();
	}
	
	public void InsertInvoice(int invoiceId, int custId, double discount, double totalCostPrice) throws SQLException
	{
		DateGenerator dateGenerator = new DateGenerator();
		Date date = dateGenerator.CurrentDateTime();
		
		double discountAmount = 0;
		ItemHandler itemHandler = new ItemHandler();
		double totalAmountWithoutTax = itemHandler.GetTotalAmountWithoutTax(invoiceId);
		double totalAmountWithTax = itemHandler.GetTotalAmountWithTax(invoiceId);
		
		if(discount != 0) {
			discountAmount =  (totalAmountWithoutTax * (discount /100.0));
		}
		
		double totalAmount = totalAmountWithTax - discountAmount;
		int total = (int)Math.round(totalAmount);
		double profit = (totalAmountWithoutTax - discountAmount) - totalCostPrice;
		
		
		Connection con = SqlConnector.ConnectDb();

		PreparedStatement ps = con.prepareStatement("insert into Invoice(invoiceDate, custId, discount, totalAmtWithoutTax, totalAmtWithTax, withoutTaxDiscount, totalAmount, profit) values(?,?,?,?,?,?,?,?)");
		ps.setDate(1, date);
		ps.setInt(2, custId);
		ps.setDouble(3, discount);
		ps.setDouble(4, totalAmountWithoutTax);
		ps.setDouble(5, totalAmountWithTax);
		ps.setDouble(6, discountAmount);
		ps.setInt(7, total);
		ps.setDouble(8, profit);
		
		ps.executeUpdate();
		con.close();
		
	}
	
	public JSONArray GetInvoice(int invoiceId) throws SQLException, JSONException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps ;
		
		if(invoiceId != 0)
		{
			ps = con.prepareStatement("SELECT * FROM Invoice WHERE invoiceId = ?");
			ps.setInt(1, invoiceId);
		}
		else
		{
			ps = con.prepareStatement("SELECT * FROM Invoice");
		}
		
		ResultSet rs = ps.executeQuery();
		
		JSONArray itemDetails = new JSONArray();
		
		while (rs.next()) {
			JSONObject itemObject = new JSONObject();
			if(rs.getInt("invoiceId") != 0)
			{
				itemObject.put("invoiceId", rs.getInt("invoiceId"));
				itemObject.put("invoiceDate", rs.getDate("invoiceDate"));
				itemObject.put("custId", rs.getInt("custId"));
				itemObject.put("custId", rs.getDouble("discount"));
				itemObject.put("totalAmount", rs.getDouble("totalAmount"));
				itemObject.put("paymentStatus", rs.getBoolean("paymentStatus"));
				itemDetails.put(itemObject);
			}
        }
		con.close();
		return itemDetails;
	}
	
	
}
