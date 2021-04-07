import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVWriter {

    public CSVWriter() {
    }

    public void writeFile(String filename, String filepath, ArrayList<ArrayList<ResponseResult>> responses) throws IOException {
        FileWriter fileWriter = new FileWriter(String.valueOf(Paths.get(filepath, filename)));
        fileWriter.write("\"start time\",\t\"request type\",\t\"latency\",\t\"response code\"\n");
        for(ArrayList<ResponseResult> response:responses){
            for(ResponseResult res:response){
                fileWriter.write(res.toString());
            }
        }
        fileWriter.close();
    }
}
