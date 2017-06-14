package sanha.rtm;

/**
 * This class represents a node of software combining tree using Restricted Transactional Memory.
 */
public final class RtmNode {
  private enum CStatus{
    IDLE, FIRST, SECOND, RESULT, ROOT
  };

  private CStatus cStatus;
  private int firstValue, secondValue;
  private int result;
  private final RtmNode parent;

  // Constructor for root node
  public RtmNode(final int initialValue) {
    this.cStatus = CStatus.ROOT;
    this.parent = null;
    this.result = initialValue;
  }

  // Constructor for non-root node
  public RtmNode(final RtmNode parent) {
    this.parent = parent;
    this.cStatus = CStatus.IDLE;
    this.firstValue = 0;
    this.secondValue = 0;
    this.result = 0;
  }

  public boolean precombine() {

    switch (cStatus) {
      case IDLE:
        cStatus = CStatus.FIRST;
        return true;
      case FIRST:
        cStatus = CStatus.SECOND;
        return false;
      case ROOT:
        return false;
      default:
        throw new RuntimeException("unexpected RtmNode state during pre-combining: " + cStatus);
    }
  }

  public int combine(final int combined) {
    firstValue = combined;

    switch (cStatus) {
      case FIRST:
        return firstValue;
      case SECOND:
        return firstValue + secondValue;
      default:
        throw new RuntimeException("unexpected RtmNode state during combining: " + cStatus);
    }
  }

  public int operation(final int combined) {
    switch (cStatus) {
      case ROOT:
        final int prior = result;
        result += combined;
        return prior;
      case SECOND:
        secondValue = combined;
        cStatus = CStatus.IDLE;
        return result;
      default:
        throw new RuntimeException("unexpected RtmNode state during operation: " + cStatus);
    }
  }

  public void distribute(final int prior) {
    switch (cStatus) {
      case FIRST:
        cStatus = CStatus.IDLE;
        break;
      case SECOND:
        result = prior + firstValue;
        cStatus = CStatus.RESULT;
        break;
      default:
        throw new RuntimeException("unexpected RtmNode state during distribution: " + cStatus);
    }
  }

  public RtmNode getParent() {
    return parent;
  }

  public int getResult() {
    return result;
  }
}
