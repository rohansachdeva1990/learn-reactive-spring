package com.rohan.reactivespring.fluxmonodemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MonoTest {

    @Test
    public void monoTest() {
        // Takes just one value
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTestWithLogInStepVerifier() {
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTestError() {
        Mono<Object> errorMono = Mono.error(new RuntimeException("Exception occurred")).log();

        StepVerifier.create(errorMono)
                .expectError(RuntimeException.class)
                .verify();
    }
}
