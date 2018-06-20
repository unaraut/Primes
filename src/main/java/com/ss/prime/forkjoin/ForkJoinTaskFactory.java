package com.ss.prime.forkjoin;

public class ForkJoinTaskFactory {

    public static PrimeForkJoinTask create(int candidate)
    {
        return new PrimeForkJoinTask(candidate, 5, (int) Math.sqrt((double) candidate));
    }
}
