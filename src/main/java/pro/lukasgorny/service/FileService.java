package pro.lukasgorny.service;

import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;
import pro.lukasgorny.util.Commons;

import java.io.FileWriter;
import java.io.IOException;

@Service
public class FileService {

    public void writeFileToDisk(Model model) throws IOException {
        FileWriter out = new FileWriter(Commons.RDF_FILENAME);
        try {
            model.write(out, Commons.RDF_TYPE);
        } finally {
            try {
                out.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }
    }
}