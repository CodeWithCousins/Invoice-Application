package security;

public class RandomKey {
	
	static String getAlphaNumericString(int n) 
	 { 
	  String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz"; 
	  String randomKey ="";
	  for (int i = 0; i < n; i++) { 
		  int index = (int)(AlphaNumericString.length() * Math.random()); 
		  randomKey += (AlphaNumericString.charAt(index));
	  } 
	  return randomKey; 
	 } 

}
