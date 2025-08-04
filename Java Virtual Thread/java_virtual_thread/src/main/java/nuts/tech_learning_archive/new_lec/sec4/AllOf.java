package nuts.tech_learning_archive.new_lec.sec4;

import nuts.tech_learning_archive.aggregator.AggregatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class AllOf {

    private static final Logger log = LoggerFactory.getLogger(AllOf.class);

    public static void main(String[] args) {

        var executor = Executors.newVirtualThreadPerTaskExecutor();
        var aggregator = new AggregatorService(executor);

        var futures = IntStream.rangeClosed(1, 50)
                .mapToObj(id -> CompletableFuture.supplyAsync(() -> aggregator.getProductDto(id), executor)).toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        var list = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        log.info("list : {}", list);
    }
}
