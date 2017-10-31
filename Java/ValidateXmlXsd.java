import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * Custom error handler while validating xml against xsd
 */

public class ValidateXmlXsd extends HttpServlet{
	private static final long serialVersionUID = -1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
 
        PrintWriter writer = response.getWriter();
        writer.println("<html>Este servlet no acepta GET</html>");
        writer.flush();
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String xmlString = request.getParameter("xmlString").trim().replaceAll("\"","\\\""); //Cambiamos las \" por \\"
		String result ="";
		
		try {
	           SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	           Schema schema = factory.newSchema(ValidateXmlXsd.class.getResource("/webpayxml.xsd"));
	           Validator validator = schema.newValidator();
	           validator.setErrorHandler(new XsdErrorHandler());
	           validator.validate(new StreamSource(new StringReader(xmlString)));
	           result="Validación exitosa. Ve al Paso 2: Cifrando la cadena";
	       } catch (IOException e) {
	           // handle exception while reading source
	       } catch (SAXException e) {
	           result = "La cadena no cumple con el esquema \n"+e.getMessage();
	       }
		
		response.setContentType("text/plain");
		PrintWriter writer = response.getWriter();
        writer.println(result);
        writer.flush();
	}
}
