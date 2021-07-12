package com.rohan.reactivespring.fluxmonodemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .log(); // start from 0  ----> ........ infinite

        infiniteFlux.subscribe(element -> System.out.println("Value is " + element));

        // Uncomment below and the function will complete as subscribe is just registering a callback and values are not
        // emitted yet
        // Emission of flux happens in separate threads
        // And this test case runs in main thread

        Thread.sleep(3000);
    }

    @Test
    public void infiniteSequenceWithoutSleep() throws InterruptedException {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete(); // Verify complete is like a subscribe call equivalent to line 16
        // Actual flow start after verifyComplete is executed
    }



    @Test
    public void infiniteSequenceMapWithoutSleep() throws InterruptedException {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(Long::intValue)
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete(); // Verify complete is like a subscribe call equivalent to line 16
        // Actual flow start after verifyComplete is executed
    }


}
