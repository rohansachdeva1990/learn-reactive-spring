package com.rohan.reactivespring.fluxmonodemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = List.of("meliodas", "estrosa", "bahn", "king");

    @Test
    public void transformUsingMap() {
        Flux<String> nameFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(nameFlux)
                .expectNext("MELIODAS")
                .expectNext("ESTROSA")
                .expectNext("BAHN")
                .expectNext("KING")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_getLength() {
        Flux<Integer> nameFlux = Flux.fromIterable(names)
                .map(String::length)
                .log();

        StepVerifier.create(nameFlux)
                .expectNext(8, 7, 4, 4)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_getLengthRepeat() {
        Flux<Integer> nameFlux = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier.create(nameFlux)
                .expectNext(8, 7, 4, 4)
                .expectNext(8, 7, 4, 4)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Filter() {
        Flux<String> nameFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(String::toUpperCase) // converting flux to string
                .log();

        StepVerifier.create(nameFlux)
                .expectNext("MELIODAS")
                .expectNext("ESTROSA")
                .verifyComplete();
    }


    @Test
    public void transformUsingFlatMap() {
        // A flat is specific use case, when you want to call db or external element for every flux element.
        Flux<String> stringFlux = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> Flux.fromIterable(convertToList(s)))  // A -> List[A, newValue], B -> List[B, newValue]
                .log();

        // For each and every element we get 2 elements due to convertToList
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    // Important
    @Test
    public void transformUsingFlatMapAndParallel() {
        // A flat is specific use case, when you want to call db or external element for every flux element.
        Flux<String> stringFlux = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                // Instead of passing element 1 by 1, it passes 2 elements. Now flux would wait until we have 2 elements
                // at a time and then pass to next layer, (a,B) -> (c,d) -> (e,f); Flux<Flux<String>
                .window(2)
                .flatMap(s -> s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(Flux::fromIterable)
                .log();

        // For each and every element we get 2 elements due to convertToList
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }


    @Test
    public void transformUsingFlatMapAndParallelAndMaintainOrderUsingConcatMap() {
        // 2 approaches, 1. concatMap it maintains order, but it will take complete time which in our case would be 6seconds


        // A flat is specific use case, when you want to call db or external element for every flux element.
        Flux<String> stringFlux = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                // Instead of passing element 1 by 1, it passes 2 elements. Now flux would wait until we have 2 elements
                // at a time and then pass to next layer, (a,B) -> (c,d) -> (e,f); Flux<Flux<String>
                .window(2)
                .concatMap(s -> s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(Flux::fromIterable)
                .log();

        // For each and every element we get 2 elements due to convertToList
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }


    @Test
    public void transformUsingFlatMapAndParallelAndMaintainOrderUsingFlatMapSequential() {
        // 2 approaches, 2.flatMapSequential

        // A flat is specific use case, when you want to call db or external element for every flux element.
        Flux<String> stringFlux = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                // Instead of passing element 1 by 1, it passes 2 elements. Now flux would wait until we have 2 elements
                // at a time and then pass to next layer, (a,B) -> (c,d) -> (e,f); Flux<Flux<String>
                .window(2)
                .flatMapSequential(s -> s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(Flux::fromIterable)
                .log();

        // For each and every element we get 2 elements due to convertToList
        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }
}
