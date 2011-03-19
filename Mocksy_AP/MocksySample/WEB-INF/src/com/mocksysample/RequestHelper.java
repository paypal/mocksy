package com.mocksysample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Properties;
import javax.net.ssl.SSLContext;

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



public class RequestHelper {


	public String sendHttpPost(String url, String payload, Properties headers) {
		SSLContext sc = null;
		sc = getDefaultSSLContext();

		HttpURLConnection connection = setupConnection(url, sc, headers,
				null);

		return sendHttpPost(payload, connection);

	}

	public static String sendHttpPost(String payload,
			HttpURLConnection connection) {
		String line, returnedResponse = "";
		BufferedReader reader = null;

		System.out.println("Request: " + payload);
		try {

			OutputStream os = connection.getOutputStream();
			os.write(payload.toString().getBytes("UTF-8"));
			os.close();
			int status = connection.getResponseCode();
			if (status != 200) {
				System.out.println("HTTP Error code " + status
						+ " received, transaction not submitted");
				reader = new BufferedReader(new InputStreamReader(connection
						.getErrorStream()));
			} else {
				reader = new BufferedReader(new InputStreamReader(connection
						.getInputStream()));
			}

			while ((line = reader.readLine()) != null) {
				returnedResponse += line;
			}

		} catch (Exception e) {
			System.out.println(e);
		} finally {

			try {
				if (reader != null)
					reader.close();
				if (connection != null)
					connection.disconnect();
			} catch (Exception e) {
				System.out.println(e);
			}

		}
		return returnedResponse;
	}

	private static HttpURLConnection setupConnection(String endpoint,
			SSLContext sc, Properties headers, Properties connectionProps) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(endpoint);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			// TODO make configurable

			// connection.setConnectTimeout(Integer
			// .parseInt((String) connectionProps
			// .getProperty(CONNECTION_TIMEOUT)));
			// connection.setReadTimeout(Integer.parseInt((String)
			// connectionProps
			// .getProperty(READ_TIMEOUT)));

			Object[] keys = headers.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				connection.setRequestProperty((String) keys[i],
						(String) headers.get(keys[i]));
			}
		} catch (Exception e) {
			System.out.println("Failed setting up HTTP Connection\n" + e);
		}
		return connection;

	}


	private static SSLContext getDefaultSSLContext() {
		try {
			SSLContext ctx = SSLContext.getInstance("SSL"); 
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(System.currentTimeMillis());

			ctx.init(null, null, null);

			return ctx;

		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	} 
}

