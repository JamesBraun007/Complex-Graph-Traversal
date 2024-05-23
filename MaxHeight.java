public class MaxHeight {

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


        public void swimUp(Tuple[] futureHeap, int i) {
            // calculate parent
            int parent = (i - 1) / 2;

            // if child dist is more than its parent dist
            if (futureHeap[i].dist > futureHeap[parent].dist) {
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
            // assigning largest to the i of the largest block of the heap and calculating left child and right child
            int largest = i;
            int leftChild = (2 * i) + 1;
            int rightChild = (2 * i + 2);

            // if leftChild's dist is more than the largest's dist, set largest to leftChild
            if (leftChild < heapLength && futureHeap[leftChild].dist > futureHeap[largest].dist) {
                largest = leftChild;
            }

            // if rightChild's dist is more than the largest's dist, set largest to rightChild
            if (rightChild < heapLength && futureHeap[rightChild].dist > futureHeap[largest].dist) {
                largest = rightChild;
            }

            // if the largest isn't equal to i, swap the new largest and ith block and call sinkDown on subtree
            if (largest != i) {
                Tuple temp = futureHeap[i];
                futureHeap[i] = futureHeap[largest];
                futureHeap[largest] = temp;

                sinkDown(futureHeap, heapLength, i);
            }
        }

        public boolean isEmpty() {
            return nodeHeap[0] == null;
        }

        public Graph.Node delMax() {
            Graph.Node max;
            if (numNodes == 0) {
                return null;
            } else if (numNodes == 1) {
                max = nodeHeap[0].vertex;
                nodeHeap[0] = null;
                numNodes = 0;
            } else {
                max = nodeHeap[0].vertex;
                nodeHeap[0] = nodeHeap[numNodes - 1];
                nodeHeap[numNodes - 1] = null;
                numNodes--;

                for (int i = 0; i < numNodes; i++) {
                    sinkDown(nodeHeap, numNodes, i);
                }

            }
            return max;
        }

        public void update(int dist, Graph.Node vertex) {
            for (int i = 0; i < numNodes; i++) {
                if (nodeHeap[i].vertex.key == vertex.key) {
                    int oldDist = nodeHeap[i].dist;
                    nodeHeap[i].dist = dist;

                    if (dist < oldDist) {
                        sinkDown(nodeHeap, numNodes, i);
                    } else if (dist > oldDist) {
                        swimUp(nodeHeap, i);
                    }
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

    // build the maximum spanning tree and return the minimum value, which is the maxHeight
    public static int maxHeight(Graph G) {
        int[] MST = MST(G);

        int maxHeight = MST[1];
        for (int i = 1; i < MST.length; i++) {
            if (MST[i] < maxHeight) {
                maxHeight = MST[i];
            }
        }

        return maxHeight;
    }

    public static int[] MST(Graph G) {
        PriorityQueue PQ = new PriorityQueue(G.adjacencyList.length);
        boolean[] visited = new boolean[G.adjacencyList.length];
        int[] distTo = new int[G.adjacencyList.length];

        for (int i = 0; i < distTo.length; i++) {
            distTo[i] = -1;
        }

        distTo[0] = 0;
        PQ.insert(0, G.adjacencyList[0]);


        while(!PQ.isEmpty()) {
            Graph.Node u = PQ.delMax();

            // need this here so that neighbors of u are accessible
            u = G.adjacencyList[u.key];
            visited[u.key] = true;

            Graph.Node v = u.next;

            while (v != null) {

                // if the vertex has not been visited
                if (!visited[v.key]) {

                    // check if its edge weight is more than the distance to it, if so
                    if (v.weight > distTo[v.key]) {
                        distTo[v.key] = v.weight;

                        // find and update the new distance
                        if (PQ.findVertex(v)) {
                            PQ.update(distTo[v.key], v);

                            // or insert it into the PQ
                        } else {
                            PQ.insert(distTo[v.key], v);
                        }
                    }
                }
                v = v.next;
            }
        }
        return distTo;
    }
}
