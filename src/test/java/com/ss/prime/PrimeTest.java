package com.ss.prime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class PrimeTest {

    @Before
    public void setup()
    {
        File file = new File("test-unit1");
        file.delete();
    }

    @Test(timeout=1000L)
    public void testSingle() throws Exception {
        Primes primes = new Primes("test-unit1", "result-test-unit1");
        long time = System.currentTimeMillis();
        //632658701 is composite
        int candidate =  8501*19309;//525702829;//632658701;//composite
        boolean isPrime = primes.testPrime(candidate);
        Assert.assertFalse(isPrime);
        System.out.printf("Time taken: %d\n" ,(System.currentTimeMillis()-time)/4);
    }

    @Test(timeout=1000L)
    public void testSmall() throws Exception {
        Primes primes = new Primes("test-unit1", "result-test-unit1");
        long time = System.currentTimeMillis();
        //632658701 is composite
        int[] candidates = new int[]{ 633910111,633910117,633910163,633910177,633910181,633910183,633910187,633910201,633910241,633910261};
        for (int candidate: candidates)
        {
            boolean isPrime = primes.testPrime(candidate);
            Assert.assertTrue(isPrime);

            isPrime = primes.testPrime(candidate+3);
            Assert.assertFalse(isPrime);
        }
        System.out.printf("Time taken: %d\n" ,(System.currentTimeMillis()-time)/4);
    }

    @Test(timeout=1000L)
    public void testLargeNumber() throws Exception {
        Primes primes = new Primes("test-unit1", "result-test-unit1");
        int candidate= Integer.MAX_VALUE;
        boolean isPrime = primes.testPrime(candidate);
        Assert.assertTrue(isPrime);
    }

    @Test(timeout=1000L)
    public void testEdgeCases() throws Exception {
        Primes primes = new Primes("test-unit1", "result-test-unit1");
        int[] candidates = new int[]{ 0,-1,Integer.MIN_VALUE,1};
        for (int candidate: candidates)
        {
            boolean isPrime = primes.testPrime(candidate);
            Assert.assertFalse(isPrime);
        }
    }
}
