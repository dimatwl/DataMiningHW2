package ru.spbau.shestavin.datamining.hw2a.searchers;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;

public class MongeElkanSearcher extends AbstractDuplicateSearcher {
    private AbstractStringMetric metric = new MongeElkan();

    @Override
    protected float getMetric(String left, String right) {
        return metric.getSimilarity(left, right);
    }
}
