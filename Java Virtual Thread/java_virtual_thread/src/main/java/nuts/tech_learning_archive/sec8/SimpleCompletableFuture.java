package nuts.tech_learning_archive.sec8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class SimpleCompletableFuture {

    private static final Logger log = LoggerFactory.getLogger(SimpleCompletableFuture.class);

    public static void main(String[] args) {
        log.info("main starts");
        var cf = fastTask();
//        cf.thenAccept(System.out::println);

        log.info("value = {}", cf.join());
        log.info("main ends");
    }

    private static CompletableFuture<String> fastTask() {
        log.info("method starts");
        var cf = new CompletableFuture<String>();
//        Thread.ofVirtual().start(() -> {
//            try {
//                Thread.sleep(Duration.ofMillis(1000));
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            log.info("virtual end");
//            cf.complete("hi");
//        });
        log.info("method ends");
        return cf;
    }
}
