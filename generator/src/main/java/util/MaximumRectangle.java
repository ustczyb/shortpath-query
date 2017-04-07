package util;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import routing.*;

public class MaximumRectangle {

    final static class Cell {

        final int col;
        final int row;

        Cell(final int col, final int row) {
            this.col = col;
            this.row = row;
        }

        @Override
        public String toString() {
            return String.format("[col=%d, row=%d]", col + 1, row + 1);
        }
    }

    final static class Cache {

        private final LinkedList<Integer> aggregateHeights;

        Cache(final int size) {
            aggregateHeights = new LinkedList<>();
            for (int i = 0; i <= size; i++) {
                aggregateHeights.add(0);
            }
        }

        public int get(final int col) {
            return aggregateHeights.get(col);
        }

        public void aggregateInt(final int[][] faceMatrix, int row, boolean print) {
            for (int col = 0; col < faceMatrix.length; col++) {
            //for (int col = faceMatrix[0].length-1; col >= 0; col--) {
                final int element = faceMatrix[col][row];
                if (print) {
                    System.err.printf("%s ", element);
                }

                if (0 == element) {
                    aggregateHeights.set(col, 0);
                } else {
                    aggregateHeights.set(col, aggregateHeights.get(col) + 1);
                }
            }
            if (print) {
                System.err.printf("\n");
            }
        }
    }

