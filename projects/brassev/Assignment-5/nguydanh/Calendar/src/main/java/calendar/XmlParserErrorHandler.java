/*
 * XmlParserErrorHandler.java
 *
 */

package calendar;

import org.xml.sax.*;

/**
* This error handler is to report warnings and errors with the 
* XML Parser. It is modeled after Java's sample code.
*
**/

public class XmlParserErrorHandler implements ErrorHandler {

	/**
	* Returns a string describing parse exception details
	**/
	private String getParseExceptionInfo(SAXParseException spe) {
		String systemId = spe.getSystemId();
		if (systemId == null) {
			systemId = "null";
		}
		String info = "URI=" + systemId +
		" Line=" + spe.getLineNumber() +
		": " + spe.getMessage();
		return info;
	}
	
	// The following methods are standard SAX ErrorHandler methods.
	// See SAX documentation for more info.
	public void warning(SAXParseException spe) throws SAXException {
		//out.println("Warning: " + getParseExceptionInfo(spe));
		//Treat errors as warnings
		String message = "Error: " + getParseExceptionInfo(spe);
		throw new SAXException(message);
	}

	public void error(SAXParseException spe) throws SAXException {
		String message = "Error: " + getParseExceptionInfo(spe);
		throw new SAXException(message);
	}

	public void fatalError(SAXParseException spe) throws SAXException {
		String message = "Fatal Error: " + getParseExceptionInfo(spe);
		throw new SAXException(message);
	}
}