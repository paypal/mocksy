package com.mocksysample;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SendMoney extends HttpServlet {

	private static final long serialVersionUID = 1L;

	PrintWriter output;

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		try {

			String primaryReceiver  =  req.getParameter("primaryReceiver"); //"ashsar_1233867711_biz@yahoo.com";
			String secondaryReceiver = req.getParameter("secondaryReceiver");
			String callerAmount = "10.0";
			String receiverAmount = "9.0";
			sendMoney(primaryReceiver, secondaryReceiver, callerAmount, receiverAmount,
					req, res);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendMoney(String callerEmail, String receiverEmail,
			String callerAmount, String receiverAmount, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		/*
		 * //String url =
		 * "https://svcs.sandbox.paypal.com/AdaptivePayments/Pay/"; //String url
		 * = "http://localhost:8080/AdaptivePayments/Pay/"; String actionType =
		 * "PAY"; String returnUrl =
		 * "http://localhost:9090/MocksySample/smsuccess.jsp"; String cancelUrl
		 * = "http://localhost:9090/MocksySample/smcancel.jsp"; String
		 * errorLanguage = "en_US"; String ipAddress = "127.0.0.1"; String
		 * deviceId = "myDevice"; String currencyCode = "USD"; String feesPayer
		 * = "EACHRECEIVER";
		 */

		String url = req.getParameter("serverUrl");

		String actionType = req.getParameter("actionType");
		String returnUrl = req.getParameter("returnUrl");
		String cancelUrl = req.getParameter("cancelUrl");
		String errorLanguage = req.getParameter("errorLanguage");
		String ipAddress = req.getParameter("ipAddress");
		String deviceId = req.getParameter("myDevice");
		String currencyCode = req.getParameter("currencyCode");
		String feesPayer = req.getParameter("feesPayer");

		RequestHelper ppRequest = new RequestHelper();

		Properties headers = MyUtil.getHeaders();

		StringBuffer body = new StringBuffer();
		body.append("actionType=" + actionType);
		body.append("&");
		body.append("currencyCode=" + currencyCode);
		body.append("&");
		body.append("feesPayer=" + feesPayer);

		body.append("&");
		body.append("receiverList.receiver(0).email=" + callerEmail);
		body.append("&");
		body.append("receiverList.receiver(0).amount=" + callerAmount);
		
		if(receiverEmail != null && receiverEmail.length() !=0){
			body.append("&");
			body.append("receiverList.receiver(0).primary=true");			
			body.append("&");
			body.append("receiverList.receiver(1).email=" + receiverEmail);
			body.append("&");
			body.append("receiverList.receiver(1).primary=false");
			body.append("&");
			body.append("receiverList.receiver(1).amount=" + receiverAmount);
		}
				
		body.append("&");
		body.append("returnUrl=" + returnUrl);
		body.append("&");
		body.append("cancelUrl=" + cancelUrl);
		body.append("&");
		body.append("requestEnvelope.errorLanguage=" + errorLanguage);
		body.append("&");
		body.append("clientDetails.ipAddress=" + ipAddress);
		body.append("&");
		body.append("clientDetails.deviceId=" + deviceId);
		body.append("&");
		body.append("clientDetails.applicationId=PayNvpDemo");

		String response = ppRequest.sendHttpPost(
				url + "/AdaptivePayments/Pay/", body.toString(), headers);

		ResponseHelper.formatResponse(response);

		Map<String, String> resMap = ResponseHelper.getResponseNvpMap(response);

		String ack = resMap.get("responseEnvelope.ack");

		if (ack != null) {
			if (ack.equalsIgnoreCase("Success")) {
				String payKey = resMap.get("payKey");
				if (payKey != null) {
					url = url + "/webscr?" + "cmd=_ap-payment&paykey=" + payKey;
					res.sendRedirect(url);
				}
			}
		}

		// System.out.println("Response=" + response);

		output = res.getWriter();

		output.write(response);

	}

}
