package pro.lukasgorny.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.springframework.stereotype.Service;

import pro.lukasgorny.model.dto.ResultDeviceDto;
import pro.lukasgorny.model.dto.SurveyDto;
import pro.lukasgorny.util.Commons;

@Service
public class ReasonerService {

    private Model model;
    private List<ResultDeviceDto> results;
    private SurveyDto criterias;
    private QueryExecution queryExecution;

    public List<ResultDeviceDto> execute() throws ParseException {
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

    private void prepareQuery() throws ParseException {
        prepareQueryBody();
        Query query = QueryFactory.create(prepareQueryBody());
        queryExecution = QueryExecutionFactory.create(query, model);
    }

    private Query prepareQueryBody() throws ParseException {
        String queryString = "PREFIX feature: <https://lukasgorny.pro/devices#>" +
                "SELECT ?deviceName ?memoryNumeric ?screenSizeNumeric ?memoryUnit ?jack WHERE { ?x feature:device-name ?deviceName ;" +
                " feature:internal-memory-size-numeric ?memoryNumeric ;" +
                " feature:screen-size-numeric ?screenSizeNumeric ;" +
                " feature:internal-memory-unit ?memoryUnit ;" +
                " feature:music-jack ?jack ;" +
                " feature:screen-size ?screenSize ;" +
                " feature:internal-memory-size ?memorySize" +
                buildFilter()
                + " }";

        ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString();
        parameterizedSparqlString.setCommandText(queryString);
        parameterizedSparqlString.setLiteral("screenSizeParam", criterias.getScreenSize());
        parameterizedSparqlString.setLiteral("internalMemorySizeParam", criterias.getInternalMemorySize());
        parameterizedSparqlString.setLiteral("jackParam", criterias.getMusicJack());

        return parameterizedSparqlString.asQuery();
    }

    private String buildFilter() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" FILTER ( ");
        stringBuilder.append("str(?screenSize) = ?screenSizeParam && str(?memorySize) = ?internalMemorySizeParam && str(?jack) = ?jackParam");
        stringBuilder.append(" )");

        return stringBuilder.toString();
    }

    private void getPhonesByCriteria() {
        try {
            ResultSet resultSet = queryExecution.execSelect();

            while(resultSet.hasNext()) {
                QuerySolution querySolution = resultSet.nextSolution();
                ResultDeviceDto resultDeviceDto = prepareResultDeviceDto(querySolution);
                results.add(resultDeviceDto);
            }
        } finally {
            queryExecution.close();
        }
    }

    private ResultDeviceDto prepareResultDeviceDto(QuerySolution querySolution) {
        ResultDeviceDto returnDto = new ResultDeviceDto();
        Literal deviceName = querySolution.getLiteral("deviceName");
        Literal memorySize = querySolution.getLiteral("memoryNumeric");
        Literal screenSize = querySolution.getLiteral("screenSizeNumeric");
        Literal memoryUnit = querySolution.getLiteral("memoryUnit");
        Literal musicJack = querySolution.getLiteral("jack");

        returnDto.setDeviceName(deviceName.toString());
        returnDto.setMemorySize(memorySize.toString());
        returnDto.setScreenSize(screenSize.toString());
        returnDto.setMemoryUnit(memoryUnit.toString());
        returnDto.setMusicJack(musicJack.toString());

        return returnDto;
    }

    public void setCriterias(SurveyDto criterias) {
        this.criterias = criterias;
    }
}