package com.sanha;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

/**
 * Utility class for experiment.
 */
public final class ExperimentUtils {
  public enum SharedVariableType {
    ATOMIC, TREE
  };

  // Test throughput
  public static long throughputExperiment(final SharedVariableType type,
                                          final int threadNum,
                                          final int incrementNumPerThread,
                                          final int deltaPerIncrement) {
    final Long[] endTimes = new Long[threadNum];
    final ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
    final SharedVariable sharedVariable;
    long totalDuration = 0;

    try {
      switch (type) {
        case ATOMIC:
          sharedVariable = new AtomicIntegerSharedVariable(0);
          break;
        case TREE:
          sharedVariable = new CombiningTreeSharedVariable(0, 2 * threadNum);
          break;
        default:
          throw new RuntimeException("Invalid first arg: atomic or tree");
      }

      final Future[] futures = new Future[threadNum];

      final long startTime = System.nanoTime();

      // Run threads
      for (int itr = 0; itr < threadNum; itr++) {
        final int threadId = itr;
        futures[itr] = executorService.submit(new Runnable() {
          public void run() {
            try {
              for (int i = 0; i < incrementNumPerThread; i++) {
                sharedVariable.getAndAdd(threadId, deltaPerIncrement);
              }
              endTimes[threadId] = System.nanoTime();
            } catch (final Exception e) {
              e.printStackTrace();
            }
          }
        });
      }

      long lastEndTime = Long.MIN_VALUE;
      for (int i = 0; i < threadNum; i++) {
        final Future future = futures[i];
        while (!future.isDone()) {
          sleep(1000);
        }
        final long endTimeOfFuture = endTimes[i];
        if (lastEndTime < endTimeOfFuture) {
          lastEndTime = endTimeOfFuture;
        }
      }
      totalDuration = lastEndTime - startTime;

      System.out.println("Final result: " + sharedVariable.get() + ", consumed " + totalDuration + " ns.");
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      executorService.shutdown();
    }

    return totalDuration;
  }

  // Test latency
  public static long latencyExperiment(final SharedVariableType type,
                                       final int threadNum,
                                       final int incrementNumPerThread,
                                       final int deltaPerIncrement) {
    final ArrayList<LinkedList<Long>> latencies = new ArrayList<>(threadNum);
    final ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
    final SharedVariable sharedVariable;
    long avgLatency = 0;

    try {
      switch (type) {
        case ATOMIC:
          sharedVariable = new AtomicIntegerSharedVariable(0);
          break;
        case TREE:
          sharedVariable = new CombiningTreeSharedVariable(0, 2 * threadNum);
          break;
        default:
          throw new RuntimeException("Invalid first arg: atomic or tree");
      }

      final Future[] futures = new Future[threadNum];

      // Run threads
      for (int itr = 0; itr < threadNum; itr++) {
        final int threadId = itr;
        final LinkedList<Long> latencyInThread = new LinkedList<>();
        latencies.add(latencyInThread);
        futures[itr] = executorService.submit(new Runnable() {
          public void run() {
            try {
              for (int i = 0; i < incrementNumPerThread; i++) {
                if (i % 100 == 0) {
                  final long startTime = System.nanoTime();
                  sharedVariable.getAndAdd(threadId, deltaPerIncrement);
                  latencyInThread.add(System.nanoTime() - startTime);
                }
              }
            } catch (final Exception e) {
              e.printStackTrace();
            }
          }
        });
      }

      long latencyAccum = 0;
      for (int i = 0; i < threadNum; i++) {
        final Future future = futures[i];
        while (!future.isDone()) {
          sleep(1000);
        }
        final LinkedList<Long> latencyListInThread = latencies.get(i);
        long latencyAccumInThread = 0;
        for (final long latency : latencyListInThread) {
          latencyAccumInThread += latency;
        }
        latencyAccum += latencyAccumInThread / latencyListInThread.size();
      }
      avgLatency = latencyAccum / threadNum;

      System.out.println("Final result: " + sharedVariable.get() + ", avgLatency " + avgLatency + " ns.");
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      executorService.shutdown();
    }

    return avgLatency;
  }
}
