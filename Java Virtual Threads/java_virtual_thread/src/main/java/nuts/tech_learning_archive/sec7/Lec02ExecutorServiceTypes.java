package nuts.tech_learning_archive.sec7;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Lec02ExecutorServiceTypes {

    private static final Logger log = Logger.getLogger(Lec02ExecutorServiceTypes.class.getName());

    public static void main(String[] args) {
        execute(Executors.newSingleThreadExecutor(), 3);
        execute(Executors.newFixedThreadPool(2), 3);
        execute(Executors.newCachedThreadPool(), 3);
        execute(Executors.newVirtualThreadPerTaskExecutor(), 3);
    }

    private static void execute(ExecutorService executor, int taskCount) {
        try (executor) {
            for (int i = 0; i < taskCount; i++) {
                int j = i;
                executor.submit(() -> ioTask(j));
            }
        }
    }

    private static void ioTask(int i) {
        log.info(i + "");
        try {
            Thread.sleep(Duration.ofSeconds(i));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
