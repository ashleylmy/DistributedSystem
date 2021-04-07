import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DataProcessor {
    private List<Integer> latencyResults =new ArrayList<Integer>();
    private int dataSize=0;
    private double mean;
    private double median;
    private int throughput;
    private Integer maxResponse;
    private Integer nintyninthPercentile;

    public DataProcessor(ArrayList<ArrayList<ResponseResult>> results, long wallTime, Integer totalSuccessRequests ) {
        processResults(results);
        dataSize=latencyResults.size();
        this.mean = calculateMean(latencyResults);
        this.median = calculateMedian(latencyResults);
        this.throughput =  (int)(totalSuccessRequests / wallTime);
        this.maxResponse = latencyResults.get(dataSize - 1);
        this.nintyninthPercentile = latencyResults.get((int) Math.ceil(dataSize * 0.99) - 1);
    }

    private void processResults(ArrayList<ArrayList<ResponseResult>> results){
        for(ArrayList<ResponseResult> response:results){
            for(ResponseResult res:response){
               latencyResults.add(res.getLatency());
            }
        }
    }

    private double calculateMean(List<Integer> responseTimes) {
        double sum = 0;
        for (Integer response : responseTimes) {
            sum += response;
        }
        return sum / dataSize;
    }

    private double calculateMedian(List<Integer> responseTimes) {
        Collections.sort(responseTimes);
        double median;
        if (dataSize % 2 == 0) {
            int sumOfMiddleElements =
                    responseTimes.get(dataSize / 2) + responseTimes.get(dataSize / 2 - 1);
            median = ((double) sumOfMiddleElements) / 2;
        } else {
            median = (double) responseTimes.get(dataSize / 2);
        }
        return median;
    }


    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public int getThroughput() {
        return throughput;
    }

    public Integer getMaxResponse() {
        return maxResponse;
    }

    public Integer getNintyninthPercentile() {
        return nintyninthPercentile;
    }
}
