package userReqHandler;
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
import security.Base;

/**
 * Servlet implementation class User
 */
@WebServlet("/api/v0/user/*")
public class User extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pOut = response.getWriter();
		Requesthandler requesthandler = new Requesthandler();
		int isValidCredentials = requesthandler.Handler(request, response);
		JSONArray jsArrUserDetails = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		// Logic 
		if(isValidCredentials != 0)
		{
			String uri = request.getRequestURI();
			uri = uri.replace("/invoice/api/v0/user", "");
			int organizationId = Integer.parseInt(request.getParameter("organization_id"));
			SqlValidation sqlValidation = new SqlValidation();
			int userId = 0;
			UserHandler userHandler = new UserHandler();
			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				userId = Integer.parseInt(uri.substring(1));
				CustomerHandler customerHandler = new CustomerHandler();

				try {
					jsArrUserDetails = userHandler.GetUser(userId, organizationId);
				} catch (SQLException | JSONException e) {
					e.printStackTrace();
				}
			}
			else if(uri == "")
			{	
				try {
					jsArrUserDetails = userHandler.GetUser(userId, organizationId);
				} catch (SQLException | JSONException e) {
					e.printStackTrace();
				}
			}
		}
		if(jsArrUserDetails.length() != 0)
		{
			try {
				jsonObject.put("status", "success");
				jsonObject.put("users", jsArrUserDetails);
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
			uri = uri.replace("/invoice/api/v0/user", "");

			SqlValidation sqlValidation = new SqlValidation();

			if(uri != "")
				return;
			
			String bodyMsgline = null;
			String bodyMsg = "";

			BufferedReader reader = request.getReader();
			while ((bodyMsgline = reader.readLine()) != null) {
				bodyMsg +=bodyMsgline;
			}
			
			Base base = new Base();

			JSONArray arrayOfBody = null;

			try {
				JSONObject json = new JSONObject(bodyMsg); 
				arrayOfBody = json.getJSONArray("users");
				for(int i=0;i<arrayOfBody.length();i++)
				{
					String userName = "", email="", city ="", password = "", credents="";
					int organizationId = 0;
					long phnNo = 0;

					if(arrayOfBody.getJSONObject(i).has("userName")) 
					{
						userName = arrayOfBody.getJSONObject(i).getString("userName");

						if(arrayOfBody.getJSONObject(i).has("organizationId"))
							organizationId = arrayOfBody.getJSONObject(i).getInt("organizationId");
						
						if(arrayOfBody.getJSONObject(i).has("email")) 
							email = arrayOfBody.getJSONObject(i).getString("email");
							
						if(arrayOfBody.getJSONObject(i).has("city")) 
							city = arrayOfBody.getJSONObject(i).getString("city");
						
						if(arrayOfBody.getJSONObject(i).has("phoneNumber"))
							phnNo = arrayOfBody.getJSONObject(i).getLong("phoneNumber");
						
						if(arrayOfBody.getJSONObject(i).has("password")) 
							password = arrayOfBody.getJSONObject(i).getString("password");
						
						if(sqlValidation.IsValidString(userName) && sqlValidation.IsValidString(password) && sqlValidation.IsValidString(email) && sqlValidation.IsValidString(city)) {
							credents = base.EncodeBase(email + ":" + password);
							UserHandler userHandler = new UserHandler();
							userHandler.InsertUser(userName, email, organizationId, city, phnNo, credents);
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
				// TODO Auto-generated catch block
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
			uri = uri.replace("/invoice/api/v0/user", "");
			int organizationId = Integer.parseInt(request.getParameter("organization_id"));
			SqlValidation sqlValidation = new SqlValidation();

			int userId = 0;

			if(uri != "" && sqlValidation.IsNumeric(uri.substring(1)))
			{
				userId = Integer.parseInt(uri.substring(1));
				UserHandler userHandler = new UserHandler();

				try {
					res = userHandler.DeleteUser(userId, organizationId);
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
				jsonObject.put("status", "error");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		pOut.println(jsonObject);
		pOut.close();
	}

}
