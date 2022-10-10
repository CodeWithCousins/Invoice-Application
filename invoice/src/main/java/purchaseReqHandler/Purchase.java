package purchaseReqHandler;

import itemReqHandler.ItemHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.SqlValidation;
import reqHandler.Requesthandler;

/**
 * Servlet implementation class Purchase
 */
@WebServlet("/api/v0/purchase")
public class Purchase extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		boolean isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		// Logic 
		if(isValidCredentials)
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

			JSONArray arrayOfBody = null;

			try {
				
				arrayOfBody = new JSONArray(bodyMsg);
				for(int i=0;i<arrayOfBody.length();i++)
				{
					String itemName = "";
					int itemQuantity = 0;
					double itemPrice = 0, tax=0, sellingPrice=0;
					
					if(arrayOfBody.getJSONObject(i).has("itemName") && arrayOfBody.getJSONObject(i).has("itemQuantity") && arrayOfBody.getJSONObject(i).has("itemPrice") && arrayOfBody.getJSONObject(i).has("tax"))
					{
						itemName = arrayOfBody.getJSONObject(i).getString("itemName");
						itemQuantity = arrayOfBody.getJSONObject(i).getInt("itemQuantity");
						itemPrice = arrayOfBody.getJSONObject(i).getDouble("itemPrice");
						tax = arrayOfBody.getJSONObject(i).getDouble("tax");
						sellingPrice = itemPrice + 1;
						
						ItemHandler itemHandler = new ItemHandler();
						int updatedRows = itemHandler.UpdateStock(itemName, itemQuantity, itemPrice, tax);
					
						if(updatedRows == 0)
						{
							// insert Item;
							itemHandler.InsertItem(itemName, itemPrice, sellingPrice, tax, itemQuantity);
						}
					}
				}
				jsonObject.put("status", "success");
				out.println(jsonObject);
				
			}catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
