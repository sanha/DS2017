package com.sanha;

/**
 * An interface for shared (integer) variable.
 */
public interface SharedVariable {

  int getAndIncrement(int threadId);

  int getAndAdd(int threadId,
                int delta);

  int get();
}
