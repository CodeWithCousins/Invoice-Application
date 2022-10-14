package invoiceReqHandler;

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

@WebServlet("/api/v0/invoice/*")
public class Invoice extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pOut = response.getWriter();
		JSONArray jsArrInvoiceDetails = new JSONArray();
		Requesthandler requesthandler = new Requesthandler();

		int isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials != 0)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/invoice", "");
			SqlValidation sqlValidation = new SqlValidation();
			int invoiceId = 0;
			InvoiceHandler invoiceHandler = new InvoiceHandler();

			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				invoiceId = Integer.parseInt(uri.substring(1));
				try {
					jsonObject.put("status", "success");
					jsArrInvoiceDetails = invoiceHandler.GetInvoice(invoiceId);
				} catch (SQLException | JSONException e) {
					e.printStackTrace();
				}
			}
			else if(uri == "")
			{	
				ItemHandler itemHandler = new ItemHandler();
				try {
					jsArrInvoiceDetails = invoiceHandler.GetInvoice(invoiceId);
				} catch (SQLException | JSONException e) {
					e.printStackTrace();
				}
			}
		}
		if(jsArrInvoiceDetails.length() != 0)
		{
			try {
				jsonObject.put("status", "success");
				jsonObject.put("invoices", jsArrInvoiceDetails);
			} catch (JSONException e) {
				e.printStackTrace();
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
		}
		pOut.println(jsonObject);
		pOut.close();

	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter pOut = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		int isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials != 0)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/invoice", "");

			if(uri != "")
				return;

			String bodyMsgline = null;
			String bodyMsg = "";

			BufferedReader reader = request.getReader();
			while ((bodyMsgline = reader.readLine()) != null) {
				bodyMsg +=bodyMsgline;
			}

			JSONArray arrBodyItems = null;
			int custId = 0;
			double discount=0;
			try {
				JSONObject json = new JSONObject(bodyMsg);  
				arrBodyItems = json.getJSONArray("items");
				JSONArray arrBodyItemDetails = json.getJSONArray("details");

				if(arrBodyItemDetails.getJSONObject(0).has("custId"))
				{
					custId = arrBodyItemDetails.getJSONObject(0).getInt("custId");
				} 
				if(arrBodyItemDetails.getJSONObject(0).has("discount"))
				{
					discount = arrBodyItemDetails.getJSONObject(0).getDouble("discount");
				} 

				InvoiceHandler invoiceHandler = new InvoiceHandler();
				int invoiceId = invoiceHandler.GetLatestInvoiceId()+1;
				boolean canInvoiceBeCreated = true;
				ItemHandler itemHandler = new ItemHandler();
				double totalCostPrice= 0;
				for(int i=0;i<arrBodyItems.length();i++)
				{
					int itemId =0, quantity =0;
					if(arrBodyItems.getJSONObject(i).has("itemId") && arrBodyItems.getJSONObject(i).has("quantity")) 
					{
						itemId = arrBodyItems.getJSONObject(i).getInt("itemId");
						quantity = arrBodyItems.getJSONObject(i).getInt("quantity");
						if(!itemHandler.IsItemAvailable(itemId, quantity))
						{
							canInvoiceBeCreated = false;
							break;
						}
					}
				}

				if(canInvoiceBeCreated) {
					for(int i=0;i<arrBodyItems.length();i++)
					{
						int itemId =0, quantity =0;

						if(arrBodyItems.getJSONObject(i).has("itemId") && arrBodyItems.getJSONObject(i).has("quantity")) 
						{
							itemId = arrBodyItems.getJSONObject(i).getInt("itemId");
							quantity = arrBodyItems.getJSONObject(i).getInt("quantity");

							itemHandler.ReduceItemStock(itemId, quantity);
							invoiceHandler.InsertInvoicetItem(invoiceId, itemId, quantity);
							totalCostPrice += (itemHandler.GetCostPrice(itemId) * quantity);
						}
					}
					invoiceHandler.InsertInvoice(invoiceId, custId, discount, totalCostPrice, isValidCredentials);
					jsonObject.put("status", "success");
				}
				else
				{
					jsonObject.put("status", "error");
				}
			}catch (JSONException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		pOut.println(jsonObject);
		pOut.close();
	}

}
