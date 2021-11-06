package countdownlatch;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchApplication {
    public static void main(String[] args) {
        countDownMain();
        countDownThread();
    }

    @SneakyThrows
    private static void countDownMain() {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executorService.submit(new Processor1(countDownLatch));
        }

        executorService.shutdown();
        countDownLatch.await(); // main ждёт, пока защёлка откроется
        System.out.println("Зашёлка была открыта");
    }

    @SneakyThrows
    private static void countDownThread() {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executorService.submit(new Processor2(i, countDownLatch));
        }
        executorService.shutdown();
        for (int i = 0; i < 3; i++) {
            Thread.sleep(1000);
            countDownLatch.countDown();
            System.out.println("Открываем защёлку " + (i + 1));
        }
    }
}

@AllArgsConstructor
class Processor1 implements Runnable {
    private final CountDownLatch countDownLatch;

    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(1000);
        countDownLatch.countDown();
        System.out.println("Поток " + Thread.currentThread().getName() + " открывает одну защёлку");
    }
}

@AllArgsConstructor
class Processor2 implements Runnable {
    private final int id;
    private final CountDownLatch countDownLatch;

    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(3000);
        countDownLatch.await(); // Поток ждёт, пока защёлка откроется
        System.out.println("Защёлка открыта для процесса " + id);
    }
}


