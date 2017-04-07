package util;

/**
 * Created by Ulf on 09/04/15.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import routing.*;

public class InputReader {

    public static ArrayList<Face> ReadFile(List<Node> nodes, String filename) {
        ArrayList<Face> faces = new ArrayList<Face>();
        BufferedReader br = null;

        double avg = 0;

        try {

            String sCurrentLine;
            int currentFace = 0;

            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                ArrayList<Node> temp = new ArrayList<>();
                Scanner sc = new Scanner(sCurrentLine);
                while (sc.hasNextInt()){
                    int next = sc.nextInt();
                    for (Node node : nodes) {
                        if (node.getsId() == next) {
                            //temp.add(new Node(node.x, node.y, next, (short) 0));
                            temp.add(node);
                            break;
                        }
                    }
                }
                faces.add(new Face(currentFace, temp, 0));
                avg += temp.size();
                currentFace++;
            }

            System.out.println("Number of faces with before removal: " + faces.size());

            //TODO REACTIVATE AGAIN WHEN USING ON REAL DATA
//            RemoveSmallestAndBiggestAvgFaces(faces,avg);

            System.out.println("Done with input");
            return faces;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static void RemoveSmallestAndBiggestAvgFaces(ArrayList<Face> faces, double avg){
        /**
         * REMOVE IF USING TEST VARIABLES
         */
        avg = avg / faces.size();
        double upperNumOfNodes = ((avg/100)*80);
        double lowerNumOfNodes = ((avg/100)*10);

        System.out.println("Removing faces with more than: " + upperNumOfNodes + " nodes");
        System.out.println("Removing faces with less than: " + lowerNumOfNodes + " nodes");

        ArrayList<Face> removedFaces = new ArrayList<>();

        for (int i = 0; i < faces.size()-1 ; i++) {
            if (faces.get(i).getNodes().size() > upperNumOfNodes || faces.get(i).getNodes().size() < lowerNumOfNodes) {
                removedFaces.add(faces.get(i));
                //System.out.println("Removed face number of nodes: " + faces.get(i).getNodes().size());
            }
        }

        for (Face f : removedFaces){
            faces.remove(f);
        }

        System.out.println("Aveage numer of nodes: " + avg);
        System.out.println("Number of faces: " + faces.size());
        System.out.println("Number of faces removed: " + removedFaces.size());
    }
}