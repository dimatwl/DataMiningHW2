package ru.spbau.shestavin.datamining.hw2a;

import ru.spbau.shestavin.datamining.hw2a.searchers.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {


    public static void main(final String[] args) throws IOException, InterruptedException, ExecutionException {
        List<AbstractDuplicateSearcher> searchers = new ArrayList<AbstractDuplicateSearcher>();

        searchers.add(new LevenshteinSearcher());
        //searchers.add(new JaroWinklerSearcher());
        //searchers.add(new MongeElkanSearcher());
        //searchers.add(new SmithWatermanSearcher());

        ExecutorService executor = Executors.newCachedThreadPool();
        final String inputFile = "./res/gameExtracted.txt";
        final float step = (float)0.01;
        List<Future<?>> futureResults = new ArrayList<Future<?>>();
        for (final AbstractDuplicateSearcher searcher : searchers) {
            futureResults.add(executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<AbstractDuplicateSearcher.Result> results = searcher.getResults(inputFile, step);
                        PrintWriter thresholdOut = new PrintWriter(new FileWriter("./output/" + searcher.getClass().getSimpleName() + "." + "threshold" + ".txt"));
                        PrintWriter precisionOut = new PrintWriter(new FileWriter("./output/" + searcher.getClass().getSimpleName() + "." + "precision" + ".txt"));
                        PrintWriter recallOut = new PrintWriter(new FileWriter("./output/" + searcher.getClass().getSimpleName() + "." + "recall" + ".txt"));
                        for (AbstractDuplicateSearcher.Result result : results) {
                            thresholdOut.println(result.getThreshold());
                            precisionOut.println(result.getPrecision());
                            recallOut.println(result.getRecall());
                        }
                        thresholdOut.close();
                        precisionOut.close();
                        recallOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        for (Future<?> future : futureResults) {
            future.get();
        }
        executor.shutdownNow();
    }
}
