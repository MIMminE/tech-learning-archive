package nuts.tech_learning_archive.aggregator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;


public class AggregatorDemo {
    private static final Logger log = LoggerFactory.getLogger(AggregatorDemo.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        AggregatorService aggregatorService = new AggregatorService(executorService);

        List<Future<ProductDto>> futureList = IntStream.range(1, 50)
                .mapToObj(id -> executorService.submit(() -> aggregatorService.getProductDto(id))).toList();

        futureList.forEach(future -> {
            try {
                log.info("product= {}", future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


    }
}
