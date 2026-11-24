package com.dibaj.dibajboardgamebuddy;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class EvaluationService {

    private final ChatClient chatClient;

    private static final PromptTemplate RELEVANCY_PROMPT = new PromptTemplate("""
            Your task is to evaluate if the response for the query
            is in line with the context information provided.

            You have two options to answer: YES or NO.

            Answer YES if the response is in line with the query and context, otherwise answer NO.
            Reply with ONLY the single word YES or NO. Do not add any punctuation, explanation, or extra characters.

            Query:
            {query}

            Response:
            {response}

            Context:
            {context}

            Answer:
            """);

    private static final PromptTemplate FACTUAL_PROMPT = new PromptTemplate("""
            Evaluate whether or not the following claim is supported by the provided document.
            Respond with "YES" if the claim is supported, or "NO" if it is not.
            Reply with ONLY the single word YES or NO. Do not add any punctuation, explanation, or extra characters.

            Document:
            {document}

            Claim:
            {claim}

            Answer:
            """);

    public EvaluationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public EvaluationResult evaluateRelevancy(EvaluationInput input) {
        String context = input.context() == null || input.context().isEmpty()
                ? (input.answer() != null ? input.answer() : "")
                : String.join("\n", input.context());

        String prompt = RELEVANCY_PROMPT.render(Map.of(
                "query", input.question() != null ? input.question() : "",
                "response", input.answer() != null ? input.answer() : "",
                "context", context
        ));

        String rawResponse = chatClient.prompt().user(prompt).call().content();
        System.out.println("[DEBUG-RELEVANCY] rawResponse: '" + rawResponse + "'");
        boolean pass = isPositive(rawResponse);
        float score = pass ? 1.0f : 0.0f;
        String feedback = pass
                ? "Jawaban dinilai relevan dengan pertanyaan dan selaras dengan konteks yang diberikan."
                : "Jawaban dinilai tidak relevan atau tidak menjawab pertanyaan sesuai konteks.";
        return new EvaluationResult(pass, score, feedback);
    }

    public EvaluationResult evaluateFactualAccuracy(EvaluationInput input) {
        String context = input.context() == null || input.context().isEmpty()
                ? ""
                : String.join("\n", input.context());

        String prompt = FACTUAL_PROMPT.render(Map.of(
                "document", context,
                "claim", input.answer() != null ? input.answer() : ""
        ));

        String rawResponse = chatClient.prompt().user(prompt).call().content();
        System.out.println("[DEBUG-RELEVANCY] rawResponse: '" + rawResponse + "'");
        boolean pass = isPositive(rawResponse);
        float score = pass ? 1.0f : 0.0f;
        String feedback = pass
                ? "Klaim/jawaban didukung secara faktual oleh dokumen acuan (Factually Accurated)."
                : "Klaim/jawaban tidak didukung atau bertentangan dengan dokumen acuan (Hallucination detected).";
        return new EvaluationResult(pass, score, feedback);
    }

    public static boolean isPositive(String response) {
        if (response == null || response.isBlank()) return false;
        String trimmed = response.trim().toLowerCase();
        if (trimmed.startsWith("yes")) return true;
        if (trimmed.startsWith("no")) return false;

        boolean hasYes = trimmed.matches("(?s).*\\byes\\b.*");
        boolean hasNo = trimmed.matches("(?s).*\\bno\\b.*");
        if (hasYes && !hasNo) return true;
        if (hasNo && !hasYes) return false;

        // If both or neither, check whether "yes" occurs before "no"
        if (hasYes && hasNo) {
            return trimmed.lastIndexOf("yes") > trimmed.lastIndexOf("no");
        }
        return false;
    }
}
