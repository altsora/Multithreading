package threadpool;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolApplication {
    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
            executorService.submit(new Work(i));
        }

        executorService.shutdown(); // Чтобы сервис больше не ожидал задач
        executorService.awaitTermination(1, TimeUnit.DAYS); // Ждём выполнение всех задач, main засыпает
    }
}
