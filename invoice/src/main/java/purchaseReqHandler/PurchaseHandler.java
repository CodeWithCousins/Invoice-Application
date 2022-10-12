package purchaseReqHandler;
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

public class PurchaseHandler {
	
	public void InsertPurchaseItem(int purchaseId, String itemName, int itemQuantity, double itemPrice, double tax) throws SQLException
	{
		PurchaseHandler purchaseHandler = new PurchaseHandler();
		ItemHandler ItemHandler = new ItemHandler();
		
		int itemId = ItemHandler.GetItemId(itemName);
		double totalAmountWithoutTax = itemPrice * itemQuantity;
		double totalAmountWithTax = totalAmountWithoutTax + ((itemPrice * (tax/100.0)) * itemQuantity);
		
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("Insert into PurchaseItem values(?,?,?,?,?)");
		ps.setInt(1, purchaseId);
		ps.setInt(2, itemId);
		ps.setInt(3, itemQuantity);
		ps.setDouble(4, totalAmountWithoutTax);
		ps.setDouble(5, totalAmountWithTax);
		ps.executeUpdate();
		con.close();
	}
	public JSONArray GetPurchase(int purchaseId) throws JSONException, SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps ;
		JSONArray purchaseDetails = new JSONArray();
		if(purchaseId != 0)
		{
			ps = con.prepareStatement("select Purchase.purchaseId, Purchase.userId, Purchase.purchaseDate, Purchase.supplierId, Purchase.discount, Purchase.totalAmtWithTax, Purchase.totalAmount, Purchase.paymentStatus, \n"
					+ " PurchaseItem.itemId, PurchaseItem.quantity, PurchaseItem.itemTotalWithoutTax, PurchaseItem.itemTotalWithTax from Purchase inner join PurchaseItem \n"
					+ "on Purchase.purchaseId = PurchaseItem.purchaseId where Purchase.purchaseId = ?");
			ps.setInt(1, purchaseId);
			ResultSet rs = ps.executeQuery();
			
			
			while (rs.next()) {
				JSONObject itemObject = new JSONObject();
				itemObject.put("purchaseId", rs.getInt("purchaseId"));
				itemObject.put("userId", rs.getInt("userId"));
				itemObject.put("purchaseDate", rs.getDate("purchaseDate"));
				itemObject.put("supplierId", rs.getInt("supplierId"));
				itemObject.put("discount", rs.getDouble("discount"));
				itemObject.put("totalAmtWithTax", rs.getDouble("totalAmtWithTax"));
				itemObject.put("totalAmount", rs.getInt("totalAmount"));
				itemObject.put("itemId", rs.getInt("itemId"));
				itemObject.put("quantity", rs.getInt("quantity"));
				itemObject.put("itemTotalWithoutTax", rs.getDouble("itemTotalWithoutTax"));
				itemObject.put("paymentStatus", rs.getBoolean("paymentStatus"));
				purchaseDetails.put(itemObject);
	        }
		}
		else
		{
			ps = con.prepareStatement("SELECT * FROM Purchase");
			ResultSet rs = ps.executeQuery();
			
			
			while (rs.next()) {
				JSONObject itemObject = new JSONObject();
				itemObject.put("purchaseId", rs.getInt("purchaseId"));
				itemObject.put("userId", rs.getInt("userId"));
				itemObject.put("purchaseDate", rs.getDate("purchaseDate"));
				itemObject.put("supplierId", rs.getInt("supplierId"));
				itemObject.put("discount", rs.getDouble("discount"));
				itemObject.put("totalAmount", rs.getDouble("totalAmount"));
				itemObject.put("paymentStatus", rs.getBoolean("paymentStatus"));
				purchaseDetails.put(itemObject);
	        }
		}
		
		
		con.close();
		return purchaseDetails;
	}
	public void InsertPurchase(int purchaseId, String taxId,int supplierId, double discount, int userId) throws SQLException
	{
		PurchaseHandler purchaseHandler = new PurchaseHandler();
		DateGenerator dateGenerator = new DateGenerator();
		Date date = dateGenerator.CurrentDateTime();
		double totalAmtWithoutTax = purchaseHandler.GetTotalAmountWithoutTax(purchaseId);
		double totalAmtWithTax = purchaseHandler.GetTotalAmountWithTax(purchaseId);
		double discountAmount =  (totalAmtWithoutTax * (discount /100.0));
		double totalAmount = totalAmtWithTax - discountAmount;
		int total = (int)Math.round(totalAmount);
		
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("insert into Purchase(purchaseDate, supplierId, discount, totalAmtWithoutTax, totalAmtWithTax, totalAmount, userId) values(?,?,?,?,?,?,?)");
		ps.setDate(1, date);
		ps.setInt(2, supplierId);
		ps.setDouble(3, discount);
		ps.setDouble(4, totalAmtWithoutTax);
		ps.setDouble(5, totalAmtWithTax);
		ps.setInt(6, total);
		ps.setInt(7, userId);
		
		ps.executeUpdate();
		con.close();
	}
	
	public int GetLatestPurchaseId() throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("SELECT * from Purchase order by purchaseId desc limit 1");
        ResultSet rs = ps.executeQuery();
        int purchaseId=0;
        
        while(rs.next()){
        	purchaseId = rs.getInt("purchaseId");        	
         }
		
        con.close();
		return purchaseId;
	}
	
	public double GetTotalAmountWithoutTax(int purchaseId) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("SELECT * from PurchaseItem WHERE purchaseId = ?");
		ps.setInt(1, purchaseId);
        ResultSet rs = ps.executeQuery();
        double itemTotalWithoutTax = 0;
        
        while(rs.next()){
        	itemTotalWithoutTax = rs.getInt("itemTotalWithoutTax");        	
         }
        
        con.close();
		return itemTotalWithoutTax;
	}
	
	public double GetTotalAmountWithTax(int purchaseId) throws SQLException
	{
		Connection con = SqlConnector.ConnectDb();
		PreparedStatement ps = con.prepareStatement("SELECT * from PurchaseItem WHERE purchaseId = ?");
		ps.setInt(1, purchaseId);
        ResultSet rs = ps.executeQuery();
        double itemTotalWithTax = 0;
        
        while(rs.next()){
        	itemTotalWithTax = rs.getInt("itemTotalWithTax");        	
         }
        
        con.close();
		return itemTotalWithTax;
	}

}