    public static Face FindMaximumRectangle(int[][] faceMatrix, Face currentFace){

        int bestArea = 0;
        Cell bestLowerLeftCorner = new Cell(0, 0);
        Cell bestUpperRightCorner = new Cell(-1, -1);

        final int numColumns = faceMatrix.length;
        final int numRows = faceMatrix[0].length;

        //System.err.printf("Reading %dx%d array (1 row == %d elements)\n", numColumns, numRows, numColumns);

        final Stack<Cell> stack = new Stack<>();
        final Cache rectangleHeightCache = new Cache(numColumns);

        int row = 0;
        for (int i = numRows-1; i >= 0; i--) {
            rectangleHeightCache.aggregateInt(faceMatrix, i, false); //Change third variable to print face in matrix form
            for (int col = 0, currentRectHeight = 0; col <= numColumns; col++) {
                final int aggregateRectHeight = rectangleHeightCache.get(col);
                if (aggregateRectHeight > currentRectHeight) {
                    stack.push(new Cell(col, currentRectHeight));
                    currentRectHeight = aggregateRectHeight;
                } else if (aggregateRectHeight < currentRectHeight) {
                    Cell rectStartCell;
                    do {
                        rectStartCell = stack.pop();
                        final int rectWidth = col - rectStartCell.col;
                        final int area = currentRectHeight * rectWidth;
                        if (area > bestArea) {
                            bestArea = area;
                            bestLowerLeftCorner = new Cell(rectStartCell.col, row);
                            bestUpperRightCorner = new Cell(col - 1, row - currentRectHeight + 1);
                        }
                        currentRectHeight = rectStartCell.row;
                        } while (aggregateRectHeight < currentRectHeight);
                            currentRectHeight = aggregateRectHeight;
                            if (currentRectHeight != 0) {
                                stack.push(rectStartCell);
                            }
                    }
                }
                row++;
            }
            //FOR TESTING ONLY
/*
        if (currentFace.getId() == 1) {
            System.err.printf("Reading %dx%d array (1 row == %d elements)\n", numColumns, numRows, numColumns);
            System.err.printf("The maximal rectangle has area %d.\n", bestArea);
            System.err.printf("Location: %s to %s\n", bestLowerLeftCorner, bestUpperRightCorner);
        }
*/
        //This is for visualising the maximum rectangle found:
        ArrayList<Node> tempNodes = Utility.getAllNodes();
        ArrayList<Edge> tempEdges = Utility.getAllEdges();

        Node lowerLeft = new Node(currentFace.LowerX() + bestLowerLeftCorner.col, currentFace.HighestY() - bestLowerLeftCorner.row, Utility.getUniqueNodeID(), (short)5);
        Node upperLeft = new Node(currentFace.LowerX() + bestLowerLeftCorner.col, currentFace.HighestY() - bestUpperRightCorner.row, Utility.getUniqueNodeID(), (short)5);
        Node lowerRight = new Node(currentFace.LowerX() + bestUpperRightCorner.col, currentFace.HighestY() - bestLowerLeftCorner.row, Utility.getUniqueNodeID(), (short)5);
        Node upperRight = new Node(currentFace.LowerX() + bestUpperRightCorner.col, currentFace.HighestY() - bestUpperRightCorner.row, Utility.getUniqueNodeID(), (short)5);

        //Adding the maximum rectangle nodes to the face
        ArrayList<Node> maximumRectangleNodes = new ArrayList<>();
        maximumRectangleNodes.add(lowerLeft);
        maximumRectangleNodes.add(upperLeft);
        maximumRectangleNodes.add(lowerRight);
        maximumRectangleNodes.add(upperRight);
        currentFace.setMaximumRectangleNodes(maximumRectangleNodes);//TODO Delete this if you don't want to draw maximum rectangle

        Edge llul = new Edge(lowerLeft.getID() * 100000 + upperLeft.getID(), lowerLeft, upperLeft, "walls", (short)5, (short)0, 0);
        Edge lllr = new Edge(lowerLeft.getID() * 100000 + lowerRight.getID(), lowerLeft, lowerRight, "walls", (short)5, (short)0, 0);
        Edge urul = new Edge(upperRight.getID() * 100000 + upperLeft.getID(), upperRight, upperLeft, "walls", (short)5, (short)0, 0);
        Edge urlr = new Edge(upperRight.getID() * 100000 + lowerRight.getID(), upperRight, lowerRight, "walls", (short)5, (short)0, 0);

        //Adding the maximum rectangle edges to the face
        ArrayList<Edge> maximumRectangle = new ArrayList<>();
        maximumRectangle.add(llul);
        maximumRectangle.add(lllr);
        maximumRectangle.add(urul);
        maximumRectangle.add(urlr);
        currentFace.setMaximumRectangleEdges(maximumRectangle); //TODO Delete this if you don't want to draw maximum rectangle

//        tempNodes.add(lowerLeft);
//        tempNodes.add(lowerRight);
//        tempNodes.add(upperLeft);
//        tempNodes.add(upperRight);

        urul.setEdgeClass((short)9);
        urlr.setEdgeClass((short)9);
        llul.setEdgeClass((short)9);
        lllr.setEdgeClass((short)9);

//        tempEdges.add(lllr);
//        tempEdges.add(llul);
//        tempEdges.add(urlr);
//        tempEdges.add(urul);

        //Utility.setAllEdges(tempEdges);
        //Utility.setAllNodes(tempNodes);

        //TODO Skal lige gøre sådan så det ikke bliver gjort på alle
//        int numberOfMultibuildings = (int)Math.ceil(((currentFace.LowerX() + bestUpperRightCorner.col) - (currentFace.LowerX() + bestLowerLeftCorner.col)) / ((currentFace.HighestY() - bestUpperRightCorner.row)-(currentFace.HighestY() - bestLowerLeftCorner.row)));
//        if (numberOfMultibuildings <= 1){
//            for (Node n : currentFace.getNodes()) {
//                n = Utility.NodeRotation(n,currentFace.getCenter(),currentFace.getAngle()+Math.toRadians(90));
//            }
//            currentFace.setAngle(currentFace.getAngle()+Math.toRadians(90));
//        }

        //return FaceTemplate.insertTemplate(currentFace.HighestX() - bestUpperRightCorner.col, currentFace.HighestY() - bestUpperRightCorner.row, faceMatrix, currentFace.HighestX() - bestLowerLeftCorner.col, currentFace.HighestY() - bestLowerLeftCorner.row ,currentFace, 1);
        return BuildingTemplate.insertTemplate(currentFace.LowerX() + bestUpperRightCorner.col, currentFace.HighestY() - bestUpperRightCorner.row, faceMatrix, currentFace.LowerX() + bestLowerLeftCorner.col, currentFace.HighestY() - bestLowerLeftCorner.row ,currentFace, 0);
        //return FaceTemplate.insertTemplate(bestUpperRightCorner.col, bestUpperRightCorner.row, faceMatrix, bestLowerLeftCorner.col, bestLowerLeftCorner.row,currentFace, 1);
    }
}
