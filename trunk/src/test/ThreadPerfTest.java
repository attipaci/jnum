package test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPerfTest {

    public static void main(String[] args) {
        try { new ThreadPerfTest().test(); }
        catch(InterruptedException e) {}
    }
    
    public void test() throws InterruptedException {
        final BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
    
        Runnable task = new Runnable() {
            @Override
            public void run() {
                queue.add(1);
            }
        };

        for (int t = 0; t < 3; t++) {
            {
                long start = System.nanoTime();
                int runs = 20000;
                for (int i = 0; i < runs; i++)
                    new Thread(task).start();
                for (int i = 0; i < runs; i++)
                    queue.take();
                long time = System.nanoTime() - start;
                System.out.printf("Time for a task to complete in a new Thread %.1f us%n", time / runs / 1000.0);
            }
            {
                int threads = Runtime.getRuntime().availableProcessors();
                ExecutorService es = Executors.newFixedThreadPool(threads);
                long start = System.nanoTime();
                int runs = 200000;
                for (int i = 0; i < runs; i++)
                    es.execute(task);
                for (int i = 0; i < runs; i++)
                    queue.take();
                long time = System.nanoTime() - start;
                System.out.printf("Time for a task to complete in a thread pool %.2f us%n", time / runs / 1000.0);
                es.shutdown();
            }
            {
                long start = System.nanoTime();
                int runs = 200000;
                for (int i = 0; i < runs; i++)
                    task.run();
                for (int i = 0; i < runs; i++)
                    queue.take();
                long time = System.nanoTime() - start;
                System.out.printf("Time for a task to complete in the same thread %.2f us%n", time / runs / 1000.0);
            }
        }
        
    }

}
