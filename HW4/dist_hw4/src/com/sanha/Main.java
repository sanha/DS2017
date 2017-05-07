package com.sanha;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Dist hw 4 - software combining tree.
 */
public class Main {

  public static void main(final String[] args) {
    if (args.length != 3) {
      System.err.println("Invalid arg: machine type, max thread number, hop size");
      return;
    }

    final String machine = args[0];
    final int maxThreadNum = Integer.valueOf(args[1]);
    final int hopSize = Integer.valueOf(args[2]);

    final String atomicThFileName = "./result/" + machine + "_atomic_throughput.txt";
    final String treeThFileName = "./result/" + machine + "_tree_throughput.txt";
    final String atomicLaFileName = "./result/" + machine + "_atomic_latency.txt";
    final String treeLaFileName = "./result/" + machine + "_tree_latency.txt";

    try {
      // Worm up
      for (int i = 1; i <= maxThreadNum / 2; i += hopSize) {
        ExperimentUtils.throughputExperiment(
            ExperimentUtils.SharedVariableType.ATOMIC,
            i,
            1000,
            100);
        ExperimentUtils.throughputExperiment(
            ExperimentUtils.SharedVariableType.TREE,
            i,
            1000,
            100);
        ExperimentUtils.latencyExperiment(
            ExperimentUtils.SharedVariableType.ATOMIC,
            i,
            1000,
            100);
        ExperimentUtils.latencyExperiment(
            ExperimentUtils.SharedVariableType.TREE,
            i,
            1000,
            100);
      }

      Files.createFile(Paths.get(atomicThFileName));
      final BufferedWriter fw1 = new BufferedWriter(new FileWriter(atomicThFileName, true));
      Files.createFile(Paths.get(treeThFileName));
      final BufferedWriter fw2 = new BufferedWriter(new FileWriter(treeThFileName, true));
      Files.createFile(Paths.get(atomicLaFileName));
      final BufferedWriter fw3 = new BufferedWriter(new FileWriter(atomicLaFileName, true));
      Files.createFile(Paths.get(treeLaFileName));
      final BufferedWriter fw4 = new BufferedWriter(new FileWriter(treeLaFileName, true));

      for (int i = 1; i <= maxThreadNum; i += hopSize) {
        final long duration1 = ExperimentUtils.throughputExperiment(
            ExperimentUtils.SharedVariableType.ATOMIC,
            i,
            1000,
            100);
        fw1.write(i + "\t" + duration1 + "\n");
        final long duration2 = ExperimentUtils.throughputExperiment(
            ExperimentUtils.SharedVariableType.TREE,
            i,
            1000,
            100);
        fw2.write(i + "\t" + duration2 + "\n");
        final long latency1 = ExperimentUtils.latencyExperiment(
            ExperimentUtils.SharedVariableType.ATOMIC,
            i,
            1000,
            100);
        fw3.write(i + "\t" + latency1 + "\n");
        final long latency2 = ExperimentUtils.latencyExperiment(
            ExperimentUtils.SharedVariableType.TREE,
            i,
            1000,
            100);
        fw4.write(i + "\t" + latency2 + "\n");
      }
      fw1.flush();
      fw2.flush();
      fw3.flush();
      fw4.flush();
      fw1.close();
      fw2.close();
      fw3.close();
      fw4.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
