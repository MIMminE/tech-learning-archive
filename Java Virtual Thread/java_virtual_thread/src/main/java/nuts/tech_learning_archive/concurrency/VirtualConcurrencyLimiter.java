package nuts.tech_learning_archive.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;

public class VirtualConcurrencyLimiter implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(VirtualConcurrencyLimiter.class);

    private final ExecutorService executor;
    private final Semaphore semaphore;
    private final Queue<Callable<?>> queue;

    public VirtualConcurrencyLimiter(ExecutorService executor, int limit) {
        this.executor = executor;
        this.semaphore = new Semaphore(limit);
        this.queue = new ConcurrentLinkedDeque<>();
    }

    public <T> Future<T> submit(Callable<T> callable) {
        this.queue.add(callable);
        return executor.submit(() -> executeTask());
    }

    private <T> T executeTask() {
        try {
            semaphore.acquire();
            return (T) this.queue.poll().call();
        } catch (Exception e){
            log.error(e.getMessage(), e);
        } finally{
            semaphore.release();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        this.executor.close();
    }
}
