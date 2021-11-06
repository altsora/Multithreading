package semaphore;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreApplication {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        Connection connection = Connection.getConnection();
        Runnable task = () -> connection.publicWork();
        for (int i = 0; i < 50; i++) {
            executorService.submit(task);
        }

        executorService.shutdown();
    }
}

class Connection {
    private static Connection connection = new Connection();
    private final Lock lock = new ReentrantLock();
    private Semaphore semaphore = new Semaphore(10);
    private int connectionCount;

    private Connection() {}

    public static Connection getConnection() {return connection;}

    @SneakyThrows
    public void publicWork() {
        semaphore.acquire();
        privateWork();
        semaphore.release();
    }

    @SneakyThrows
    private void privateWork() {
        lock.lock();
        System.out.println(connectionCount++ + " ++ -> " + connectionCount);
        lock.unlock();

        Thread.sleep(5000);

        lock.lock();
        System.out.println(connectionCount-- + " -- -> " + connectionCount);
        lock.unlock();
    }
}
