package nuts.tech_learning_archive.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class VirtualConcurrencyLimiter implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(VirtualConcurrencyLimiter.class);

    private final ExecutorService executor;
    private final Semaphore semaphore;

    public VirtualConcurrencyLimiter(ExecutorService executor, int limit) {
        this.executor = executor;
        this.semaphore = new Semaphore(limit);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(() -> wrapCallable(callable));
    }

    private <T> T wrapCallable(Callable<T> callable) {
        try {
            semaphore.acquire();
            return callable.call();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            semaphore.release();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        this.executor.close();
    }
}
