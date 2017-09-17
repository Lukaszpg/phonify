package pro.lukasgorny.service;

import org.apache.jena.Jena;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.stereotype.Service;
import pro.lukasgorny.model.dto.SurveyDto;
import pro.lukasgorny.util.Commons;
import pro.lukasgorny.util.JenaProperties;
import pro.lukasgorny.util.enums.InternalMemorySize;
import pro.lukasgorny.util.enums.ScreenSize;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReasonerService {

    private Model model;
    private List<String> results;
    private SurveyDto criterias;
    private List<Resource> devices;

    public List<String> execute() {
        initialize();
        readModelFromFile();
        getPhonesByFilters();

        return results;
    }

    private void initialize() {
        results = new ArrayList<>();
    }

    private void readModelFromFile() {
        model = RDFDataMgr.loadModel(Commons.RDF_FILENAME, Lang.RDFXML);
    }

    private void getPhonesByFilters() {
        /*if (shouldFilterByScreenSize()) {
            devices = model.listResourcesWithProperty(JenaProperties.screenSize,
                    model.createLiteral(ScreenSize.valueOf(criterias.getScreenSize()).name())).toList();
        }

        if (shouldFilterByInternalMemorySize()) {
            devices = model.listResourcesWithProperty(JenaProperties.internalMemorySize,
                    model.createLiteral(InternalMemorySize.valueOf(criterias.getInternalMemorySize()).name())).toList();
        }*/

        String queryString = "PREFIX feature: <https://lukasgorny.pro/devices#>" +
                "SELECT ?x WHERE {" +
                "?x feature:device-name ?name ." +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);

        try {
            ResultSet resultSet = queryExecution.execSelect();
            ResultSetFormatter.out(System.out, resultSet, query);
        } finally {
            queryExecution.close();
        }

        devices.forEach(device -> results.add(device.getProperty(JenaProperties.deviceName).getLiteral().toString()));
    }

    private boolean shouldFilterByScreenSize() {
        return criterias.getScreenSize() != null && !criterias.getScreenSize().isEmpty();
    }

    private boolean shouldFilterByInternalMemorySize() {
        return criterias.getInternalMemorySize() != null && !criterias.getInternalMemorySize().isEmpty();
    }

    public SurveyDto getCriterias() {
        return criterias;
    }

    public void setCriterias(SurveyDto criterias) {
        this.criterias = criterias;
    }
}