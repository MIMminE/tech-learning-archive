package nuts.tech_learning_archive.sec8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class SupplyAsync {

    private static final Logger log = LoggerFactory.getLogger(SupplyAsync.class);

    public static void main(String[] args) throws InterruptedException {

        log.info("main starts");
        var cf = slowTask();
        cf.thenAccept(v -> log.info("value = {}", v));

        log.info("main ends");

        Thread.sleep(Duration.ofSeconds(2));
    }


    private static CompletableFuture<String> slowTask() {
        log.info("method starts");
        var cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "hi";
        }, Executors.newVirtualThreadPerTaskExecutor());
        log.info("method ends");
        return cf;
    }
}
