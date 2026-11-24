package com.dibaj.dibajboardgamebuddy;

import java.util.List;

public record EvaluationInput(
        String question,
        String answer,
        List<String> context) {
}
