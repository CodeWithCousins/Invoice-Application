package security;

import java.util.Base64;

public class Base {

	public String Encode(int id) {
		
		String header ="{\n\"alg\":\"sha\",\n\"typ\":\"JWT\"\n}";
		String headerBase64format = Base64.getEncoder().encodeToString(header.getBytes());
		
		DateGenerator dateGenerator = new DateGenerator();
		String expiryDateTime = dateGenerator.ExpDateTime();
		
		System.out.println(expiryDateTime);
		
		String payload = "{\n\"id\" : "+id+", \n\"expired at\":\""+expiryDateTime+"\"\n}";
		String payloadBase64format = Base64.getEncoder().encodeToString(payload.getBytes());
		
		Sha sha = new Sha();
		String signature = sha.EncryptSalt();
		
		String BasicBase64format = headerBase64format +"."+ payloadBase64format+"."+signature;
		System.out.println(BasicBase64format);
		return BasicBase64format;
	}

	public String Decode(String toDecode)
	{

		byte[] actualByte = Base64.getDecoder().decode(toDecode);
		String actualString = new String(actualByte);

		return actualString;
	}
	public String EncodeBase(String credents) {
		
		String encUserPass = Base64.getEncoder().encodeToString(credents.getBytes());
		
		return encUserPass;
	}
}
