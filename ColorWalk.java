public class ColorWalk {
    public static class WalkPair {
        char startColor;
        int walkWeight;

        public WalkPair(char startColor, int walkWeight) {
            this.startColor = startColor;
            this.walkWeight = walkWeight;
        }
    }

    public static class Tuple {
        int dist;
        Graph.Node vertex;

        public Tuple(int dist, Graph.Node vertex) {
            this.dist = dist;
            this.vertex = vertex;
        }
    }

    public static class PriorityQueue {
        Tuple[] nodeHeap;
        int numNodes;

        public PriorityQueue(int size) {
            nodeHeap = new Tuple[size];
            numNodes = 0;
        }

        public void insert(int dist, Graph.Node vertex) {
            Tuple newTuple = new Tuple(dist, vertex);
            numNodes++;
            nodeHeap[numNodes - 1] = newTuple;

            if (numNodes > 1) {
                swimUp(nodeHeap, numNodes - 1);
            }
        }

        // TODO: in swimUp and sinkDown I am not properly accounting for duplicate weighs. Not sure if I need to
        public void swimUp(Tuple[] futureHeap, int i) {
            // calculate parent
            int parent = (i - 1) / 2;

            // if child dist is less than its parent dist
            if (futureHeap[i].dist < futureHeap[parent].dist) {
                Tuple temp = futureHeap[i];
                futureHeap[i] = futureHeap[parent];
                futureHeap[parent] = temp;

                // if the root and 2nd or 3rd block were not just swapped, keep checking up the heap
                if (parent != 0) {
                    swimUp(futureHeap, parent);
                }
            }
        }

        public void sinkDown(Tuple[] futureHeap, int heapLength, int i) {
            // assigning smallest to the i of the smallest block of the heap and calculating left child and right child
            int smallest = i;
            int leftChild = (2 * i) + 1;
            int rightChild = (2 * i + 2);

            // if leftChild's dist is less than the smallest's dist, set smallest to leftChild
            if (leftChild < heapLength && futureHeap[leftChild].dist < futureHeap[smallest].dist) {
                smallest = leftChild;
            }

            // if rightChild's dist is less than the smallest's dist, set smallest to rightChild
            if (rightChild < heapLength && futureHeap[rightChild].dist < futureHeap[smallest].dist) {
                smallest = rightChild;
            }

            // if the smallest isn't equal to i, swap the new smallest and ith block and call sinkDown on subtree
            if (smallest != i) {
                Tuple temp = futureHeap[i];
                futureHeap[i] = futureHeap[smallest];
                futureHeap[smallest] = temp;

                sinkDown(futureHeap, heapLength, i);
            }
        }

        public boolean isEmpty() {
            return numNodes == 0;
        }

        public Tuple delMin() {
            Tuple min;
            if (numNodes == 0) {
                return null;
            } else if (numNodes == 1) {
                min = nodeHeap[0];
                nodeHeap[0] = null;
                numNodes = 0;
            } else {
                min = nodeHeap[0];
                nodeHeap[0] = nodeHeap[numNodes - 1];
                nodeHeap[numNodes - 1] = null;
                numNodes--;


                for (int i = 0; i < numNodes; i++) {
                    sinkDown(nodeHeap, numNodes, i);
                }

            }
            return min;
        }

        public void update(int dist, Graph.Node vertex) {
            for (int i = 0; i < numNodes; i++) {
                if (nodeHeap[i].vertex.key == vertex.key) {
                    nodeHeap[i].dist = dist;
                    
                    swimUp(nodeHeap, i);

                    break;
                }
            }
        }

        public boolean findVertex(Graph.Node vertex) {
            for (int i = 0; i < numNodes; i++) {
                if (nodeHeap[i].vertex.key == vertex.key) {
                    return true;
                }
            }
            return false;
        }
    }


    public static WalkPair[] colorWalk(Graph G, int start) {

        // starting from each color of start
        int[] Red = shortestPath(G.adjacencyList, 3*start, start);
        int[] Green = shortestPath(G.adjacencyList, 3*start+1, start);
        int[] Blue = shortestPath(G.adjacencyList, 3*start+2, start);

        int[] startRed = new int[Red.length / 3];
        int[] startGreen = new int[Green.length / 3];
        int[] startBlue = new int[Blue.length / 3];

        int j = 0;
        for (int i = 0; i < startRed.length; i++) {
            startRed[i] = getSmallestInt(Red[j], Red[j+1], Red[j+2]);
            startGreen[i] = getSmallestInt(Green[j], Green[j+1], Green[j+2]);
            startBlue[i] = getSmallestInt(Blue[j], Blue[j+1], Blue[j+2]);
            j = j + 3;
        }

        WalkPair[] result = new WalkPair[startRed.length];

        for (int i = 0; i < result.length; i++) {
            WalkPair shortestWalk = getShortestWalk(startRed[i], startGreen[i], startBlue[i]);
            result[i] = shortestWalk;
        }

        return result;

    }

    public static int getSmallestInt(int Red, int Green, int Blue) {
        if (Red == 0 || Blue == 0 || Green == 0) {
            return 0;
        }

        if (Red == 100000 && Blue == 100000 && Green == 100000) {
            return 100000;
        }

        int weight = Red;

        if (Green < weight) {
            weight = Green;
        }

        if (Blue < weight) {
            weight = Blue;
        }

        return weight;

    }

    public static WalkPair getShortestWalk(int Red, int Green, int Blue) {
        if (Red == 0 || Blue == 0 || Green == 0) {
            return new WalkPair('-', 0);
        }

        if (Red == 100000 && Blue == 100000 && Green == 100000) {
            return new WalkPair('-', -1);
        }

        char color = 'R';
        int weight = Red;

        if (Green < weight) {
            weight = Green;
            color = 'G';
        }

        if (Blue < weight) {
            weight = Blue;
            color = 'B';
        }
        return new WalkPair(color, weight);
    }

    public static int[] shortestPath(Graph.Node[] adjacencyList, int start, int realStart) {

        // setting up stuff as in Dijkstra's algorithm
        PriorityQueue q = new PriorityQueue(adjacencyList.length);
        int[] dist = new int[adjacencyList.length];

        dist[start] = 0;
        for (int i = 0; i < dist.length; i++) {
            if (i != start) {
                dist[i] = 100000;
            }
            q.insert(dist[i], adjacencyList[i]);
        }

        while(!q.isEmpty()) {
            Tuple min = q.delMin();
            Graph.Node vertex = min.vertex;
            int currentKey = vertex.key / 3;

            Graph.Node vertexNeighbor = adjacencyList[vertex.key].next;

            if (vertexNeighbor != null) {
                int neighborKey = vertexNeighbor.key / 3;
            }

            while (vertexNeighbor != null) {
                if (q.findVertex(vertexNeighbor)) {
                    int distTo = dist[vertex.key] + vertexNeighbor.weight;

                    if (distTo < dist[vertexNeighbor.key]) {
                        dist[vertexNeighbor.key] = distTo;
                        q.update(distTo, vertexNeighbor);

                    }
                }
                vertexNeighbor = vertexNeighbor.next;
            }
        }

        return dist;
    }
}