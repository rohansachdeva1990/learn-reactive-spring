package com.rohan.reactivespring.fluxmonodemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = List.of("meliodas", "estrosa", "bahn", "king");

    @Test
    public void filterTest() {

        Flux<String> nameFlux = Flux.fromIterable(names)
                .filter(s -> s.startsWith("e"))
                .log(); // only estrosa will pass

        StepVerifier.create(nameFlux)
                .expectNext("estrosa")
                .verifyComplete();
    }


    @Test
    public void filterTestByLength() {

        Flux<String> nameFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .log();

        StepVerifier.create(nameFlux)
                .expectNext("meliodas", "estrosa")
                .verifyComplete();
    }

}
