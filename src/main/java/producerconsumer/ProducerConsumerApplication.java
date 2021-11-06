package producerconsumer;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ProducerConsumerApplication {
    public static void main(String[] args) {
        Queue<Integer> queue = new LinkedList<>();
        Object lock = new Object();
        Producer producer = new Producer(queue, lock);
        Consumer consumer = new Consumer(queue, lock);
        Runnable produce = producer::produce;
        Runnable consume = () -> consumer.consume();
        new Thread(produce).start();
        new Thread(consume).start();
    }
}

@AllArgsConstructor
class Producer {
    private final Queue<Integer> queue;
    private final Object lock;

    @SneakyThrows
    public void produce() {
        int value = 0;
        int limit = 10;
        Random random = new Random();
        while (true) {
            synchronized (lock) {
                while (queue.size() == limit) {
                    lock.wait();
                }
                System.out.println("Положили значение");
                queue.offer(value++);
                lock.notify();
            }
            Thread.sleep(random.nextInt(1000));
        }
    }
}

@AllArgsConstructor
class Consumer {
    private final Queue<Integer> queue;
    private final Object lock;

    @SneakyThrows
    public void consume() {
        Random random = new Random();
        while (true) {
            synchronized (lock) {
                while (queue.isEmpty()) {
                    lock.wait();
                }
                int value = queue.poll();
                System.out.println("Взяли значение " + value + ". Осталось: " + queue.size());
                lock.notify();
            }
            Thread.sleep(random.nextInt(3000));
        }
    }
}