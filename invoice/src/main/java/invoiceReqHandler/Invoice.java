package invoiceReqHandler;

import itemReqHandler.ItemHandler;
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

		Requesthandler requesthandler = new Requesthandler();
		
		boolean isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials)
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
					jsonObject.put("invoice",invoiceHandler.GetInvoice(invoiceId));
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
					jsonObject.put("invoice",invoiceHandler.GetInvoice(invoiceId));
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
		
		PrintWriter pOut = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		boolean isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials)
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

			JSONArray arrayOfBody = null;
			int custId = 0;
			double discount=0;
			System.out.println("hi");
			try {
				arrayOfBody = new JSONArray(bodyMsg);
				
				if(arrayOfBody.getJSONObject(arrayOfBody.length()-1).has("custId"))
				{
					custId = arrayOfBody.getJSONObject(arrayOfBody.length()-1).getInt("custId");
				} 
				if(arrayOfBody.getJSONObject(arrayOfBody.length()-1).has("discount"))
				{
					discount = arrayOfBody.getJSONObject(arrayOfBody.length()-1).getDouble("discount");
				} 
				
				InvoiceHandler invoiceHandler = new InvoiceHandler();
				int invoiceId = invoiceHandler.GetLatestInvoiceId()+1;
				boolean canInvoiceBeCreated = false;
				ItemHandler itemHandler = new ItemHandler();
				double totalCostPrice= 0;
				for(int i=0;i<arrayOfBody.length()-1;i++)
				{
					int itemId =0, quantity =0;
					
					

					if(arrayOfBody.getJSONObject(i).has("itemId") && arrayOfBody.getJSONObject(i).has("quantity")) 
					{
						itemId = arrayOfBody.getJSONObject(i).getInt("itemId");
						quantity = arrayOfBody.getJSONObject(i).getInt("quantity");
						
						if(itemHandler.IsItemAvailable(itemId, quantity))
						{
							canInvoiceBeCreated = true;
							System.out.println("hiii");
							itemHandler.ReduceItemStock(itemId, quantity);
							invoiceHandler.InsertInvoicetItem(invoiceId, itemId, quantity);
							totalCostPrice += (itemHandler.GetCostPrice(itemId) * quantity);
						}
						
					}
					else
					{
//						out.println("Provide item id ");
					}

				}
				if(canInvoiceBeCreated)
				{
					invoiceHandler.InsertInvoice(invoiceId, custId, discount, totalCostPrice);
					
					jsonObject.put("status", "success");
					pOut.println(jsonObject);
				}
				else
				{
					jsonObject.put("status", "error");
					pOut.println(jsonObject);
				}
			
		
			}catch (JSONException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		pOut.close();
	}

}
