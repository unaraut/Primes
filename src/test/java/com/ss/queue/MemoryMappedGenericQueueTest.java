package com.ss.queue;

import com.ss.queue.messages.MemoryMappedMessage;
import com.ss.queue.messages.PrimeMessage;
import com.ss.queue.messages.ResultMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MemoryMappedGenericQueueTest
{
    private File file;
    private static final String filePath = "./test-unit-message";

    @Before
    public void setup() throws Exception
    {
        file = new File(filePath);
        file.delete();
    }

    @Test(timeout = 1000L)
    public void testOfferAndPoll() throws Exception {

        try (MemoryMappedGenericQueue<PrimeMessage> queue = new MemoryMappedGenericQueue<>(filePath,
                                                                                            2,
                                                                                            PrimeMessage.class))
        {
            queue.offer(new PrimeMessage(10));
            while (true) {
                MemoryMappedMessage result = queue.poll();
                if (result != null) {
                    Assert.assertSame(10, result.getValue());
                    break;
                }
            }
        }
    }

    @Test(timeout = 1000L)
    public void testOfferAndPollResultMsg() throws Exception {

        try (MemoryMappedGenericQueue<ResultMessage> queue = new MemoryMappedGenericQueue<>(filePath,
                2,
                ResultMessage.class))
        {
            queue.offer(new ResultMessage(10, (byte) 0));
            while (true) {
                ResultMessage result = (ResultMessage)queue.poll();
                if (result != null) {
                    Assert.assertSame(10, result.getValue());
                    Assert.assertFalse(result.isPrime());
                    break;
                }
            }
        }
    }

    @Test(timeout = 2000L)
    public void testMultipleOfferAndPoll() throws Exception {
        try (MemoryMappedGenericQueue<PrimeMessage> queue = new MemoryMappedGenericQueue(filePath, 2,PrimeMessage.class)) {
            List<Integer> inputs = Arrays.asList(10, -1, 0, 2, Integer.MIN_VALUE, 200, Integer.MAX_VALUE);
            inputs.stream().map(i -> new PrimeMessage(i)).forEach(queue::offer);
            pollAndAssertResults(queue, inputs);
        }
    }

    @Test(timeout = 2000L)
    public void testMultipleOfferAndPollResultMsg() throws Exception {
        try (MemoryMappedGenericQueue<ResultMessage> queue = new MemoryMappedGenericQueue(filePath, 2,ResultMessage.class)) {
            List<Integer> inputs = Arrays.asList(10, -1, 0, 2, Integer.MIN_VALUE, 200, Integer.MAX_VALUE);
            List<Byte> results = Arrays.asList((byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)1);
            List<Boolean> expectedOutput = Arrays.asList(false, false, false, false, false, false, true);
            for(int i=0;i<inputs.size();i++) {
                queue.offer(new ResultMessage(inputs.get(i), results.get(i)));
            }
            pollAndAssertResults(queue, inputs, expectedOutput);
        }
    }

    @Test(timeout = 2000L)
    public void testOfferAndPollRandom() throws Exception {

        try (MemoryMappedGenericQueue<PrimeMessage> queue = new MemoryMappedGenericQueue(filePath, 2,PrimeMessage.class))
        {
            Random random = new Random();
            List<Integer> inputs = getRandomInputs(random);
            inputs.stream().map(i -> new PrimeMessage(i)).forEach(queue::offer);
            pollAndAssertResults(queue, inputs);
        }
    }

    private List<Integer> getRandomInputs(Random random) {
        return Stream.generate(() -> random.nextInt(Integer.MAX_VALUE)).limit(1_000).collect(toList());
    }

    private void pollAndAssertResults(MemoryMappedGenericQueue queue, List<Integer> inputs) {
        List<Integer> results = new ArrayList<>();
        do {
            MemoryMappedMessage result = queue.poll();
            if(result != null) {
                results.add(result.getValue());
            }
        }while(results.size() < inputs.size());
        Assert.assertEquals(inputs, results);
    }

    private void pollAndAssertResults(MemoryMappedGenericQueue queue, List<Integer> inputs, List<Boolean> expectedPrime) {
        List<Integer> results = new ArrayList<>();
        List<Boolean> isPrimesResult = new ArrayList<>();
        do {
            ResultMessage result = (ResultMessage)queue.poll();
            if(result != null) {
                results.add(result.getValue());
                isPrimesResult.add(result.isPrime());
            }
        }while(results.size() < inputs.size());
        Assert.assertEquals(inputs, results);
        Assert.assertEquals(expectedPrime, isPrimesResult);
    }

    @After
    public void tearDown()
    {
        file.delete();
    }
}
