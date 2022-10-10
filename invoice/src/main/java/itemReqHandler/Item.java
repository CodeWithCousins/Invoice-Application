package itemReqHandler;

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

@WebServlet("/api/v0/item/*")
public class Item extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter pOut = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		boolean isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials)
		{
			
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/item", "");
			SqlValidation sqlValidation = new SqlValidation();
			int itemId = 0;

			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				itemId = Integer.parseInt(uri.substring(1));

				ItemHandler itemHandler = new ItemHandler();

				try {
					jsonObject.put("status", "success");
					jsonObject.put("Items", itemHandler.GetItem(itemId));
					pOut.println(jsonObject);
				} catch (SQLException | JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(uri == "")
			{	
				ItemHandler itemHandler = new ItemHandler();

				try {
					jsonObject.put("status", "success");
					jsonObject.put("Items", itemHandler.GetItem(itemId));
					pOut.println(jsonObject);
				} catch (SQLException | JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			try {
				jsonObject.put("status", "error");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pOut.println(jsonObject);
		}
		pOut.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		boolean isValidCredentials = requesthandler.Handler(request, response);
		// Logic 
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/item", "");

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
					String itemName ="";
					double costPrice =0, sellingPrice =0, tax =0;
					int stock = 0;
					SqlValidation sqlValidation = new SqlValidation();

					if(arrayOfBody.getJSONObject(i).has("itemName")) {
						itemName = arrayOfBody.getJSONObject(i).getString("itemName");

						if(arrayOfBody.getJSONObject(i).has("costPrice"))
							costPrice = arrayOfBody.getJSONObject(i).getDouble("costPrice");

						if(arrayOfBody.getJSONObject(i).has("sellingPrice"))
							sellingPrice = arrayOfBody.getJSONObject(i).getDouble("sellingPrice");

						if(arrayOfBody.getJSONObject(i).has("tax"))
							tax = arrayOfBody.getJSONObject(i).getDouble("tax");

						if(arrayOfBody.getJSONObject(i).has("stock"))
							stock = arrayOfBody.getJSONObject(i).getInt("stock");

						if(sqlValidation.IsValidString(itemName)) {
							ItemHandler itemHandler = new ItemHandler();
							itemHandler.InsertItem(itemName, costPrice, sellingPrice, tax, stock);
							jsonObject.put("status", "success");
							out.println(jsonObject);
						}
					}
					else
					{
//						out.println("Provide item name ");
					}

				}


			}catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		out.close();
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		boolean isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/item", "");
			
			if(uri != "")
				return;

			String bodyMsgline = null, bodyMsg = "";

			BufferedReader reader = request.getReader();
			while ((bodyMsgline = reader.readLine()) != null) {
				bodyMsg +=bodyMsgline;
			}

			JSONArray arrayOfBody = null;

			try {
				arrayOfBody = new JSONArray(bodyMsg);
				for(int i=0;i<arrayOfBody.length();i++)
				{
					String itemName ="";
					double costPrice =0, sellingPrice =0, tax =0;
					int stock = 0, itemId = 0;
					SqlValidation sqlValidation = new SqlValidation();

					if(arrayOfBody.getJSONObject(i).has("itemId"))
					{
						itemId = arrayOfBody.getJSONObject(i).getInt("itemId");

						if(arrayOfBody.getJSONObject(i).has("itemName")) 
							itemName = arrayOfBody.getJSONObject(i).getString("itemName");

						if(arrayOfBody.getJSONObject(i).has("costPrice"))
							costPrice = arrayOfBody.getJSONObject(i).getDouble("costPrice");

						if(arrayOfBody.getJSONObject(i).has("sellingPrice"))
							sellingPrice = arrayOfBody.getJSONObject(i).getDouble("sellingPrice");

						if(arrayOfBody.getJSONObject(i).has("tax"))
							tax = arrayOfBody.getJSONObject(i).getDouble("tax");

						if(arrayOfBody.getJSONObject(i).has("stock"))
							stock = arrayOfBody.getJSONObject(i).getInt("stock");


						if(sqlValidation.IsValidString(itemName)) {
							ItemHandler itemHandler = new ItemHandler();
							itemHandler.UpdateItem(itemId, itemName, costPrice, sellingPrice, tax, stock);
							
							jsonObject.put("status", "success");
							out.println(jsonObject);
						}

					}
					else
					{
						jsonObject.put("status", "error");
						out.println(jsonObject);
					}
				}
			} catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		out.close();
	}
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter pOut = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		boolean isValidCredentials = requesthandler.Handler(request, response);
		// Logic 
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials)
		{
			SqlValidation sqlValidation = new SqlValidation();
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/item", "");

			int itemId = 0;

			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				itemId = Integer.parseInt(uri.substring(1));
				ItemHandler itemHandler = new ItemHandler();

				try {
					itemHandler.DeleteItem(itemId);
					
					jsonObject.put("status", "success");
					pOut.println(jsonObject);
				} catch (SQLException | JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		pOut.close();

	}
}

