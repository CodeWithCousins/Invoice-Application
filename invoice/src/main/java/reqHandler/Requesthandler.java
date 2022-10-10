package reqHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.json.JSONException;

import auth.Authentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdbc.SqlValidation;

public class Requesthandler {
	
	public boolean Handler(HttpServletRequest request, HttpServletResponse response) throws IOException
	{

		if(request.getParameter("organization_id") != null)
		{
			int organizationId = Integer.parseInt(request.getParameter("organization_id")); // Organization_id from url

			if(request.getHeader("Authorization") != null)
			{
				String token = request.getHeader("Authorization"); // Jwt token from Header

				SqlValidation sqlValidation = new SqlValidation();

				if(sqlValidation.IsValidString(token))
				{
					Authentication authentication = new Authentication();
					boolean isValidCredentials = false;
					try {
						isValidCredentials = authentication.IsValidCredentials(token, organizationId);
						if(isValidCredentials)
						{
							return true;
						}
					} catch (SQLException | ParseException | JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else
			{
				System.out.println("Provide Authorization Token");
			}
		}
		else
		{
			System.out.println("Provide Organization Id");
		}
		return false;
					
	}

}
