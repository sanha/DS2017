package sanha.rtm;

import sanha.SharedVariable;

import java.util.Stack;

/**
 * A shared variable implemented as a combining tree Restricted Transactional Memory.
 */
public final class RtmCombiningTreeSharedVariable implements SharedVariable {

  private final RtmNode[] nodes;
  private final RtmNode[] leaf;

  public RtmCombiningTreeSharedVariable(final int initialValue,
                                        final int width) {
    nodes = new RtmNode[width - 1];
    // root node
    nodes[0] = new RtmNode(initialValue);
    // other nodes
    for (int i = 1; i < nodes.length; i++) {
      nodes[i] = new RtmNode(nodes[(i - 1) / 2]);
    }
    // leaf nodes
    leaf = new RtmNode[(width + 1) / 2];
    for (int i = 0; i < leaf.length; i++) {
      leaf[i] = nodes[nodes.length - i - 1];
    }
  }

  @Override
  public int getAndIncrement(final int threadId) {
    return this.getAndAddSynch(threadId, 1);
  }

  @Override
  public int getAndAdd(final int threadId,
                       final int delta) {
    return this.getAndAddSynch(threadId, delta);
  }

  private synchronized int getAndAddSynch(final int threadId,
                                          final int delta) {
    final Stack<RtmNode> stack = new Stack<>();
    final RtmNode myLeaf = leaf[threadId/2];

    // Pre-combining phase
    RtmNode node = myLeaf;
    while (node.precombine()) {
      node = node.getParent();
    }
    final RtmNode stop = node;

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
