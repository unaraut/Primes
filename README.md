# Primes

### Primes.java

Application which receives requests of integers from a non durable memory mapped queue. See MemoryMappedGenericQueue
It checks if the number is prime and sends the reply on reply queue. 

The producer (Randomizer) is not slowed down if consumer(Prime) cant keep up. Prime maintains a workQueue to buffer work.
This uses a LinkedBlockingQueue but can be changed to a bounded RingBuffer to drop older messages. eg impl below.

```java
public class ArrayRingBuffer<T> extends AbstractQueue<T>
{
    private final ArrayBlockingQueue<T> backingArray;   

    @Override
    public boolean offer(T t)
    {

        for (;;)
        {
            if (!backingArray.offer(t)) {
                backingArray.poll();
                continue;
            }
            return true;
        }
    }
    ....
 }
```
GC logs generated after processing 150k messages in few secs are hosted here:
http://gceasy.io/my-gc-report.jsp?p=c2hhcmVkLzIwMTgvMDYvMTkvLS1nY2xvZy5sb2cuMC5jdXJyZW50LS0yMi01Ni00MQ==

The important parameter is that Prime app creates new objects at a very slow rate.
***Avg creation rate 	2.96 mb/sec***

JVM Parameters to use:
-XX:+UseG1GC -Xloggc:gclog.log -Xmx1g -Xms1g -XX:+DisableExplicitGC -XX:NumberOfGCLogFiles=5 -XX:+PrintGC -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseGCLogFileRotation -XX:-UseLargePagesIndividualAllocation


### Randomizer.java

Randomizer app which sends random integers to a memory mapped request queue.
It reads responses from another memory backed reply queue.


### MemoryMappedGenericQueue.java

A low latency off-heap persistent queue implementation based on memory cached files. Inspired by HFT Chronicle Queue.
It is a simple non blocking queue bounded by the file size on disk.


 **Start the Prime app before starting the Randomizer app.**
 
 **If running on windows delete the files "prime-queue" and "result-queue" before each run as File.deleteOnExit
 doesnt work reliably.**
 
