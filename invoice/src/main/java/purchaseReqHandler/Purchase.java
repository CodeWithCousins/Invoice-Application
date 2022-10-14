package purchaseReqHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import itemReqHandler.ItemHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.SqlValidation;
import reqHandler.Requesthandler;
import supplierReqHandler.SupplierHandler;

/**
 * Servlet implementation class Purchase
 */
@WebServlet("/api/v0/purchase/*")
public class Purchase extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pOut = response.getWriter();
		JSONArray jsArrPurchaseDetails = new JSONArray();
		Requesthandler requesthandler = new Requesthandler();
		
		int isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials != 0)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/purchase", "");
			SqlValidation sqlValidation = new SqlValidation();
			int purchaseId = 0;
			PurchaseHandler purchaseHandler = new PurchaseHandler();

			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				purchaseId = Integer.parseInt(uri.substring(1));
				try {
					jsArrPurchaseDetails = purchaseHandler.GetPurchase(purchaseId);
					
				} catch (SQLException | JSONException e) {
					e.printStackTrace();
				}
			}
			else if(uri == "")
			{	
				ItemHandler itemHandler = new ItemHandler();
				try {
					jsArrPurchaseDetails = purchaseHandler.GetPurchase(purchaseId);
				} catch (SQLException | JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(jsArrPurchaseDetails.length() != 0)
		{
			try {
				jsonObject.put("status", "success");
				jsonObject.put("Purchase", jsArrPurchaseDetails);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		else
		{
			try {
				jsonObject.put("status", "error");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		pOut.println(jsonObject);
		pOut.close();
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		int isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		// Logic 
		if(isValidCredentials != 0 )
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/purchase", "");

			SqlValidation sqlValidation = new SqlValidation();

			if(uri != "")
				return;

			String bodyMsgline = null;
			String bodyMsg = "";

			BufferedReader reader = request.getReader();
			while ((bodyMsgline = reader.readLine()) != null) {
				bodyMsg +=bodyMsgline;
			}

			JSONArray arrBodyItems = null;

			try {

				JSONObject json = new JSONObject(bodyMsg);  
				arrBodyItems = json.getJSONArray("items");
				JSONArray arrBodyItemDetails = json.getJSONArray("details");

				String taxId = "";
				double discount =0;
				if(arrBodyItemDetails.getJSONObject(0).has("taxID"))
					taxId = arrBodyItemDetails.getJSONObject(0).getString("taxID");
				
				SupplierHandler supplierHandler = new SupplierHandler();
				int supplierId = supplierHandler.GetSupplierId(taxId);
				if(supplierId != 0 ) 
				{
					if(arrBodyItemDetails.getJSONObject(0).has("discount"))
						discount = arrBodyItemDetails.getJSONObject(0).getDouble("discount");

					
					ItemHandler itemHandler = new ItemHandler();
					PurchaseHandler purchaseHandler = new PurchaseHandler();
					int purchaseId = purchaseHandler.GetLatestPurchaseId()+1;
					
					for(int i=0;i<arrBodyItems.length();i++)
					{
						String itemName = "";
						int itemQuantity = 0;
						double itemPrice = 0, tax=0, sellingPrice=0;

						if(arrBodyItems.getJSONObject(i).has("itemName") && arrBodyItems.getJSONObject(i).has("itemQuantity") && arrBodyItems.getJSONObject(i).has("itemPrice") && arrBodyItems.getJSONObject(i).has("tax"))
						{
							itemName = arrBodyItems.getJSONObject(i).getString("itemName");
							itemQuantity = arrBodyItems.getJSONObject(i).getInt("itemQuantity");
							itemPrice = arrBodyItems.getJSONObject(i).getDouble("itemPrice");
							tax = arrBodyItems.getJSONObject(i).getDouble("tax");
							sellingPrice = itemPrice + 3;
							int updatedRows = itemHandler.UpdateStock(itemName, itemQuantity, itemPrice, tax);
							if(updatedRows == 0)
							{
								// insert Item;
								itemHandler.InsertItem(itemName, itemPrice, sellingPrice, tax, itemQuantity);
							}
							purchaseHandler.InsertPurchaseItem(purchaseId, itemName, itemQuantity, itemPrice, tax);
							purchaseHandler.InsertPurchase(purchaseId, taxId, supplierId, discount, isValidCredentials);
						}
					}
					jsonObject.put("status", "success");
				}

			}catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(jsonObject.length() == 0)
		{
			try {
				jsonObject.put("status", "error");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		out.println(jsonObject);
		out.close();
	}

}
