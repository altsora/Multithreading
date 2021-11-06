package deadlock;

import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLockApplication {
    @SneakyThrows
    public static void main(String[] args) {
        Bank bank = new Bank();
        Thread thread1 = new Thread(bank::firstThread);
        Thread thread2 = new Thread(bank::secondThread);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        bank.finished();
    }
}

class Bank {
    private final Account account1 = new Account();
    private final Account account2 = new Account();
    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();
    private final Random random = new Random();

    /**
     * Предотвращение dead lock
     * @param lock1
     * @param lock2
     */
    @SneakyThrows
    private void takeLocks(Lock lock1, Lock lock2) {
        boolean firstLockTaken = false;
        boolean secondLockTaken = false;
        while (true) {
            try {
                // Поток пробует занять оба монитора
                firstLockTaken = lock1.tryLock();
                secondLockTaken = lock2.tryLock();
            } finally {
                // Если один монитор занять удалось, а второй нет,
                // то отдаём занятый монитор другому потоку:
                // CurrentThread: (lock1), Thread2: (lock2) ->
                // -> CurrentThread: (), Thread2: (lock2, lock1)
                if (firstLockTaken && !secondLockTaken) {
                    lock1.unlock();
                }

                if (!firstLockTaken && secondLockTaken) {
                    lock2.unlock();
                }
            }
            // Если поток занял оба монитора, то успех и выходим
            // Второй поток ждёт, пока мониторы освободятся
            if (firstLockTaken && secondLockTaken) return;
        }
    }

    public void firstThread() {
        for (int i = 0; i < 10_000; i++) {
            takeLocks(lock1, lock2);
            try {
                Account.transfer(account1, account2, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    public void secondThread() {
        for (int i = 0; i < 10_000; i++) {
            takeLocks(lock2, lock1);
            try {
                Account.transfer(account2, account1, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    /**
     * В случае ручной синхронизации важен порядок локов: должен быть одинаковым, иначе возможен dead lock
     */
    public void firstThreadLock() {
        for (int i = 0; i < 10_000; i++) {
            lock1.lock();
            lock2.lock();
            try {
                Account.transfer(account1, account2, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    /**
     * В случае ручной синхронизации важен порядок локов: должен быть одинаковым, иначе возможен dead lock
     */
    public void secondThreadLock() {
        for (int i = 0; i < 10_000; i++) {
            lock1.lock();
            lock2.lock();
            try {
                Account.transfer(account2, account1, random.nextInt(100));
            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }
    }

    public void finished() {
        System.out.println("Account 1: " + account1.getBalance());
        System.out.println("Account 2: " + account2.getBalance());
        System.out.println("Total balance: " + (account1.getBalance() + account2.getBalance()));
    }
}

@Getter
class Account {
    private int balance = 10_000;

    public void deposit(int amount) {balance += amount;}

    public void withDraw(int amount) {balance -= amount;}

    public static void transfer(Account from, Account to, int amount) {
        from.withDraw(amount);
        to.deposit(amount);
    }
}