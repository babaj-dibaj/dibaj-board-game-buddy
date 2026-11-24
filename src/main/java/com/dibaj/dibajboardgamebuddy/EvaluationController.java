package com.dibaj.dibajboardgamebuddy;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evaluate")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/relevancy")
    public EvaluationResult evaluateRelevancy(@RequestBody EvaluationInput input) {
        return evaluationService.evaluateRelevancy(input);
    }

    @PostMapping("/factual")
    public EvaluationResult evaluateFactualAccuracy(@RequestBody EvaluationInput input) {
        return evaluationService.evaluateFactualAccuracy(input);
    }
}
