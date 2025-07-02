package nuts.tech_learning_archive.sec8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class RunAsync {

    private static final Logger log = LoggerFactory.getLogger(RunAsync.class);

    public static void main(String[] args) throws InterruptedException {

        log.info("main starts");

        runAsync()
                .thenRun(() -> log.info("it is done"))
                .exceptionally(throwable -> {
                    log.info("error - {}", throwable.getMessage());
                    return null;
                });

        log.info("main ends");

        Thread.sleep(Duration.ofSeconds(2));
    }

    private static CompletableFuture<Void> runAsync() {
        log.info("method starts");

        var cf = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            log.info("task completed");
            throw new RuntimeException("oops");
        }, Executors.newVirtualThreadPerTaskExecutor());

        log.info("method ends");
        return cf;
    }
}
