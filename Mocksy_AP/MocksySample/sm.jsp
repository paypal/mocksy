
<form name="frmSendMoney" method="post" action="SendMoney">

<table>

<tr><td>Mocksy Url : <input type="text" name="serverUrl" value="http://localhost:8080"></td></tr>

<tr><td>Action Type : <input type="text" name="actionType" value="PAY"></td></tr>

<tr><td>Return Url : <input type="text" name="returnUrl" value="http://localhost:9090/MocksySample/smsuccess.jsp"></td></tr>

<tr><td>Cancel Url : <input type="text" name="cancelUrl" value="http://localhost:9090/MocksySample/smcancel.jsp"></td></tr>

<tr><td>Error Language : <input type="text" name="errorLanguage" value="en_US"></td></tr>

<tr><td>Currency Code : <input type="text" name="currencyCode" value="USD"></td></tr>

<tr><td>Fees Payer : <input type="text" name="feesPayer" value="EACHRECEIVER"></td></tr>

<tr><td>Ip Address : <input type="text" name="ipAddress" value="127.0.0.1"></td></tr>

<tr><td>Device Id : <input type="text" name="deviceId" value="myDevice"></td></tr>

<tr><td>

Primary Receiver Email : 

<select name="primaryReceiver">

<option value="PAY_SUCCESS@test.com">PAY_SUCCESS@test.com</option>
<option value="PAY_SUCCESS_USR_CANCEL@test.com">PAY_SUCCESS_USR_CANCEL@test.com</option>
<option value="PAY_INVALID_ACCT_RCV_CNTRY_EMAIL@test.com">PAY_INVALID_ACCT_RCV_CNTRY_EMAIL@test.com</option>
<option value="PAY_INVALID_ACCT_SND_CNTRY_EMAIL@test.com">PAY_INVALID_ACCT_SND_CNTRY_EMAIL@test.com</option>
<option value="PAY_INVALID_ACCT_SET_RCVR@test.com">PAY_INVALID_ACCT_SET_RCVR@test.com</option>
<option value="PAY_ACCT_EXD_RCV_LMT@test.com">PAY_ACCT_EXD_RCV_LMT@test.com</option>
<option value="PAY_CHAIN_INVALID_BUS_ACCT_PRIMARY@test.com">PAY_CHAIN_INVALID_BUS_ACCT_PRIMARY@test.com</option>
</td></tr>

<tr><td>
Secondary Receiver Email : 
<select name="secondaryReceiver">
<option value="">&nbsp;</option>
<option value="PAY_CHAIN_SECONDARY_SUCCESS@test.com">PAY_CHAIN_SECONDARY_SUCCESS@test.com</option>
<option value="PAY_CHAIN_INVALID_BUS_ACCT_SECONDARY@test.com">PAY_CHAIN_INVALID_BUS_ACCT_SECONDARY@test.com</option>
</td></tr>


<tr><td>Amount : <input type="text" name="amount" value="10.0"></td></tr>

<tr><td><input type="submit" value="Send Money"></td></tr>

</table>

</form>

