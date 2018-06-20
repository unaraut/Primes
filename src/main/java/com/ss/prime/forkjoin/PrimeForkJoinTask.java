package com.ss.prime.forkjoin;

import com.ss.prime.Primes;

import java.util.concurrent.RecursiveTask;

/**
 * The fork join task for prime checking. The algorithm is simple trial division.
 *
 * It divides the range of divisors into 2 and creates smaller sub tasks for execution.
 *
 * If one of the recursive tasks finds that the candidate is composite, then we dont need to continue checking other divisors.
 * But ForkJoin tasks once fired cannot be cleanly recalled back.
 *
 * Hence a volatile flag is used to set completion by one of the fork join threeads.
 * The other threads see this flag as set and dont start computation.
 *
 * The downside of checking a volatile variable by multiple threads and stopping is better than several fork join tasks
 * continuing to execute till end.
 *
 */
public class PrimeForkJoinTask extends RecursiveTask<Boolean> {

    private static final long serialVersionUID = -1883115459616562727L;

    private final int candidate;
    private int from;
    private int to;

    PrimeForkJoinTask(int candidate, int from, int to) {
        this.candidate = candidate;
        this.from = from;
        this.to = to;
    }

    private boolean computeSingleThreaded() {
        //skip even number
        if((this.from & 1) == 0) {
            this.from=this.from+1;
        }
        for (int i = this.from; i<this.to ; i=i+2) {// iterate to skip even numbers
            if (candidate%i == 0) {
                Primes.complete(candidate);
                return false;
            }
        }
        return true;
    }

    @Override
    protected Boolean compute() {
        if(Primes.isComplete(candidate)) {
            return false;
        }
        int length = to - from ;
        if (length < Primes.computeDirectlyLength()) {
            return computeSingleThreaded();
        }

        int middle = (to+from)/2;
        PrimeForkJoinTask leftHalf = new PrimeForkJoinTask(candidate, from, middle);
        PrimeForkJoinTask rightHalf = new PrimeForkJoinTask(candidate, middle++, to);

        invokeAll(leftHalf, rightHalf);
        return !Primes.isComplete(candidate);
    }
}
