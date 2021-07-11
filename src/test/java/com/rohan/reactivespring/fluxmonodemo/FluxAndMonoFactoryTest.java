package com.rohan.reactivespring.fluxmonodemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = List.of("meliodas", "estrosa", "bahn", "king");

    @Test
    public void fluxUsingIterable() {

        Flux<String> namesFlux = Flux.fromIterable(names).log();

        StepVerifier.create(namesFlux)
                .expectNext("meliodas", "estrosa", "bahn", "king")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        String[] names = new String[]{"meliodas", "estrosa", "bahn", "king"};

        Flux<String> namesFlux = Flux.fromArray(names);

        StepVerifier.create(namesFlux)
                .expectNext("meliodas", "estrosa", "bahn", "king")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        // As streams are lazy data won't be available until we call the terminal function for it to work
        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("meliodas", "estrosa", "bahn", "king")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        // Example for value is already shown before in
        Mono<Object> mono = Mono.justOrEmpty(null); // same as Mono.Empty();

        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "meliodas";
        Mono<Object> mono = Mono.fromSupplier(stringSupplier); // same as Mono.Empty();

        StepVerifier.create(mono.log())
                .expectNext("meliodas") // Supplier.get is called internally by reactor
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> numbersFlux = Flux.range(1, 5).log();

        StepVerifier.create(numbersFlux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }
}
