package interrupt;

import lombok.SneakyThrows;

import java.util.Random;

public class InterruptApplication {
    @SneakyThrows
    public static void main(String[] args) {
        Runnable task = () -> {
            Random random = new Random();
            for (int i = 0; i < 1_000_000_000; i++) {
                // Будет InterruptedException, если поток прервётся во время сна
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    System.err.println("Поток " + Thread.currentThread().getName() + " прерван во время сна");
//                    break;
//                }

                boolean interrupted = Thread.currentThread().isInterrupted();
                System.out.println(i + ", " + interrupted);
                if (interrupted) {
                    System.out.println("Поток " + Thread.currentThread().getName() + " культурно прерван");
                    break;
                }
                Math.sin(random.nextDouble());
            }
        };

        Thread thread = new Thread(task);
        System.out.println("Начал вычисление");
        thread.start();
        Thread.sleep(500);
        thread.interrupt();
//        thread.join(); // на interrupt нет остановки, поэтому при необходимости ждём через join
        System.out.println("Закончил вычисление");
    }
}
