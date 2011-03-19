package com.mocksysample;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/****************************************************************
THIS IS STRICTLY EXAMPLE SOURCE CODE. IT IS ONLY MEANT TO 
QUICKLY DEMONSTRATE THE CONCEPT AND THE USAGE OF DIFFERENT APIS
THAT USES ITS METHODS. PLEASE NOTE THAT THIS IS *NOT* PRODUCTION-QUALITY 
CODE AND SHOULD NOT BE USED AS SUCH.

THIS EXAMPLE CODE IS PROVIDED TO YOU ONLY ON AN "AS IS" 
BASIS WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER 
EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION ANY WARRANTIES 
OR CONDITIONS OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR 
FITNESS FOR A PARTICULAR PURPOSE. PAYPAL MAKES NO WARRANTY THAT 
THE SOFTWARE OR DOCUMENTATION WILL BE ERROR-FREE. IN NO EVENT 
SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY 
DIRECT, INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR 
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.
****************************************************************/




public class ResponseHelper {

	public static void formatResponse(String response) {
		if (response != null) {
			if (response.startsWith("<?xml ")) {
				formatResponseXml(response);
			} else {
				formatResponseNvp(response);
			}
		}
	}

	public static String getApprovalUrl(String response) {
		String url = "";
		if (response != null) {
			if (response.startsWith("<?xml ")) {
				url = getApprovalUrlXml(response);
			} else {
				url = getApprovalUrlNvp(response);
			}
		}
		return url;
	}

	private static void formatResponseXml(String response) {
		if (response != null) {

			SAXParser saxParser = null;
			// Use the default (non-validating) parser
			SAXParserFactory factory = SAXParserFactory.newInstance();
			try {
				DefaultHandler xmlHandler = new XmlHandler();
				saxParser = factory.newSAXParser();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(response));
				saxParser.parse(is, xmlHandler);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private static String formatResponseNvp(String response) {
		String url = "";

		if (response != null) {

			StringTokenizer st = new StringTokenizer(response, "&");
			while (st.hasMoreTokens()) {
				String tmp = st.nextToken();
				System.out.println(tmp);
			}
		}

		return url;
	}
	
	public static Map<String,String> getResponseNvpMap(String response){		
		Map<String,String> map = new HashMap<String,String>();		
		if (response != null) {
			StringTokenizer st = new StringTokenizer(response, "&");
			while (st.hasMoreTokens()) {
				String tmp = st.nextToken();
				StringTokenizer st1 = new StringTokenizer(tmp,"=");
				String key = st1.nextToken();
				String value = st1.nextToken();
				map.put(key,value);
			}
		}		
		return map;
	}	

	private static String getApprovalUrlNvp(String response) {
		String url = "";

		if (response != null) {

			StringTokenizer st = new StringTokenizer(response, "&");
			while (st.hasMoreTokens()) {

				String tmp = st.nextToken();
				if (tmp.startsWith("payKey")) {
					url = "https://www.sandbox.paypal.com/webscr?cmd=_ap-payment&paykey="
							+ tmp.substring(7);
				}

			}
		}

		return url;
	}

	private static String getApprovalUrlXml(String response) {
		String url = "";

		if (response != null) {

			if (response.indexOf("<payKey>") > 0) {
				url = "https://www.sandbox.paypal.com/webscr?cmd=_ap-payment&paykey="
						+ response.substring(
								(response.indexOf("<payKey>") + 8), (response
										.indexOf("</payKey>")));
			}
		}

		return url;
	}
		
	
}
