package at.fhv.transflow.simulation.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Very simple wrapper class around a given {@link ExecutorService}, which enables a thread to wait for
 * the completion of all currently running tasks without the need to shut down the executor to await its
 * termination. This ensures that an ExecutorService can be reused instead of tearing down the thread pool
 * whenever task completion has to be awaited.<br>
 * An {@link AwaitableExecutor} can be reused just like a normal executor service, and it will always notify
 * and awaiting thread as soon as its thread pool sits completely idle for a moment.
 */
public class AwaitableExecutor implements AutoCloseable {
    private final ExecutorService executor;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Lock lock = new ReentrantLock();
    private final Condition allTasksCompleted;
    private boolean started = false;

    public AwaitableExecutor(ExecutorService executor) {
        this.executor = executor;
        allTasksCompleted = lock.newCondition();
    }

    public void execute(Runnable task) {
        counter.incrementAndGet();
        started = true;

        executor.execute(() -> {
            task.run();
            decrementCounter();
        });
    }

    /**
     * Awaits the completion of all threads running within this {@link ExecutorService}. Blocks the calling
     * thread until the next time the thread pool of this executor sits completely idle (i.e. every submitted
     * task has finished execution).
     * @throws InterruptedException when the blocked thread is unexpectedly interrupted.
     */
    public void awaitCompletion() throws InterruptedException {
        lock.lock();
        try {
            while (!started || counter.get() > 0) {
                allTasksCompleted.await();
            }
        } finally {
            lock.unlock();
        }
    }

    private void decrementCounter() {
        lock.lock();
        try {
            if (started && counter.decrementAndGet() <= 0) {
                allTasksCompleted.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        counter.set(0);
        started = false;
    }

    @Override
    public void close() {
        executor.close();
    }
}