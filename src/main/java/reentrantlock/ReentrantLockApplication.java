package reentrantlock;

import lombok.SneakyThrows;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockApplication {
    @SneakyThrows
    public static void main(String[] args) {
        Task task = new Task();
        Runnable firstThread = () -> task.firstThread();
        Runnable secondThread = () -> task.secondThread();
        Thread thread1 = new Thread(firstThread);
        Thread thread2 = new Thread(secondThread);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        task.showCounter();
    }
}

class Task {
    private int counter;
    private Lock lock = new ReentrantLock();

    private void increment() {
        for (int i = 0; i < 10_000; i++) {
            counter++;
        }
    }

    public void firstThread() {
        lock.lock();
        increment();
        lock.unlock();
    }

    public void secondThread() {
        lock.lock();
        increment();
        lock.unlock();
    }

    public void showCounter() {
        System.out.println("counter = " + counter);
    }
}
