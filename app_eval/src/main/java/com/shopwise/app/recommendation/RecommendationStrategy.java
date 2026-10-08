package com.shopwise.app.recommendation;

import java.util.List;
import java.util.Set;

/** Replaceable learning and inference contract, independent of persistence. */
public interface RecommendationStrategy {
  double[] score(int productCount, List<Set<Integer>> baskets, int sourceIndex);
}
