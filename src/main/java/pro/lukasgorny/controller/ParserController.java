package pro.lukasgorny.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import pro.lukasgorny.model.dto.ImportStatsDto;
import pro.lukasgorny.service.ParserService;

import java.io.IOException;

/**
 * Created by lukaszgo on 2017-09-12.
 */
@Controller
public class ParserController {

    private final ParserService parserService;

    @Autowired
    public ParserController(ParserService parserService) {
        this.parserService = parserService;
    }

    @GetMapping("/parser")
    public ModelAndView launch() throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        ImportStatsDto importStatsDto = parserService.parse();
        modelAndView.setViewName("parser");
        modelAndView.addObject("stats", importStatsDto);

        return modelAndView;
    }
}
