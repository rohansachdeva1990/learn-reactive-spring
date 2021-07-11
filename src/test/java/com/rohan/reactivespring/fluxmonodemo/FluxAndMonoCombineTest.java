package com.rohan.reactivespring.fluxmonodemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

// Combining multiple publisher
public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge() {

        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        // In reality we might have 2 different db calls or external service call and would like to merge it

        Flux<String> mergedFlux = Flux.merge(flux1, flux2)
                .log();

        // Expect subscription is the first event
        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }


    @Test
    public void combineUsingMergeAnDelay() {

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        // In reality we might have 2 different db calls or external service call and would like to merge it

        Flux<String> mergedFlux = Flux.merge(flux1, flux2)
                .log();

        // Expect subscription is the first event
        // Here merged fluxes wont give the order in sequence due to delay and the result will be actually interleaved
        // eg A THEN D, B THEN E, C THEN F

        StepVerifier.create(mergedFlux)
                .expectSubscription()
//                .expectNext("A", "B", "C", "D", "E", "F")  // Error due to interleaved
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat() {

        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.concat(flux1, flux2)
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcatAnDelay() {

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        // In reality we might have 2 different db calls or external service call and would like to merge it

        Flux<String> mergedFlux = Flux.concat(flux1, flux2)
                .log();

        // Concat will wait for the uppler flux for things to be available before printing

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
//                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingZip() {

        // Useful when we need to combine flux with some extra business logic
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.zip(flux1, flux2, (t1, t2) -> t1.concat(t2)) // A,D -> B,E -> C,F
                .log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
