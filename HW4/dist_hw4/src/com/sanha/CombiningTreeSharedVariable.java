package com.sanha;

import java.util.Stack;

/**
 * A shared variable implemented as a combining tree.
 */
public final class CombiningTreeSharedVariable implements SharedVariable {

  private final Node[] nodes;
  private final Node[] leaf;

  public CombiningTreeSharedVariable(final int initialValue,
                                     final int width) {
    nodes = new Node[width - 1];
    // root node
    nodes[0] = new Node(initialValue);
    // other nodes
    for (int i = 1; i < nodes.length; i++) {
      nodes[i] = new Node(nodes[(i - 1) / 2]);
    }
    // leaf nodes
    leaf = new Node[(width + 1) / 2];
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
    final Stack<Node> stack = new Stack<>();
    final Node myLeaf = leaf[threadId/2];

    // Pre-combining phase
    Node node = myLeaf;
    while (node.precombine()) {
      node = node.getParent();
    }
    final Node stop = node;

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
