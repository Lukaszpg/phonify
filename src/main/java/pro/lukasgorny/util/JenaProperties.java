package pro.lukasgorny.util;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

/**
 * Created by lukaszgo on 2017-09-12.
 */
public class JenaProperties {
    private static Model model = ModelFactory.createDefaultModel();
    public static Property deviceName;
    public static Property screenSize;

    static {
        deviceName = model.createProperty(Commons.RDF_NAMESPACE, "device-name");
        screenSize = model.createProperty(Commons.RDF_NAMESPACE, "screen-size");
    }

}
