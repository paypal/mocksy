package com.mocksysample;

import java.util.Properties;

public class MyUtil {
				
	public static Properties getHeaders(){
		
		Properties headers = new Properties();

		headers.put("X-PAYPAL-SECURITY-USERID",
					"ashsar_1233867711_biz_api1.yahoo.com");
		
		headers.put("X-PAYPAL-SECURITY-PASSWORD", "1233867722");
		
		headers.put("X-PAYPAL-SECURITY-SIGNATURE",
					"AhoWT5xdNuDRaHutY9.IsMFBBvRtAXmOyTsd-WC-BiEzYjbgceQTkQX1");
		
		headers.put("X-PAYPAL-APPLICATION-ID", "APP-80W284485P519543T");

		headers.put("X-PAYPAL-SERVICE-VERSION", "1.1.0");
		
		headers.put("X-PAYPAL-REQUEST-DATA-FORMAT", "NV");
		
		headers.put("X-PAYPAL-RESPONSE-DATA-FORMAT", "NV");
		
		return headers;
		
	}
	
	
}
