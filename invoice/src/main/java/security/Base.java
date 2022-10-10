package security;

import java.util.Base64;

public class Base {

	public String Encode(int id) {
		
		String header ="{\n\"alg\":\"AES\",\n\"typ\":\"JWT\"\n}";
		String headerBase64format = Base64.getEncoder().encodeToString(header.getBytes());
		
		DateGenerator dateGenerator = new DateGenerator();
		String expiryDateTime = dateGenerator.ExpDateTime();
		
		System.out.println(expiryDateTime);
		
		String payload = "{\n\"expired at\":\""+expiryDateTime+"\"\n}";
		String payloadBase64format = Base64.getEncoder().encodeToString(payload.getBytes());
		
		AES aes = new AES();
		String signature = aes.Encrypt(id);
		
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
}
