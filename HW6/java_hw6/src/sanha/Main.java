package sanha;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Dist hw 6 - default software combining tree.
 */
public class Main {

  public static void main(final String[] args) {
    if (args.length != 4) {
      System.err.println("Invalid arg: rtm (true / false), machine, max thread number, hop size");
      return;
    }

    final boolean rtm = Boolean.valueOf(args[0]);
    final String machine = args[1];
    final int maxThreadNum = Integer.valueOf(args[2]);
    final int hopSize = Integer.valueOf(args[3]);

    final String treeThFileName = "./result/" + machine + "_" + rtm + "_tree_throughput.txt";
    final String treeLaFileName = "./result/" + machine + "_" + rtm + "_tree_latency.txt";

    try {
      // Worm up
      for (int i = 1; i <= maxThreadNum / 2; i += hopSize) {
        if (rtm) {
          ExperimentUtils.throughputExperiment(
              ExperimentUtils.SharedVariableType.RTM_TREE,
              i,
              1000,
              100);
          ExperimentUtils.latencyExperiment(
              ExperimentUtils.SharedVariableType.RTM_TREE,
              i,
              1000,
              100);
        } else {
          ExperimentUtils.throughputExperiment(
              ExperimentUtils.SharedVariableType.TREE,
              i,
              1000,
              100);
          ExperimentUtils.latencyExperiment(
              ExperimentUtils.SharedVariableType.TREE,
              i,
              1000,
              100);
        }
      }

      Files.createFile(Paths.get(treeThFileName));
      final BufferedWriter fw1 = new BufferedWriter(new FileWriter(treeThFileName, true));
      Files.createFile(Paths.get(treeLaFileName));
      final BufferedWriter fw2 = new BufferedWriter(new FileWriter(treeLaFileName, true));

      for (int i = 1; i <= maxThreadNum; i += hopSize) {
        if (rtm) {
          final long duration = ExperimentUtils.throughputExperiment(
              ExperimentUtils.SharedVariableType.RTM_TREE,
              i,
              1000,
              100);
          fw1.write(i + "\t" + duration + "\n");
          final long latency = ExperimentUtils.latencyExperiment(
              ExperimentUtils.SharedVariableType.RTM_TREE,
              i,
              1000,
              100);
          fw2.write(i + "\t" + latency + "\n");
        } else {
          final long duration = ExperimentUtils.throughputExperiment(
              ExperimentUtils.SharedVariableType.TREE,
              i,
              1000,
              100);
          fw1.write(i + "\t" + duration + "\n");
          final long latency = ExperimentUtils.latencyExperiment(
              ExperimentUtils.SharedVariableType.TREE,
              i,
              1000,
              100);
          fw2.write(i + "\t" + latency + "\n");
        }
      }
      fw1.flush();
      fw2.flush();
      fw1.close();
      fw2.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
