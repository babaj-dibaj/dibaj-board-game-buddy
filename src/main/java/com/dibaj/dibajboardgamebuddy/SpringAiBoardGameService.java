package com.dibaj.dibajboardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service("springAiBoardGameService")
public class SpringAiBoardGameService implements BoardGameService {

    private final ChatClient chatClient;

    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are a friendly and knowledgeable board game expert assistant.
                        Help users understand board game rules, strategies, tips, and recommendations.
                        Answer clearly and concisely. Format your answer with proper paragraphs and bullet points when listing items.
                        """)
                .build();
    }

    @Override
    public Answer askQuestion(Question question) {
        String answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();
        return new Answer(answerText);
    }
}