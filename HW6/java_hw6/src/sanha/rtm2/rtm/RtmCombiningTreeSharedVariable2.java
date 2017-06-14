package sanha.rtm2.rtm;

import sanha.SharedVariable;

import java.util.Stack;

/**
 * A shared variable implemented as a combining tree Restricted Transactional Memory.
 */
public final class RtmCombiningTreeSharedVariable2 implements SharedVariable {

  private final RtmNode2[] nodes;
  private final RtmNode2[] leaf;

  public RtmCombiningTreeSharedVariable2(final int initialValue,
                                         final int width) {
    nodes = new RtmNode2[width - 1];
    // root node
    nodes[0] = new RtmNode2(initialValue);
    // other nodes
    for (int i = 1; i < nodes.length; i++) {
      nodes[i] = new RtmNode2(nodes[(i - 1) / 2]);
    }
    // leaf nodes
    leaf = new RtmNode2[(width + 1) / 2];
    for (int i = 0; i < leaf.length; i++) {
      leaf[i] = nodes[nodes.length - i - 1];
    }
  }

  @Override
  public int getAndIncrement(final int threadId) {
    return this.getAndAdd(threadId, 1);
  }

  @Override
  public int getAndAdd(final int threadId,
                       final int delta) {
    final Stack<RtmNode2> stack = new Stack<>();
    final RtmNode2 myLeaf = leaf[threadId/2];

    // Pre-combining phase
    RtmNode2 node = myLeaf;
    while (node.precombine()) {
      node = node.getParent();
    }
    final RtmNode2 stop = node;

    // Combining phase
    node = myLeaf;
    int combined = delta;
    while (node != stop) {
      combined = node.combine(combined);
      stack.push(node);
      node = node.getParent();
    }

    // Operation phase
    final int prior = stop.operation(combined);

    // Distribution phase
    while (!stack.empty()) {
      node = stack.pop();
      node.distribute(prior);
    }

    return prior;
  }

  @Override
  public int get() {
    return nodes[0].getResult();
  }
}
