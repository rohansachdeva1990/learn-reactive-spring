package com.rohan.reactivespring.fluxmonodemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class HowDoesFluxWorks {

    @Test
    public void simpleFluxTest() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring");

        // Only way to access data in the flux is using subscribe
        // When we subscribe, the flux will start emitting
        stringFlux
                .subscribe(System.out::println);
    }

    @Test
    public void attachingErrorFluxTest() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred!"))); // Attach error

        // Only way to access data in the flux is using subscribe
        // When we subscribe, the flux will start emitting
        stringFlux
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    public void loggingFluxTest() {

        // Flow
        /**
         * DEBUG reactor.util.Loggers - Using Slf4j logging framework
         * INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
         * INFO reactor.Flux.ConcatArray.1 - request(unbounded)
         * INFO reactor.Flux.ConcatArray.1 - onNext(Spring)
         *
         * INFO reactor.Flux.ConcatArray.1 - onNext(Spring Boot)
         *
         * INFO reactor.Flux.ConcatArray.1 - onNext(Reactive Spring)
         *
         * ERROR reactor.Flux.ConcatArray.1 - onError(java.lang.RuntimeException: Exception occurred!)
         * ERROR reactor.Flux.ConcatArray.1 -
         * java.lang.RuntimeException: Exception occurred!
         */
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred!")))
                .log();

        // Only way to access data in the flux is using subscribe
        // When we subscribe, the flux will start emitting
        stringFlux
                .subscribe(System.out::println, System.err::println);
    }


    @Test
    public void loggingFluxOnlyCompTest() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        /**
         *  INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
         *  INFO reactor.Flux.Array.1 - | request(unbounded)
         *  INFO reactor.Flux.Array.1 - | onNext(Spring)
         *
         *  INFO reactor.Flux.Array.1 - | onNext(Spring Boot)
         *
         *  INFO reactor.Flux.Array.1 - | onNext(Reactive Spring)
         *
         *  INFO reactor.Flux.Array.1 - | onComplete()
         */

        // Only way to access data in the flux is using subscribe
        // When we subscribe, the flux will start emitting
        stringFlux
                .subscribe(System.out::println, x -> System.err.println("Exception is" + x));
    }

    @Test
    public void afterErrorDoesFluxEmitToTheSubscriberOrNot() {

        // Once error is emitted, it wont continue further
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred!")))
                .concatWith(Flux.just("After Error"));

        // Only way to access data in the flux is using subscribe
        // When we subscribe, the flux will start emitting
        stringFlux
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    public void testOnComplete() {
        // Once error is emitted, it wont continue further
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.just("After Error"));

        // Only way to access data in the flux is using subscribe
        // When we subscribe, the flux will start emitting
        stringFlux
                .subscribe(System.out::println, System.err::println,
                        () -> System.out.println("Completed!!")); // We can add callback to notify us on finish
    }
}
