package com.ss.queue.messages;

import sun.misc.Unsafe;

public class ResultMessage implements MemoryMappedMessage {

    private final int candidate;
    private final byte isPrime;
    private static final byte IS_PRIME_FLAG = 1;

    public ResultMessage(int candidate, byte isPrime) {
        this.candidate = candidate;
        this.isPrime = isPrime;
    }

    @Override
    public Integer getValue() {
        return candidate;
    }

    @Override
    public int messageLength() {
        return 5; // integer + boolean
    }

    @Override
    public void writeMessage(Unsafe unsafe, long mem, long offset) {
        unsafe.putInt(mem+offset, candidate);
        unsafe.putByte(mem+offset+8, isPrime);
    }

    public boolean isPrime() {
        return IS_PRIME_FLAG == isPrime;
    }
}
