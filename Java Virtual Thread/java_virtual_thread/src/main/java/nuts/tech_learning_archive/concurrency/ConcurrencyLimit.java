package nuts.tech_learning_archive.concurrency;

import nuts.tech_learning_archive.sec7.ExternalServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyLimit {
    private static final Logger log = LoggerFactory.getLogger(ConcurrencyLimit.class);

    public static void main(String[] args) {
        execute(Executors.newFixedThreadPool(3), 20);
    }

    private static void execute(ExecutorService executor, int taskCount) {

        try (executor) {
            for (int i = 1; i <= taskCount; i++) {
                int j = i;
                executor.submit(() -> printProductInfo(j));
            }
        }
    }

    private static void printProductInfo(int id) {
        log.info("{} => {}", id, ExternalServiceClient.getProduct(id));
    }
}