package com.sanha;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

/**
 * Dist hw 4 - software combining tree.
 */
public class Main {

  public static void main(final String[] args) {

    // Args check
    if (args.length != 4) {
      throw new RuntimeException("Invalid arguments. First arg: atomic / tree, second arg: thread num, " +
          "third arg: # of increments per thread, fourth arg: delta per increment");
    }

    final String sharedVariableType = args[0];
    final int threadNum = Integer.valueOf(args[1]);
    final int incrementNumPerThread = Integer.valueOf(args[2]);
    final int deltaPerIncrement = Integer.valueOf(args[3]);
    final Long[] endTimes = new Long[threadNum];

    final ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
    final SharedVariable sharedVariable;

    try {
      if (sharedVariableType.equals("atomic")) {
        sharedVariable = new AtomicIntegerSharedVariable(0);
      } else if (sharedVariableType.equals("tree")) {
        sharedVariable = new CombiningTreeSharedVariable(0, 2 * threadNum);
      } else {
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

      System.out.println("Final result: " + sharedVariable.get() + ", consumed " + (lastEndTime - startTime) + " ns.");
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      executorService.shutdown();
    }
  }
}
