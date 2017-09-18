package pro.lukasgorny.controller;

import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import pro.lukasgorny.model.dto.ResultDeviceDto;
import pro.lukasgorny.model.dto.SurveyDto;
import pro.lukasgorny.service.ReasonerService;

import java.util.List;

@Controller
public class IndexController {

    private final ReasonerService reasonerService;

    @Autowired
    public IndexController(ReasonerService reasonerService) {
        this.reasonerService = reasonerService;
    }

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("survey", new SurveyDto());
        modelAndView.setViewName("index");

        return modelAndView;
    }

    @PostMapping("/result")
    public ModelAndView result(@ModelAttribute SurveyDto survey) throws ParseException {
        ModelAndView modelAndView = new ModelAndView();
        reasonerService.setCriterias(survey);

        List<ResultDeviceDto> devices = reasonerService.execute();

        modelAndView.setViewName("result");
        modelAndView.addObject("devices", devices);

        return modelAndView;
    }
}