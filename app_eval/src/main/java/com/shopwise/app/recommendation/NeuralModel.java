package com.shopwise.app.recommendation;

import java.util.*;

/** Small multi-label network: masked basket -> tanh hidden layer -> sigmoid products. */
public final class NeuralModel {
  private final int size;
  private final double[][] input, output;
  private final double[] hiddenBias, outputBias;

  public NeuralModel(int size) {
    this.size = size;
    int width = Math.min(16, Math.max(4, size));
    input = new double[width][size];
    output = new double[size][width];
    hiddenBias = new double[width];
    outputBias = new double[size];
    Random random = new Random(42);
    for (double[] row : input) for (int i = 0; i < row.length; i++) row[i] = random.nextGaussian() * .1;
    for (double[] row : output) for (int i = 0; i < row.length; i++) row[i] = random.nextGaussian() * .1;
  }

  private double[] hidden(double[] x) {
    double[] h = hiddenBias.clone();
    for (int j = 0; j < h.length; j++) {
      for (int i = 0; i < size; i++) h[j] += input[j][i] * x[i];
      h[j] = Math.tanh(h[j]);
    }
    return h;
  }

  public double[] predict(double[] x) {
    double[] h = hidden(x), y = outputBias.clone();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < h.length; j++) y[i] += output[i][j] * h[j];
      y[i] = 1 / (1 + Math.exp(-Math.max(-30, Math.min(30, y[i]))));
    }
    return y;
  }

  /** One example per held-out product. Targets identify only the masked product. */
  public int train(List<Set<Integer>> baskets, int epochs) {
    int examples = baskets.stream().filter(b -> b.size() >= 2).mapToInt(Set::size).sum();
    for (int epoch = 0; epoch < epochs; epoch++) for (Set<Integer> basket : baskets) {
      if (basket.size() < 2) continue;
      for (int target : new TreeSet<>(basket)) {
        double[] x = new double[size];
        for (int index : basket) if (index != target) x[index] = 1;
        double[] h = hidden(x), y = predict(x), dh = new double[h.length];
        for (int i = 0; i < size; i++) {
          double error = y[i] - (i == target ? 1 : 0);
          for (int j = 0; j < h.length; j++) dh[j] += error * output[i][j];
        }
        double rate = .05;
        for (int i = 0; i < size; i++) {
          double error = y[i] - (i == target ? 1 : 0);
          outputBias[i] -= rate * error;
          for (int j = 0; j < h.length; j++) output[i][j] -= rate * (error * h[j] + .001 * output[i][j]);
        }
        for (int j = 0; j < h.length; j++) {
          double gradient = dh[j] * (1 - h[j] * h[j]);
          hiddenBias[j] -= rate * gradient;
          for (int i = 0; i < size; i++) input[j][i] -= rate * (gradient * x[i] + .001 * input[j][i]);
        }
      }
    }
    return examples;
  }
}
