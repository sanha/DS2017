package sanha;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A shared variable implemented with Java AtomicInteger.
 */
public final class AtomicIntegerSharedVariable implements SharedVariable {

  private final AtomicInteger atomicInteger;

  public AtomicIntegerSharedVariable(final int initialValue) {
    this.atomicInteger = new AtomicInteger(initialValue);
  }

  @Override
  public int getAndIncrement(final int threadId) {
    return atomicInteger.getAndIncrement();
  }

  @Override
  public int getAndAdd(final int threadId,
                       final int delta) {
    return atomicInteger.getAndAdd(delta);
  }

  @Override
  public int get() {
    return atomicInteger.get();
  }
}
