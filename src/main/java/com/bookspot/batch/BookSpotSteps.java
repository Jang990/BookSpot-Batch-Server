package com.bookspot.batch;

import org.springframework.batch.core.Step;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BookSpotSteps {
    private final Map<String, Step> steps;

    public BookSpotSteps(List<Step> steps) {
        this.steps = Map.copyOf(
                steps.stream()
                        .collect(Collectors.toMap(Step::getName, Function.identity()))
        );
    }

    public Step getStep(String stepName) {
        return steps.get(stepName);
    }
}
