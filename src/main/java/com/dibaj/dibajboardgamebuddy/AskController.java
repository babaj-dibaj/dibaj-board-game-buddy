package com.dibaj.dibajboardgamebuddy;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.beans.factory.annotation.Qualifier;

@RestController
public class AskController {

    private final BoardGameService boardGameService;
    private final BoardGameService selfEvaluatingBoardGameService;

    public AskController(
            @Qualifier("springAiBoardGameService") BoardGameService boardGameService,
            @Qualifier("selfEvaluatingBoardGameService") BoardGameService selfEvaluatingBoardGameService) {
        this.boardGameService = boardGameService;
        this.selfEvaluatingBoardGameService = selfEvaluatingBoardGameService;
    }

    @PostMapping("/ask")
    public Answer ask(@RequestBody Question question) {
        return boardGameService.askQuestion(question);
    }

    @PostMapping("/ask/evaluated")
    public Answer askWithSelfEvaluation(@RequestBody Question question) {
        return selfEvaluatingBoardGameService.askQuestion(question);
    }

    @ExceptionHandler(AnswerNotRelevantException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public Answer answerNotRelevant() {
        return new Answer("Maaf, saya belum dapat memberikan jawaban yang relevan untuk pertanyaan tersebut.");
    }
}