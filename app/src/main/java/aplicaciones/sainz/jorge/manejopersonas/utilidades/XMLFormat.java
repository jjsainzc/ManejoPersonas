package aplicaciones.sainz.jorge.manejopersonas.utilidades;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by JJSC on 19/02/2018.
 * Usa dom4j-xx.jar
 */

public class XMLFormat {

    public static String prettyFormat(String xml) throws DocumentException, IOException {
        Document doc = DocumentHelper.parseText(xml);
        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xw = new XMLWriter(sw, format);
        xw.write(doc);
        return sw.toString();

    }
}
