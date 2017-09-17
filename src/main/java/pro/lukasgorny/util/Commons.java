package pro.lukasgorny.util;

/**
 * Created by lukaszgo on 2017-09-12.
 */
public class Commons {
    public static final String RDF_NAMESPACE = "https://lukasgorny.pro/devices#";
    public static final String RDF_FILENAME = "devices.rdf";
    public static final String FONOAPI_TOKEN = "a3ee55911da2a3bde6a485e0fa42118ec03f8a810a12ea0f";
    public static final String FONOAPI_GET_LATEST_LINK = "https://fonoapi.freshpixl.com/v1/getlatest";
    public static final String RDF_TYPE = "RDF/XML-ABBREV";

    public static class Regex {
        public static String SCREEN_SIZE = "( inches .*| inches)";
        public static String INTERNAL_MEMORY_SIZE = ",.*";
        public static String INTERNAL_MEMORY_SIZE_UNIT = " *GB| *MB";
        public static String YEAR_IN_DEVICE_NAME = " [(][0-9]*[)]";
        public static String ALL_WHITESPACE = "\\s+";
    }
}
