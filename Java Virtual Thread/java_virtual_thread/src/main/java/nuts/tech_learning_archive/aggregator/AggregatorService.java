package nuts.tech_learning_archive.aggregator;

import nuts.tech_learning_archive.sec7.ExternalServiceClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class AggregatorService {

    private final ExecutorService executor;

    public AggregatorService(ExecutorService executor) {
        this.executor = executor;
    }

    public ProductDto getProductDto(int id) throws ExecutionException, InterruptedException {
//        var product = executor.submit(() -> ExternalServiceClient.getProduct(id));
        var product = CompletableFuture.supplyAsync(() -> ExternalServiceClient.getProduct(id), executor)
                .exceptionally(ex -> "product-not-found");


//        var rating = executor.submit(() -> ExternalServiceClient.getRating(id));
        var rating = CompletableFuture.supplyAsync(() -> ExternalServiceClient.getRating(id), executor)
                .exceptionally(ex -> 1)
                .orTimeout(750, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> 2);

        return new ProductDto(id, product.join(), rating.join());
    }
}
