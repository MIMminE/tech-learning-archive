package nuts.tech_learning_archive.concurrency;

import nuts.tech_learning_archive.sec7.ExternalServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyLimitWithSemaphore {

    private static final Logger log = LoggerFactory.getLogger(ConcurrencyLimitWithSemaphore.class);

    public static void main(String[] args) throws Exception {
        var virtualConcurrencyLimiter = new VirtualConcurrencyLimiter(Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory()), 3);
        execute(virtualConcurrencyLimiter, 20);
    }

    private static void execute(VirtualConcurrencyLimiter concurrencyLimiter, int taskCount) throws Exception {
        try (concurrencyLimiter) {
            for (int i = 1; i <= taskCount; i++) {
                int j = i;
                concurrencyLimiter.submit(() -> printProductInfo(j));
            }
            log.info("submitted");
        }
    }

    private static String printProductInfo(int id) {
        String product
                = ExternalServiceClient.getProduct(id);
        log.info("{} => {}", id, product);
        return product;
    }
}
