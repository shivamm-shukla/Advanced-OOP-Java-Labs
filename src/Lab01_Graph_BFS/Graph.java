package Lab01_Graph_BFS;

import java.util.*;

public class Graph {
    private int numVertices;
    private int numEdges;
    private HashMap<Integer, ArrayList<Integer>> adjList;

    // Constructor with validation
    public Graph(int numVertices) {
        if (numVertices < 1) {
            throw new IllegalArgumentException("Number of vertices must be at least 1.");
        }
        this.numVertices = numVertices;
        this.numEdges = 0;
        this.adjList = new HashMap<>();

        // Initialize empty adjacency list for all vertices
        for (int i = 0; i < numVertices; i++) {
            adjList.put(i, new ArrayList<>());
        }
    }

    public int getNumOfVertices(){
        return this.numVertices;
    }

    public int getNumOfEdges(){
        return this.numEdges;
    }

    // Add edge
    public void addEdge(int v1, int v2){

        if (v1 < 0 || v2 < 0 || v1 >= numVertices || v2 >= numVertices) {
            throw new IllegalArgumentException("Invalid vertex number. Must be between 0 and " + (numVertices - 1));
        }

        adjList.get(v1).add(v2);
        adjList.get(v2).add(v1);

        numEdges++;
    }

    // BFS traversal
    public void bfs(int startVertex){

        if (startVertex < 0 || startVertex >= numVertices) {
            throw new IllegalArgumentException("Invalid start vertex. Must be between 0 and " + (numVertices - 1));
        }

        Queue<Integer> queue = new ArrayDeque<>();
        HashSet<Integer> visited = new HashSet<>();

        queue.offer(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()){
            int vertex = queue.poll();
            System.out.print(vertex + " ");   // small formatting change

            for(Integer neighbor : adjList.getOrDefault(vertex, new ArrayList<>())){
                if (!visited.contains(neighbor)){
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        System.out.println();
    }
}
