/**
 * Result from API call response
 */

public class ResponseResult {

    private long startTime;
    private long latency;
    private int responseCode;

    public ResponseResult(long startTime, long latency, int responseCode) {
        this.startTime = startTime;
        this.latency = latency;
        this.responseCode = responseCode;
    }

    public int getLatency() {
        return (int) latency;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return startTime + "\t, Post request\t, " + latency + "\t, "  + responseCode + "\n";
    }
}
