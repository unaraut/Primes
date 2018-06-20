package com.ss.queue.messages;

import sun.misc.Unsafe;

public class PrimeMessage implements MemoryMappedMessage {

    private final int candidate;

    public PrimeMessage(int candidate) {
        this.candidate = candidate;
    }

    @Override
    public Integer getValue() {
        return candidate;
    }

    @Override
    public int messageLength() {
        return 4;// integer
    }

    @Override
    public void writeMessage(Unsafe unsafe, long mem, long offset) {
        unsafe.putInt(mem+offset, candidate);
    }


}
