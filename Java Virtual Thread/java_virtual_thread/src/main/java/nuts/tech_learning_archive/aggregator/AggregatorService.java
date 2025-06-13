package nuts.tech_learning_archive.aggregator;

import nuts.tech_learning_archive.sec7.ExternalServiceClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AggregatorService {

    private final ExecutorService executor;

    public AggregatorService(ExecutorService executor) {
        this.executor = executor;
    }

    public ProductDto getProductDto(int id) throws ExecutionException, InterruptedException {
        var product = executor.submit(() -> ExternalServiceClient.getProduct(id));
        var rating = executor.submit(() -> ExternalServiceClient.getRating(id));
        return new ProductDto(id, product.get(), rating.get());
    }
}
