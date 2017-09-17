package pro.lukasgorny.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.stereotype.Service;
import pro.lukasgorny.model.dto.SurveyDto;
import pro.lukasgorny.util.Commons;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReasonerService {

    private Model model;
    private List<String> results;
    private SurveyDto criterias;
    private Query query;
    private QueryExecution queryExecution;
    private ResultSet resultSet;

    public List<String> execute() {
        initialize();
        readModelFromFile();
        prepareQuery();
        getPhonesByCriteria();

        return results;
    }

    private void initialize() {
        results = new ArrayList<>();
    }

    private void readModelFromFile() {
        model = RDFDataMgr.loadModel(Commons.RDF_FILENAME, Lang.RDFXML);
    }

    private void prepareQuery() {
        prepareQueryBody();
        query = QueryFactory.create(prepareQueryBody());
        queryExecution = QueryExecutionFactory.create(query, model);
    }

    private Query prepareQueryBody() {
        String queryString = "PREFIX feature: <https://lukasgorny.pro/devices#>" +
                "SELECT ?device WHERE { " +
                "?device feature:device-name ?deviceName ." +
                "OPTIONAL { ?x feature:screen-size ?screenSize . } " +
                "OPTIONAL { ?y feature:internal-memory-size ?internalMemorySize . } " +
                "}" +
                "GROUP BY ?device";

        ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString();
        parameterizedSparqlString.setCommandText(queryString);
        parameterizedSparqlString.setLiteral("screenSize", criterias.getScreenSize());
        parameterizedSparqlString.setLiteral("internalMemorySize", criterias.getInternalMemorySize());

        return parameterizedSparqlString.asQuery();
    }

    private void getPhonesByCriteria() {
        try {
            resultSet = queryExecution.execSelect();
            ResultSetFormatter.out(System.out, resultSet, query);
        } finally {
            queryExecution.close();
        }
    }

    public void setCriterias(SurveyDto criterias) {
        this.criterias = criterias;
    }
}