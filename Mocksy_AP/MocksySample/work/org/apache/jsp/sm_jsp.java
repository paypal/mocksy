package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class sm_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("<form name=\"frmSendMoney\" method=\"post\" action=\"SendMoney\">\n");
      out.write("\n");
      out.write("<table>\n");
      out.write("\n");
      out.write("<tr><td>Mocksy Url : <input type=\"text\" name=\"serverUrl\" value=\"http://localhost:8080\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Action Type : <input type=\"text\" name=\"actionType\" value=\"PAY\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Return Url : <input type=\"text\" name=\"returnUrl\" value=\"http://localhost:9090/MocksySample/smsuccess.jsp\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Cancel Url : <input type=\"text\" name=\"cancelUrl\" value=\"http://localhost:9090/MocksySample/smcancel.jsp\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Error Language : <input type=\"text\" name=\"errorLanguage\" value=\"en_US\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Currency Code : <input type=\"text\" name=\"currencyCode\" value=\"USD\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Fees Payer : <input type=\"text\" name=\"feesPayer\" value=\"EACHRECEIVER\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Ip Address : <input type=\"text\" name=\"ipAddress\" value=\"127.0.0.1\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Device Id : <input type=\"text\" name=\"deviceId\" value=\"myDevice\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td>\n");
      out.write("\n");
      out.write("Primary Receiver Email : \n");
      out.write("\n");
      out.write("<select name=\"primaryReceiver\">\n");
      out.write("\n");
      out.write("<option value=\"PAY_SUCCESS@test.com\">PAY_SUCCESS@test.com</option>\n");
      out.write("<option value=\"PAY_SUCCESS_USR_CANCEL@test.com\">PAY_SUCCESS_USR_CANCEL@test.com</option>\n");
      out.write("<option value=\"PAY_INVALID_ACCT_RCV_CNTRY_EMAIL@test.com\">PAY_INVALID_ACCT_RCV_CNTRY_EMAIL@test.com</option>\n");
      out.write("<option value=\"PAY_INVALID_ACCT_SND_CNTRY_EMAIL@test.com\">PAY_INVALID_ACCT_SND_CNTRY_EMAIL@test.com</option>\n");
      out.write("<option value=\"PAY_INVALID_ACCT_SET_RCVR@test.com\">PAY_INVALID_ACCT_SET_RCVR@test.com</option>\n");
      out.write("<option value=\"PAY_ACCT_EXD_RCV_LMT@test.com\">PAY_ACCT_EXD_RCV_LMT@test.com</option>\n");
      out.write("<option value=\"PAY_CHAIN_INVALID_BUS_ACCT_PRIMARY@test.com\">PAY_CHAIN_INVALID_BUS_ACCT_PRIMARY@test.com</option>\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("<tr><td>\n");
      out.write("Secondary Receiver Email : \n");
      out.write("<select name=\"secondaryReceiver\">\n");
      out.write("<option value=\"\">&nbsp;</option>\n");
      out.write("<option value=\"PAY_CHAIN_SECONDARY_SUCCESS@test.com\">PAY_CHAIN_SECONDARY_SUCCESS@test.com</option>\n");
      out.write("<option value=\"PAY_CHAIN_INVALID_BUS_ACCT_SECONDARY@test.com\">PAY_CHAIN_INVALID_BUS_ACCT_SECONDARY@test.com</option>\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("\n");
      out.write("<tr><td>Amount : <input type=\"text\" name=\"amount\" value=\"10.0\"></td></tr>\n");
      out.write("\n");
      out.write("<tr><td><input type=\"submit\" value=\"Send Money\"></td></tr>\n");
      out.write("\n");
      out.write("</table>\n");
      out.write("\n");
      out.write("</form>\n");
      out.write("\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
