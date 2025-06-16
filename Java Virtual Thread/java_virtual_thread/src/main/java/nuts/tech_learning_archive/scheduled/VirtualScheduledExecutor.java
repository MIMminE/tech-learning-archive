package nuts.tech_learning_archive.scheduled;


import nuts.tech_learning_archive.sec7.ExternalServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VirtualScheduledExecutor {

    private static final Logger log = LoggerFactory.getLogger(VirtualScheduledExecutor.class);

    public static void scheduled() {
        try (var executorService = Executors.newSingleThreadScheduledExecutor();
             var virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            executorService.scheduleAtFixedRate(() -> {
                virtualExecutor.execute(() -> printProductInfo(1));
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    private static void printProductInfo(int id) {
        log.info("{} => {}", id, ExternalServiceClient.getProduct(id));
    }
}
