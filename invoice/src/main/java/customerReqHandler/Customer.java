package customerReqHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.SqlValidation;
import reqHandler.Requesthandler;


@WebServlet("/api/v0/customer/*")
public class Customer extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		PrintWriter pOut = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		boolean isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		// Logic 
		if(isValidCredentials)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/customer", "");

			SqlValidation sqlValidation = new SqlValidation();

			int customerId = 0;

			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				customerId = Integer.parseInt(uri.substring(1));

				CustomerHandler customerHandler = new CustomerHandler();

				try {
					jsonObject.put("status", "success");
					jsonObject.put("customer", customerHandler.GetCustomer(customerId));
					pOut.println(jsonObject);
				} catch (SQLException | JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(uri == "")
			{	
				CustomerHandler customerHandler = new CustomerHandler();

				try {
					jsonObject.put("status", "success");
					jsonObject.put("customer", customerHandler.GetCustomer(customerId));
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
		JSONObject jsonObject = new JSONObject();
		// Logic 
		if(isValidCredentials)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/customer", "");

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
					String custName = "", custAddress = "";
					long custPhnNo = 0;

					if(arrayOfBody.getJSONObject(i).has("customer_name")) {
						custName = arrayOfBody.getJSONObject(i).getString("customer_name");

						if(arrayOfBody.getJSONObject(i).has("customer_phone"))
							custPhnNo = arrayOfBody.getJSONObject(i).getLong("customer_phone");

						if(arrayOfBody.getJSONObject(i).has("customer_address"))
							custAddress = arrayOfBody.getJSONObject(i).getString("customer_address");
						if(sqlValidation.IsValidString(custName) && sqlValidation.IsValidString(custAddress)) {
							CustomerHandler customerHandler = new CustomerHandler();
							customerHandler.InsertCustomer(custName, custPhnNo, custAddress);
						}
					}
					else
					{
						out.println("Provide name ");
					}
				}
				jsonObject.put("status", "success");
				out.println(jsonObject);
				
			} catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
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
			out.println(jsonObject);
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
			uri = uri.replace("/invoice/api/v0/customer", "");

			SqlValidation sqlValidation = new SqlValidation();

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
					String custName = "", custAddress = "";
					long custPhnNo = 0;
					int custId =0;

					if(arrayOfBody.getJSONObject(i).has("custId"))
					{
						custId = arrayOfBody.getJSONObject(i).getInt("custId");

						if(arrayOfBody.getJSONObject(i).has("customer_name")) 
							custName = arrayOfBody.getJSONObject(i).getString("customer_name");

						if(arrayOfBody.getJSONObject(i).has("customer_phone"))
							custPhnNo = arrayOfBody.getJSONObject(i).getLong("customer_phone");

						if(arrayOfBody.getJSONObject(i).has("customer_address"))
							custAddress = arrayOfBody.getJSONObject(i).getString("customer_address");

						if(sqlValidation.IsValidString(custName) && sqlValidation.IsValidString(custAddress)) {
							CustomerHandler customerHandler = new CustomerHandler();
							customerHandler.UpdateCustomer(custId, custName, custPhnNo, custAddress);
							jsonObject.put("status", "success");
							out.println(jsonObject);
						}

					}
					else
					{
						
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
		
		JSONObject jsonObject = new JSONObject();
		// Logic 
		if(isValidCredentials)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/customer", "");

			SqlValidation sqlValidation = new SqlValidation();

			int customerId = 0;

			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				customerId = Integer.parseInt(uri.substring(1));
				CustomerHandler customerHandler = new CustomerHandler();

				try {
					customerHandler.DeleteCustomer(customerId);
					
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
