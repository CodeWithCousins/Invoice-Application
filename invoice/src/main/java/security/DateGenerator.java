package security;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateGenerator {
	
	public java.sql.Date CurrentDateTime()
	{
		long millis=System.currentTimeMillis(); 
		
        java.sql.Date date=new java.sql.Date(millis);  
        System.out.println(date);  

		return date;
	}
	
	public String ExpDateTime()
	{
		Date currentDate = new Date(System.currentTimeMillis() + 3600 * 1000);
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    
	    String exptime = formatter.format(currentDate);
		
		return exptime;
	}
	
	public boolean  TokenExpiryValidator(String expTime) throws ParseException
	{
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = formatter.parse(expTime);
		Date date1 = new Date();
		
		if(date.compareTo(date1) > 0)//Date 1 occurs before Date 2
		{
			return true;
		}
		
		return false;
	}

}
