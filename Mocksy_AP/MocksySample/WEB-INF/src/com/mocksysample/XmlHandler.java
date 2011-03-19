package com.mocksysample;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
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



public class XmlHandler extends DefaultHandler {

	private static String spacer = "   ";
	private static int counter = 0;
	private static String lastFunction = "";

	public XmlHandler() {
	}

	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================
	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {
		String eName = sName; // element name
		

		if ("".equals(eName)) {
			eName = qName; // not namespaceAware
		}

		System.out.println("");
		for (int i = 0; i < counter; i++) {
			System.out.print(spacer);
		}
		System.out.print("<" + eName + ">");

		counter++;
		lastFunction = "startElement";
	}

	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) throws SAXException {
		
		String eName = sName; // element name

		if ("".equals(eName)) {
			eName = qName; // not namespaceAware
		}

		counter--;
		if ("endElement".equals(lastFunction)) {
			System.out.println();
		}
		if (!"characters".equals(lastFunction)) {
			for (int i = 0; i < counter; i++) {
				System.out.print(spacer);
			}
		}
		
		System.out.print("</" + eName + ">");
		lastFunction = "endElement";
	}

	public void characters(char[] buf, int offset, int len) throws SAXException {
		String s = new String(buf, offset, len);
		System.out.print(s);
		lastFunction = "characters";
	}
}
