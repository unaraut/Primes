# Primes

### Primes.java

Application which receives requests of integers from a non durable memory mapped queue. See MemoryMappedGenericQueue
It checks if the number is prime and sends the reply on reply queue. 

GC logs generated after processing 150k messages in few secs are hosted here:
http://gceasy.io/my-gc-report.jsp?p=c2hhcmVkLzIwMTgvMDYvMTkvLS1nY2xvZy5sb2cuMC5jdXJyZW50LS0yMi01Ni00MQ==

JVM Parameters to use:
-XX:+UseG1GC -Xloggc:gclog.log -Xmx1g -Xms1g -XX:+DisableExplicitGC -XX:NumberOfGCLogFiles=5 -XX:+PrintGC -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseGCLogFileRotation -XX:-UseLargePagesIndividualAllocation


### Randomizer.java

Randomizer app which sends random integers to a memory mapped request queue.
It reads responses from another memory backed reply queue.


### MemoryMappedGenericQueue.java

A low latency off-heap persistent queue implementation based on memory cached files. Inspired by HFT Chronicle Queue.

 **Start the Prime app before starting the Randomizer app.**
 
 **If running on windows delete the files "prime-queue" and "result-queue" before each run as File.deleteOnExit
 doesnt work reliably.**
 
