package com.dibaj.dibajboardgamebuddy;

public record EvaluationResult(
        boolean pass,
        float score,
        String feedback) {
}
