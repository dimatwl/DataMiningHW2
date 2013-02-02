package ru.spbau.shestavin.datamining.hw2a.searchers;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class LevenshteinSearcher extends AbstractDuplicateSearcher {
    private AbstractStringMetric metric = new Levenshtein();

    @Override
    protected float getMetric(String left, String right) {
        return metric.getSimilarity(left, right);
    }
}
