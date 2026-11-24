package com.dibaj.dibajboardgamebuddy;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service("selfEvaluatingBoardGameService")
public class SelfEvaluatingBoardGameService implements BoardGameService {

    private final ChatClient chatClient;
    private final ChatClient evaluatorClient;

    private static final PromptTemplate RELEVANCY_PROMPT = new PromptTemplate("""
            Your task is to evaluate if the generated answer is relevant and directly answers the user's question.

            You have two options to answer: YES or NO.

            Answer YES if the answer is relevant and helpful for the question.
            Answer NO if the answer is completely off-topic, evasive, or irrelevant.
            Reply with ONLY the single word YES or NO.

            Question:
            {query}

            Generated Answer:
            {response}

            Answer:
            """);

    public SelfEvaluatingBoardGameService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.evaluatorClient = chatClientBuilder.build();
    }

    @Override
    @Retryable(value = AnswerNotRelevantException.class, maxRetries = 1)
    public Answer askQuestion(Question question) {
        String answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();

        if (answerText == null || answerText.isBlank()) {
            throw new IllegalStateException("The language model returned an empty answer");
        }

        String evalPrompt = RELEVANCY_PROMPT.render(Map.of(
                "query", question.question(),
                "response", answerText
        ));

        String rawEval = evaluatorClient.prompt().user(evalPrompt).call().content();
        boolean pass = EvaluationService.isPositive(rawEval);

        if (!pass) {
            throw new AnswerNotRelevantException(question.question(), answerText);
        }

        return new Answer(answerText);
    }

}
