package nuts.tech_learning_archive.sec7;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AccessResponseUsingFuture {
    private static final Logger log = LoggerFactory.getLogger(AccessResponseUsingFuture.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()){

            Future<String> product1 = executorService.submit(() -> ExternalServiceClient.getProduct(1));
            Future<String> product2 = executorService.submit(() -> ExternalServiceClient.getProduct(2));
            Future<String> product3 = executorService.submit(() -> ExternalServiceClient.getProduct(3));

            log.info("product-1 : {}", product1.get());
            log.info("product-2 : {}", product2.get());
            log.info("product-3 : {}", product3.get());

        }
    }
}
