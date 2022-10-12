package auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.SqlValidation;
import security.Base;
import security.KeySetter;

@WebServlet("/jwt/v0/token")
public class Token extends HttpServlet {
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		String loginCredentials = request.getHeader("Authorization"); // From Authorization Header
		
		SqlValidation sqlValidation = new SqlValidation();
		boolean sqlValid = sqlValidation.IsValidString(loginCredentials); // Checks Input String
		JSONObject jsonObject = new JSONObject();
		if(sqlValid)
		{
			Authentication authentication = new Authentication();
			int userId = 0;
			try {
				userId = authentication.IsValidUser(loginCredentials);
				if(userId != 0) {
					
					Base base64 = new Base();
					String jwtToken = base64.Encode(userId);
					
					KeySetter keySetter = new KeySetter();
					keySetter.SetEncryptionTokenInDb(jwtToken, userId);
					
					jsonObject.put("status", "success");
					jsonObject.put("Token", jwtToken);
					out.println(jsonObject);
				}
			} catch (SQLException | JSONException e) {
				
				try {
					jsonObject.put("status", "error");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				out.println(jsonObject);
				e.printStackTrace();
			}
		}
		out.close();
	}
}
