import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class Graph {
    public Node[] adjacencyList;

    // constructor for Graph adjacency list
    public Graph(Node[] adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static class Node {
        int key;
        int weight;
        Node next;
        char color;

        // constructor for unweighted node (used for head nodes in adjacency list readVertexWeights and readEdgeWeights)
        public Node(int key) {
            this.key = key;
            next = null;
        }

        // constructor for readVertexWeights and readEdgeWeights
        public Node(int key, int weight) {
            this.key = key;
            this.weight = weight;
            next = null;
        }

        public Node(int key, char color) {
            this.key = key;
            this.color = color;

        }

    }

    public static Graph readVertexWeights(String filename) throws IOException {
        Scanner input = new Scanner(new File(filename));

        // getting number of vertices from first line of file and initializing adjacency list
        int numVertices = Integer.parseInt(input.nextLine());
        Node[] adjList = new Node[numVertices];

        // storing weights of vertices in array
        String[] weightVertices = input.nextLine().split(" ");
        int listIndex = 0;

        while (input.hasNextLine()) {
            String line = input.nextLine();

            if (!line.equals("")) {
                // making head of linked list the number of the current line of the file
                int vertexWeight = Integer.parseInt(weightVertices[listIndex]);
                Node head = new Node(listIndex, vertexWeight);

                // storing head in the corresponding index of adjacency list
                adjList[listIndex] = head;

                // storing neighbors of head node in array
                String[] adjVertices = line.split(" ");

                // for each neighbor of the head node
                for (String adjVertex : adjVertices) {
                    int nodeKey = Integer.parseInt(adjVertex);
                    int nodeWeight = Integer.parseInt(weightVertices[nodeKey]);

                    Node neighborNode = new Node(nodeKey, nodeWeight);

                    // iterate to end of linked list until null is found
                    Node current = head;
                    while (current.next != null) {
                        current = current.next;
                    }

                    // append the new node to the end of the linked list
                    current.next = neighborNode;
                }
            } else {
                adjList[listIndex] = null;
            }

            // move to next line of the file and the next index of the adjacency list
            listIndex++;
        }

        input.close();
        return new Graph(adjList);
    }


    public static Graph readEdgeWeights(String filename) throws IOException {
        Scanner input = new Scanner(new File(filename));

        // retrieve numVertices and numEdges from first line of file
        String[] line = input.nextLine().split(" ");
        int numVertices = Integer.parseInt(line[0]);
        int numEdges = Integer.parseInt(line[1]);

        Node[] adjList = new Node[numVertices];

        while (input.hasNextLine()) {
            String currentLine = input.nextLine();
            if (!currentLine.equals("")) {
                int sourceVertex = Integer.parseInt(currentLine.split(" ")[0]);
                int destinationVertex = Integer.parseInt(currentLine.split(" ")[1]);
                int edgeWeight = Integer.parseInt(currentLine.split(" ")[2]);

                Node head;
                Node neighbor;

                // fill out adjacency list
                if (adjList[sourceVertex] == null) {
                    head = new Node(sourceVertex);
                    neighbor = new Node(destinationVertex, edgeWeight);
                    head.next = neighbor;
                    adjList[sourceVertex] = head;
                } else {
                    head = adjList[sourceVertex];
                    while (head.next != null) {
                        head = head.next;
                    }
                    neighbor = new Node(destinationVertex, edgeWeight);
                    head.next = neighbor;
                }
            }
        }

        // filling in adjacency list indices in case directed graph node has no outgoing edges
        for (int i = 0; i < adjList.length; i++) {
            if (adjList[i] == null) {
                adjList[i] = new Node(i);
            }
        }

        input.close();
        return new Graph(adjList);
    }

    // constructing graph with different colored outgoing edges from each vertex
    public static Graph readEdgeColors(String filename) throws IOException {
        Scanner input = new Scanner(new File(filename));

        // retrieve numVertices and numEdges from first line of file
        String[] line = input.nextLine().split(" ");
        int numVertices = Integer.parseInt(line[0]);
        int numEdges = Integer.parseInt(line[1]);

        Node[] adjList = new Node[numVertices * 3];
        int i = 0;

        while (i < adjList.length) {
            adjList[i] = new Node(i, 'R');
            adjList[i+1] = new Node(i + 1, 'G');
            adjList[i+2] = new Node(i + 2, 'B');
            i = i + 3;
        }

        while (input.hasNextLine()) {
            String currentLine = input.nextLine();
            if (!currentLine.equals("")) {
                int sourceVertex = Integer.parseInt(currentLine.split(" ")[0]);
                int destinationVertex = Integer.parseInt(currentLine.split(" ")[1]);
                int edgeWeight = Integer.parseInt(currentLine.split(" ")[2]);

                String colorString = currentLine.split(" ")[3];
                char color = colorString.charAt(0);

                Node head;

                // if the edge color is red add it to red source
                if (color == 'R') {
                    head = adjList[3 * sourceVertex];
                    // head.red will only have an edge to destination.green
                    destinationVertex = 3 * destinationVertex + 1;

                } else if (color == 'G') {
                    head = adjList[3 * sourceVertex + 1];
                    //head.green will only have an edge to destination.blue
                    destinationVertex = 3 * destinationVertex + 2;

                } else {
                    head = adjList[3 * sourceVertex + 2];
                    // head.blue will only have an edge to destination.red
                    destinationVertex = 3 * destinationVertex;
                }

                boolean isDuplicate = false;
                boolean skip = false;
                while (head.next != null) {
                    // replacing or skipping edge weight if an edge is found with the same source and dest but differing weight
                    if (destinationVertex == head.next.key && edgeWeight < head.next.weight) {
                        head.next.weight = edgeWeight;
                        isDuplicate = true;
                        break;
                    } else if (destinationVertex == head.next.key && edgeWeight > head.next.weight) {
                        skip = true;
                    }
                    head = head.next;
                }

                if (!isDuplicate && !skip) {
                    head.next = new Node(destinationVertex, edgeWeight);
                }
            }
        }

        input.close();
        return new Graph(adjList);
    }
}