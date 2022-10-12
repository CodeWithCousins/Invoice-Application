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
	
	public void InsertInvoice(int invoiceId, int custId, double discount, double totalCostPrice, int userId) throws SQLException
	{
		DateGenerator dateGenerator = new DateGenerator();
		Date date = dateGenerator.CurrentDateTime();

		double discountAmount = 0;
		ItemHandler itemHandler = new ItemHandler();
		double totalAmountWithoutTax = itemHandler.GetTotalAmountWithoutTax(invoiceId);
		double totalAmountWithTax = itemHandler.GetTotalAmountWithTax(invoiceId);
		if(discount != 0) 
			discountAmount =  (totalAmountWithoutTax * (discount /100.0));
		
		double totalAmount = totalAmountWithTax - discountAmount;
		int total = (int)Math.round(totalAmount);
		double profit = (totalAmountWithoutTax - discountAmount) - totalCostPrice;
		
		
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("insert into Invoice(invoiceDate, custId, discount, totalAmtWithoutTax, totalAmtWithTax, withoutTaxDiscount, totalAmount, profit, userId) values(?,?,?,?,?,?,?,?,?)");
		ps.setDate(1, date);
		ps.setInt(2, custId);
		ps.setDouble(3, discount);
		ps.setDouble(4, totalAmountWithoutTax);
		ps.setDouble(5, totalAmountWithTax);
		ps.setDouble(6, discountAmount);
		ps.setInt(7, total);
		ps.setDouble(8, profit);
		ps.setInt(9, userId);
		
		ps.executeUpdate();
		con.close();
		
	}
	
	public JSONArray GetInvoice(int invoiceId) throws SQLException, JSONException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = null ;
		JSONArray itemDetails = new JSONArray();
		if(invoiceId != 0)
		{
			ps = con.prepareStatement("select Items.itemName, Items.sellingPrice, Items.tax , i.userId, i.paymentStatus, i.invoiceId, i.invoiceDate,i.quantity, i.itemTotalWithTax, i.discount, i.totalAmount, i.custId from (select InvoiceItem.itemid,Invoice.invoiceDate, Invoice.invoiceId, Invoice.paymentStatus, Invoice.custId, InvoiceItem.quantity, InvoiceItem.itemTotalWithTax, Invoice.discount,Invoice.userId, Invoice.totalAmount from InvoiceItem inner join Invoice on InvoiceItem.invoiceId = Invoice.invoiceId where Invoice.invoiceId= ? ) as i inner join Items on i.itemId = Items.itemId;");
			ps.setInt(1, invoiceId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject itemObject = new JSONObject();
				itemObject.put("invoiceId", rs.getInt("invoiceId"));
				itemObject.put("userId", rs.getInt("userId"));
				itemObject.put("invoiceDate", rs.getDate("invoiceDate"));
				itemObject.put("custId", rs.getInt("custId"));
				itemObject.put("itemName", rs.getString("itemName"));
				itemObject.put("itemPrice", rs.getDouble("sellingPrice"));
				itemObject.put("quantity", rs.getInt("quantity"));
				itemObject.put("tax", rs.getDouble("tax"));
				itemObject.put("totalAmountWithTax", rs.getDouble("itemTotalWithTax"));
				itemObject.put("discount", rs.getDouble("discount"));
				itemObject.put("totalAmount", rs.getInt("totalAmount"));
				itemObject.put("paymentStatus", rs.getBoolean("paymentStatus"));
				itemDetails.put(itemObject);
	        }
		}
		else {
			ps = con.prepareStatement("SELECT * FROM Invoice");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject itemObject = new JSONObject();
				itemObject.put("invoiceId", rs.getInt("invoiceId"));
				itemObject.put("userId", rs.getInt("userId"));
				itemObject.put("invoiceDate", rs.getDate("invoiceDate"));
				itemObject.put("custId", rs.getInt("custId"));
				itemObject.put("totalAmountWithTax", rs.getDouble("totalAmtWithTax"));
				itemObject.put("discount", rs.getDouble("discount"));
				itemObject.put("totalAmount", rs.getInt("totalAmount"));
				itemObject.put("paymentStatus", rs.getBoolean("paymentStatus"));
				itemDetails.put(itemObject);
	        }
		}
		con.close();
		return itemDetails;
	}
	
	
}
