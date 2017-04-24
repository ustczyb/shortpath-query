package src;
import java.io.*;
import java.util.*;

import routing.*;
import util.*;

public class MainTestVariables {

    public static void main(String[] args) throws Exception{
        ArrayList<Edge> content = new ArrayList<>();
        ArrayList<Node> nodes = new ArrayList<>();

        //USUAL TEST INPUT, NON-BRINKHOFF VERSION
        short x = 0;
        int q = 100; //Scale variable. Is necessary for visualization
        int z = 5000;
        Node n0 = new Node(z+0,z+10*q,1,x);
        Node n1 = new Node(z+10*q,z+10*q,2,x);
        Node n2 = new Node(z+10*q,z+20*q,3,x);
        Node n3 = new Node(z+0,z+20*q,4,x);
        Node n4 = new Node(z+5*q,z+0,5,x);
        Node n5 = new Node(z+20*q,z+20*q,6,x);
        Node n6 = new Node(z+20*q,z+10*q,7,x);
        Node n7 = new Node(z+15*q,z+0,8,x);

        content.add(new Edge(n0.getID() * 100000 + n1.getID(),n0,n1,"",x,x,0)); //0
        //content.add(new Edge(n1.getID() * 100000 + n2.getID(),n1,n2,"",x,x,0)); //1
        content.add(new Edge(n2.getID() * 100000 + n3.getID(),n2,n3,"",x,x,0)); //2
        content.add(new Edge(n3.getID() * 100000 + n0.getID(),n3,n0,"",x,x,0)); //3
        content.add(new Edge(n0.getID() * 100000 + n4.getID(),n0,n4,"",x,x,0)); //4
        content.add(new Edge(n4.getID() * 100000 + n1.getID(),n4,n1,"",x,x,0)); //5
        content.add(new Edge(n2.getID() * 100000 + n5.getID(),n2,n5,"",x,x,0)); //6
        content.add(new Edge(n1.getID() * 100000 + n6.getID(), n1, n6, "", x, x, 0)); //7
        content.add(new Edge(n5.getID() * 100000 + n6.getID(), n5, n6, "", x, x, 0)); //8
        content.add(new Edge(n1.getID() * 100000 + n7.getID(),n1,n7,"",x,x,0));//Extra //9
        content.add(new Edge(n6.getID() * 100000 + n7.getID(), n6, n7, "", x, x, 0));//Extra //10

        n0.addEdge(content.get(0));
        n0.addEdge(content.get(3-1));
        n0.addEdge(content.get(4-1));
        n1.addEdge(content.get(0));
        //n1.addEdge(content.get(1-1));
        n1.addEdge(content.get(5-1));
        n1.addEdge(content.get(7-1));
        n1.addEdge(content.get(9-1));
        //n2.addEdge(content.get(1-1));
        n2.addEdge(content.get(2-1));
        n2.addEdge(content.get(6-1));
        n3.addEdge(content.get(2-1));
        n3.addEdge(content.get(3-1));
        n4.addEdge(content.get(4-1));
        n4.addEdge(content.get(5-1));
        n5.addEdge(content.get(6-1));
        n5.addEdge(content.get(7-1));
        n6.addEdge(content.get(7-1));
        n6.addEdge(content.get(8-1));
        n6.addEdge(content.get(10-1));
        n7.addEdge(content.get(9-1));
        n7.addEdge(content.get(10-1));

        nodes.add(n0);
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        nodes.add(n5);
        nodes.add(n6);
        nodes.add(n7);// Extra

        Utility.setAllEdges(content);
        Utility.initializeAllNodes(nodes);

//        For generating test example
        try {
            OutputWriter.WriteEdgesToEdgeFile(content,"/home/nicky/Sim-city/dataset/network" + ".edge");
            OutputWriter.WriteEdgesToEdgeFile(content,"/home/nicky/Sim-city/dataset/Originalnetwork" + ".edge");
            OutputWriter.WriteNodesToNodesFile(nodes,"/home/nicky/Sim-city/dataset/network" + ".node");
            OutputWriter.WriteNodesToNodesFile(nodes,"/home/nicky/Sim-city/dataset/Originalnetwork" + ".node");

            System.out.println("DONE WRITING TEST SET");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}