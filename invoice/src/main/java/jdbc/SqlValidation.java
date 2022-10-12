package jdbc;

public class SqlValidation {
	
	public boolean IsValidString(String strToBeValidated)
	{
		String lowerCaseStr = strToBeValidated.toLowerCase();
		if(lowerCaseStr.contains("drop table ") ||lowerCaseStr.contains(" * ") || lowerCaseStr.contains(";") || lowerCaseStr.contains(" = ") || lowerCaseStr.contains("update table ") || lowerCaseStr.contains("alter table ") || lowerCaseStr.contains("select "))
			return false;
		
		return true;
	}
	
	public boolean IsNumeric(String strToBeValidated)
	{
		try {  
			Double.parseDouble(strToBeValidated);  
			return true;
		}catch(NumberFormatException e){  
			return false;  
		}  
	}
}
