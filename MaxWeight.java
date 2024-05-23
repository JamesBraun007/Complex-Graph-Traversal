public class MaxWeight {

    public static class Triple {
        int in;
        int out1;
        int out2;

        public Triple(int in, int out1, int out2) {
            this.in = in;
            this.out1 = out1;
            this.out2 = out2;
        }
    }

    public static class Fruple {
        int L2;
        int L1in;
        int L1out1;
        int L1out2;

        public Fruple(int L2, int L1in, int L1out1, int L1out2) {
            this.L2 = L2;
            this.L1in = L1in;
            this.L1out1 = L1out1;
            this.L1out2 = L1out2;
        }
    }

    public static int maxWeightChain(Graph G) {
        boolean[] visited = new boolean[G.adjacencyList.length];

        // finding leaf for starting point
        Graph.Node leaf = findLeaf(G);

        // call recursiveChain to find the max weight chain
        assert leaf != null;
        Triple result = recursiveChain(G, leaf, visited);

        return Math.max(result.in, result.out1);
    }

    public static Graph.Node findLeaf(Graph G) {
        // for each vertex in the graph
        for (int i = 0; i < G.adjacencyList.length; i++) {
            int length = 0;
            Graph.Node head = G.adjacencyList[i];
            Graph.Node temp = head;

            // look through list of neighbors
            while (temp.next != null) {
                length++;
                temp = temp.next;
            }
            // if there is only one neighbor, then the vertex is a leaf
            if (length == 1) {
                return head;
            }
        }
        return null;
    }

    public static Triple recursiveChain(Graph G, Graph.Node vertex, boolean[] visited) {
        visited[vertex.key] = true;
        Graph.Node child = G.adjacencyList[vertex.key].next;

        // skip the parent of the vertex since it has already been visited
        if (visited[child.key]) {
            child = child.next;
        }

        // if we find a leaf then return the base case
        if (child == null) {
            return new Triple(vertex.weight, 0, 0);
        }

        // calculate the triple for the vertex's child
        Triple childTriple = recursiveChain(G, child, visited);

        // store the respective calculated in's and out's for the current vertex
        int inVertex = vertex.weight + Math.max(childTriple.out1, childTriple.out2);
        int out1Vertex = childTriple.in;
        int out2Vertex = childTriple.out1;

        // return the current vertex's triple
        return new Triple(inVertex, out1Vertex, out2Vertex);
    }

    public static int maxWeightTree(Graph G) {
        boolean[] visited = new boolean[G.adjacencyList.length];
        Fruple result = recursiveTree(G, G.adjacencyList[0], visited);

        return result.L2;
    }

    public static Fruple recursiveTree(Graph G, Graph.Node vertex, boolean[] visited) {
        visited[vertex.key] = true;
        Graph.Node child = G.adjacencyList[vertex.key].next;

        // skip the parent of the vertex since it has already been visited
        if (visited[child.key]) {
            child = child.next;
        }

        // encountered leaf since parent was already visited
        if (child == null) {
            return new Fruple(0, vertex.weight, 0, 0);
        }

        int L2 = 0;
        int L1in = 0;
        int L1out1 = 0;
        int L1out2 = 0;

        while (child != null) {
            // if child hasn't been visited, call recursiveTree on it
            if (!visited[child.key]) {
                Fruple childFruple = recursiveTree(G, child, visited);

                int maxOneL2 = Math.max(L2, childFruple.L2);
                int maxTwoL2 = Math.max(L1in + Math.max(childFruple.L1out1, childFruple.L1out2), L1out1 + Math.max(childFruple.L1in, childFruple.L1out1));
                int maxThreeL2 = Math.max(maxOneL2, maxTwoL2);

                L2 = Math.max(maxThreeL2, L1out2 + childFruple.L1in);
                L1in = Math.max(L1in, vertex.weight + Math.max(childFruple.L1out1, childFruple.L1out2));
                L1out1 = Math.max(L1out1, childFruple.L1in);
                L1out2 = Math.max(L1out2, childFruple.L1out1);
            }

            child = child.next;
        }
        return new Fruple(L2, L1in, L1out1, L1out2);
    }
}
