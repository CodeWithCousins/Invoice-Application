package supplierReqHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import customerReqHandler.CustomerHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.SqlValidation;
import reqHandler.Requesthandler;


@WebServlet("/api/v0/supplier/*")
public class Supplier extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pOut = response.getWriter();
		JSONArray jsArrSupplierDetails = new JSONArray();
		Requesthandler requesthandler = new Requesthandler();
		int isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		// Logic 
		if(isValidCredentials != 0)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/supplier", "");
			SqlValidation sqlValidation = new SqlValidation();
			int id = 0;
			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				id = Integer.parseInt(uri.substring(1));
				SupplierHandler supplierHandler = new SupplierHandler();
				try {
					jsArrSupplierDetails = supplierHandler.GetSupplier(id);
				} catch (SQLException | JSONException e) {
					e.printStackTrace();
				}
			}
			else if(uri == "")
			{	
				SupplierHandler supplierHandler = new SupplierHandler();
				try {
					jsArrSupplierDetails = supplierHandler.GetSupplier(id);
				} catch (SQLException | JSONException e) {
					e.printStackTrace();
				}
			}
		}
		if(jsArrSupplierDetails.length() != 0)
		{
			try {
				jsonObject.put("status", "success");
				jsonObject.put("supplier", jsArrSupplierDetails);
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
		if(isValidCredentials != 0)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/supplier", "");

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
				JSONObject json = new JSONObject(bodyMsg);  
				arrayOfBody = json.getJSONArray("supplier");
				for(int i=0;i<arrayOfBody.length();i++)
				{
					String name = "", address = "", taxId="";
					long phnNo = 0;

					if(arrayOfBody.getJSONObject(i).has("supplier_name")) {
						name = arrayOfBody.getJSONObject(i).getString("supplier_name");

						if(arrayOfBody.getJSONObject(i).has("supplier_phone"))
							phnNo = arrayOfBody.getJSONObject(i).getLong("supplier_phone");
						
						if(arrayOfBody.getJSONObject(i).has("taxId"))
							taxId = arrayOfBody.getJSONObject(i).getString("taxId");

						if(arrayOfBody.getJSONObject(i).has("supplier_address"))
							address = arrayOfBody.getJSONObject(i).getString("supplier_address");
						
						if(sqlValidation.IsValidString(name) && sqlValidation.IsValidString(address)) {
							SupplierHandler supplierHandler = new SupplierHandler();
							supplierHandler.InsertSupplier(name, taxId, phnNo, address);
						}
					}
				}
				jsonObject.put("status", "success");
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
		}
		else if(jsonObject.length() == 0)
		{
			try {
				jsonObject.put("status", "error");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			out.println(jsonObject);
		}
		out.println(jsonObject);
		out.close();
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		Requesthandler requesthandler = new Requesthandler();
		int isValidCredentials = requesthandler.Handler(request, response);
		JSONObject jsonObject = new JSONObject();
		if(isValidCredentials != 0)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/supplier", "");

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
				JSONObject json = new JSONObject(bodyMsg);  
				arrayOfBody = json.getJSONArray("supplier");
				for(int i=0;i<arrayOfBody.length();i++)
				{
					String name = "", address = "", taxId ="";
					long phnNo = 0;
					int id =0;

					if(arrayOfBody.getJSONObject(i).has("supplier_id"))
					{
						id = arrayOfBody.getJSONObject(i).getInt("supplier_id");

						if(arrayOfBody.getJSONObject(i).has("supplier_name")) 
							name = arrayOfBody.getJSONObject(i).getString("supplier_name");
						
						if(arrayOfBody.getJSONObject(i).has("taxId"))
							taxId = arrayOfBody.getJSONObject(i).getString("taxId");

						if(arrayOfBody.getJSONObject(i).has("supplier_phone"))
							phnNo = arrayOfBody.getJSONObject(i).getLong("supplier_phone");

						if(arrayOfBody.getJSONObject(i).has("supplier_address"))
							address = arrayOfBody.getJSONObject(i).getString("supplier_address");

						if(sqlValidation.IsValidString(name) && sqlValidation.IsValidString(address)) {
							SupplierHandler supplierHandler = new SupplierHandler();
							supplierHandler.UpdateSupplier(id, taxId, name, phnNo, address);
							jsonObject.put("status", "success");
						}
					}
				}
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
		}
		else if(jsonObject.length() == 0)
		{
			try {
				jsonObject.put("status", "error");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		out.println(jsonObject);
		out.close();
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter pOut = response.getWriter();
		Requesthandler requesthandler = new Requesthandler();
		int isValidCredentials = requesthandler.Handler(request, response);
		int res = 0;
		JSONObject jsonObject = new JSONObject();
		// Logic 
		if(isValidCredentials != 0)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/supplier", "");

			SqlValidation sqlValidation = new SqlValidation();

			int id = 0;

			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				id = Integer.parseInt(uri.substring(1));
				SupplierHandler supplierHandler = new SupplierHandler();
				try {
					res = supplierHandler.DeleteSupplier(id);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(res != 0)
		{
			try {
				jsonObject.put("status", "success");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				jsonObject.put("status", "erroe");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		pOut.println(jsonObject);
		pOut.close();
	}

}
