package src;
import java.util.*;
import routing.*;
import util.*;

public class MainBrinkhoff {

    //BRINKHOFF VERSION, USED TO TEST PROGRAM ON BRINKHOFF DATASET
    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(".\\boost-facefinding\\facefinding.exe");
        Process p = pb.start();
        p.waitFor();

        Utility.setAllNodes(Utility.removeSingleEdgeNodes(Utility.getAllNodes()));
        //Used to write the files for input for the C++ program
        OutputWriter.WriteSplitFiles(Utility.getAllEdges());
    }
}