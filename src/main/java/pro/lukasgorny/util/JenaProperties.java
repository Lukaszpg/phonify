package pro.lukasgorny.util;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class JenaProperties {
    private static Model model = ModelFactory.createDefaultModel();
    public static Property deviceName;
    public static Property screenSize;
    public static Property internalMemorySize;
    public static Property screenSizeNumeric;
    public static Property internalMemorySizeNumeric;
    public static Property internalMemoryUnit;
    public static Property musicJack;
    public static Property primaryCameraQuality;
    public static Property hasDualCamera;
    public static Property hasDualSim;
    public static Property isTablet;

    static {
        deviceName = model.createProperty(Commons.RDF_NAMESPACE, "device-name");
        screenSize = model.createProperty(Commons.RDF_NAMESPACE, "screen-size");
        screenSizeNumeric = model.createProperty(Commons.RDF_NAMESPACE, "screen-size-numeric");
        internalMemorySize = model.createProperty(Commons.RDF_NAMESPACE, "internal-memory-size");
        internalMemorySizeNumeric = model.createProperty(Commons.RDF_NAMESPACE, "internal-memory-size-numeric");
        internalMemoryUnit = model.createProperty(Commons.RDF_NAMESPACE, "internal-memory-unit");
        musicJack = model.createProperty(Commons.RDF_NAMESPACE, "music-jack");
        primaryCameraQuality = model.createProperty(Commons.RDF_NAMESPACE, "primary-camera-quality");
        hasDualCamera = model.createProperty(Commons.RDF_NAMESPACE, "has-dual-camera");
        hasDualSim = model.createProperty(Commons.RDF_NAMESPACE, "has-dual-sim");
        isTablet = model.createProperty(Commons.RDF_NAMESPACE, "is-tablet");
    }

}
