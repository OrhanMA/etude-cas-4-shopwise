package com.shopwise.app.recommendation;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class NeuralRecommendationStrategy implements RecommendationStrategy {
  private final int epochs;
  public NeuralRecommendationStrategy(@Value("${app.recommendations.epochs:150}") int epochs) {
    if (epochs < 1 || epochs > 1000) throw new IllegalArgumentException("Epochs must be between 1 and 1000");
    this.epochs = epochs;
  }
  public double[] score(int productCount, List<Set<Integer>> baskets, int sourceIndex) {
    NeuralModel model = new NeuralModel(productCount);
    model.train(baskets, epochs);
    double[] query = new double[productCount];
    query[sourceIndex] = 1;
    return model.predict(query);
  }
}
