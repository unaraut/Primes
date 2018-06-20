package com.ss.random;

import com.ss.queue.messages.MemoryMappedMessage;
import com.ss.queue.*;
import com.ss.queue.messages.PrimeMessage;
import com.ss.queue.messages.ResultMessage;

import java.io.File;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Randomizer app which sends random integers to a memory mapped request queue. *
 * It reads responses from another memory backed reply queue.
 *
 * The queueSizeInMB denotes the queue size and it should be the same value in the Primes app.
 *
 * Start the Prime app before starting the Randomizer app.
 *
 * If running on windows delete the files "prime-queue" and "result-queue" before each run as File.deleteOnExit
 * doesnt work reliably.
 *
 * >rm prime-queue result-queue
 */
public class Randomizer {
    private final Random random;
    private final int bound = Integer.MAX_VALUE;
    private final ExecutorService executorService;
    private final String primeQ;
    private final String replyQ;
    private final int queueSizeInMB = 20;

    public Randomizer() {
        this.primeQ = "prime-queue";
        this.replyQ = "result-queue";
        this.random = new Random();
        executorService = Executors.newSingleThreadExecutor();
        cleanupOnExit(primeQ, replyQ);
    }

    private void cleanupOnExit(String requestQ, String replyQ) {
        File fIn = new File(requestQ);
        File fOut = new File(replyQ);
        fIn.deleteOnExit();
        fOut.deleteOnExit();
    }

    public static void main(String[] args) throws Exception {
        Randomizer randomizer = new Randomizer();
        randomizer.startReceivingResults();
        randomizer.sendRandom();
    }

    private void startReceivingResults() {
        executorService.execute(getResult());
    }

    private final Runnable getResult() {
        return () -> {
            try(MemoryMappedGenericQueue replyQueue = new MemoryMappedGenericQueue(replyQ, queueSizeInMB, ResultMessage.class)) {
                while (true) {
                    MemoryMappedMessage msg = replyQueue.poll();
                    if (msg != null) {
                        ResultMessage resultMessage = (ResultMessage) msg;
                        System.out.printf("Received %d is %s\n", resultMessage.getValue(),
                                (resultMessage.isPrime() ? "prime" : "composite"));
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        };
    }

    private void sendRandom() throws Exception {
        boolean stop = false;
        int counter=0;
        long time = System.currentTimeMillis();
        try(MemoryMappedGenericQueue queue = new MemoryMappedGenericQueue(primeQ, queueSizeInMB, PrimeMessage.class))
        {
            while (!stop) {
                int next = random.nextInt(bound);
                queue.offer(new PrimeMessage(next));
                counter++;
                System.out.printf("Sent %d\n", next);
                if (counter == 1000) {
                    stop = true;
                }
//                Thread.sleep(1);
            }
        }
        System.out.printf("Finished prime checking for : %d messages in %d (ms)\n", counter, (System.currentTimeMillis()-time));
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);
    }
}
