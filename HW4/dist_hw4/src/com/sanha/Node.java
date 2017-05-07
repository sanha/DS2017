package com.sanha;

/**
 * This class represents a node of software combining tree.
 */
public final class Node {
  private enum CStatus{
    IDLE, FIRST, SECOND, RESULT, ROOT
  };

  private boolean locked;
  private CStatus cStatus;
  private int firstValue, secondValue;
  private int result;
  private final Node parent;

  // Constructor for root node
  public Node(final int initialValue) {
    this.cStatus = CStatus.ROOT;
    this.locked = false;
    this.parent = null;
    this.result = initialValue;
  }

  // Constructor for non-root node
  public Node(final Node parent) {
    this.parent = parent;
    this.cStatus = CStatus.IDLE;
    locked = false;
    this.firstValue = 0;
    this.secondValue = 0;
    this.result = 0;
  }

  public synchronized boolean precombine() {
    try {
      while (locked) wait();
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    switch (cStatus) {
      case IDLE:
        cStatus = CStatus.FIRST;
        return true;
      case FIRST:
        locked = true;
        cStatus = CStatus.SECOND;
        return false;
      case ROOT:
        return false;
      default:
        throw new RuntimeException("unexpected Node state during pre-combining: " + cStatus);
    }
  }

  public synchronized int combine(final int combined) {
    try {
      while (locked) wait();
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
    locked = true;
    firstValue = combined;

    switch (cStatus) {
      case FIRST:
        return firstValue;
      case SECOND:
        return firstValue + secondValue;
      default:
        throw new RuntimeException("unexpected Node state during combining: " + cStatus);
    }
  }

  public synchronized int operation(final int combined) {
    switch (cStatus) {
      case ROOT:
        final int prior = result;
        result += combined;
        return prior;
      case SECOND:
        secondValue = combined;
        locked = false;
        notifyAll();
        try {
          while (cStatus != CStatus.RESULT) wait();
        } catch (final InterruptedException e) {
          e.printStackTrace();
        }
        locked = false;
        notifyAll();
        cStatus = CStatus.IDLE;
        return result;
      default:
        throw new RuntimeException("unexpected Node state during operation: " + cStatus);
    }
  }

  public synchronized void distribute(final int prior) {
    switch (cStatus) {
      case FIRST:
        cStatus = CStatus.IDLE;
        locked = false;
        break;
      case SECOND:
        result = prior + firstValue;
        cStatus = CStatus.RESULT;
        break;
      default:
        throw new RuntimeException("unexpected Node state during distribution: " + cStatus);
    }
    notifyAll();
  }

  public Node getParent() {
    return parent;
  }

  public int getResult() {
    return result;
  }
}
