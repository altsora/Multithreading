package threadpool;

import lombok.SneakyThrows;

public class Work implements Runnable {
    private final int id;

    public Work(int id) {
        this.id = id;
    }

    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(1000);
        System.out.println("Работа №" + id + " завершена");
    }
}
