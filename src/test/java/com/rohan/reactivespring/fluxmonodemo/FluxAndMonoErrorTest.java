package com.rohan.reactivespring.fluxmonodemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorTest() {

        // Shows how to test expected error
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException()))
                .concatWith(Flux.just("D"))
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription() // Expect subscription from flux
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }


    @Test
    public void fluxErrorHandling_onErrorResume() {

        // Shows how to test expected error
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException()))
                .concatWith(Flux.just("D"))
                .onErrorResume(e -> { // This block gets executed
                    System.out.println("Exception is " + e);
                    return Flux.just("default", "default1");
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription() // Expect subscription from flux
                .expectNext("A", "B", "C")
                .expectNext("default", "default1")
//                .expectError(RuntimeException.class)
//                .verify();
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_onErrorReturn() {

        // Shows how to test expected error
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException()))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default")
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription() // Expect subscription from flux
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();
    }


    @Test
    public void fluxErrorHandling_onErrorMap() {

        // Assign exception to another type
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException()))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription() // Expect subscription from flux
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_withRetry() { // Like dB is not available due to connection issue

        // Assign exception to another type
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException()))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .retry(2)
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription() // Expect subscription from flux
                .expectNext("A", "B", "C") // Original
                .expectNext("A", "B", "C")// Same event will be emmitted before handling of the error - first retry
                .expectNext("A", "B", "C") // second retry
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_withRetryBackOff() { // Like dB is not available due to connection issue, so we might need some

        // Assign exception to another type
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(CustomException::new)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(5)))
                .log();

        StepVerifier.create(stringFlux)
                .expectSubscription() // Expect subscription from flux
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)// Time sensitive returns illegal not custom one
                .verify();
    }

}
