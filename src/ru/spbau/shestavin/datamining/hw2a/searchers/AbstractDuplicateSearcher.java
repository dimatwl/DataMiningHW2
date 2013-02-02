package ru.spbau.shestavin.datamining.hw2a.searchers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public abstract class AbstractDuplicateSearcher {

    private List<List<String>> readData(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        List<List<String>> result = new ArrayList<List<String>>();
        boolean data = false;
        try {
            String line = br.readLine();
            while (line != null) {
                List<String> newEntry = new ArrayList<String>(Arrays.asList(line.split("\t")));
                result.add(newEntry);
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        return result;
    }

    private Result searchForDuplicates(List<List<String>> data, float threshold) {
        Result result = new Result(threshold);
        for (int i = 0; i < data.size(); ++i) {
            for (int j = i + 1; j < data.size(); ++j) {
                List<String> left = data.get(i);
                List<String> right = data.get(j);
                float metric = getMetric(left.get(2), right.get(2));
                boolean sameObject = threshold <= metric;
                boolean inOneDomain = left.get(1).equals(right.get(1));
                setResult(result, inOneDomain, sameObject);
                if (sameObject && !inOneDomain) {
                    System.out.println(left.toString() + " = " + right.toString());
                }
            }
        }
        return result;
    }

    private void setResult(Result result, boolean inOneDomain, boolean sameObject) {
        if (sameObject && inOneDomain) {
            result.addTruePos();
        } else if (sameObject) {
            result.addFalsePos();
        } else if (!inOneDomain) {
            result.addTrueNeg();
        } else {
            result.addFalseNeg();
        }
    }

    protected abstract float getMetric(String left, String right);

    public List<Result> getResults(String fileName, float step) throws IOException, InterruptedException, ExecutionException {
        final List<List<String>> data = readData(fileName);
        List<Result> results = new ArrayList<Result>();
        List<Future<Result>> futureResults = new ArrayList<Future<Result>>();

        ExecutorService executor = Executors.newCachedThreadPool();
        //for (float i = 0; i <= 1; i += step) {
            final float threshold = 1;
            futureResults.add(executor.submit(new Callable<Result>() {
                @Override
                public Result call() throws Exception {
                    return searchForDuplicates(data, threshold);
                }
            }));
        //}
        for (Future<Result> futureResult : futureResults) {
            results.add(futureResult.get());
        }
        executor.shutdownNow();
        return results;

    }

    public class Result {
        private int falseNeg = 0;
        private int falsePos = 0;
        private int trueNeg = 0;
        private int truePos = 0;

        private float threshold = 0;

        public Result(float threshold) {
            this.threshold = threshold;
        }

        public void addFalseNeg() {
            ++falseNeg;
        }

        public void addFalsePos() {
            ++falsePos;
        }

        public void addTrueNeg() {
            ++trueNeg;
        }

        public void addTruePos() {
            ++truePos;
        }

        public float getPrecision() {
            return ((float) (truePos)) / ((float) (truePos + falsePos));
        }

        public float getRecall() {
            return ((float) (truePos)) / ((float) (truePos + falseNeg));
        }

        public float getThreshold() {
            return threshold;
        }
    }
}
