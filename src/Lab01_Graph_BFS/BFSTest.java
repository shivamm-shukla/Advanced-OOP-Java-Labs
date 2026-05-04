package Lab01_Graph_BFS;

/*
............................................Coneptual Questions...........................................................

Ques-01: Why does BFS require a queue instead of a stack? What would happen if we used a stack instead? What traversal pattern would that produce?
Ans: BFS requires a queue because it must process vertices in the order they are discovered, ensuring level-by-level traversal. A queue follows FIFO order, which guarantees that all vertices at distance d are visited before vertices at distance d+1. If a stack were used instead, the traversal would become depth-first (DFS), exploring as deep as possible before backtracking.

Ques-02: What happens if we don't track visited vertices? Explain the consequences with an example of a graph with a cycle.
Ans: If we do not keep track of visited vertices in BFS or DFS:
    1. The algorithm may visit the same node again and again.
    2. In graphs with cycles, it can go into an infinite loop.
    3. The program may never stop.
    4. Memory usage will increase continuously.
Example ->
Consider this graph:
0 — 1
|   |
3 — 2
This graph contains a cycle:
0 → 1 → 2 → 3 → 0
If we start BFS from 0 and do not track visited vertices:
. 0 goes to 1
. 1 goes to 2
. 2 goes to 3
. 3 goes back to 0
. 0 again goes to 1
. The process continues forever
Without a visited set, the algorithm will never stop in cyclic graphs.
Tracking visited vertices ensures each vertex is processed only once.

Ques-03: How would BFS change if the graph was directed? Would the algorithm logic need to be modified? Would addEdge() need changes?
Ans: If the graph is directed, BFS logic remains almost the same.
-> The main difference is:
. Edges have direction.
. Traversal only follows outgoing edges.

-> Algorithm Logic:
The BFS algorithm itself does not need modification.
It still uses:
. A queue
. A visited set
. Level-wise traversal

-> Change Required:
The addEdge() method must change.

-> In an undirected graph:
adjList.get(v1).add(v2);
adjList.get(v2).add(v1);

-> In a directed graph:
adjList.get(v1).add(v2);
Only one direction is added.

Ques-04: Where is encapsulation used in your design? Identify at least three examples of encapsulation in your Graph class and explain why each is important.
Ans: Encapsulation means hiding internal data and exposing only necessary functionality.
-> Three examples in the Graph class:
1. Private Adjacency List
private Map<Integer, List<Integer>> adjList;
. External classes cannot modify graph structure directly.
. Prevents accidental corruption.
. Maintains data integrity.

2. Controlled Access Through Public Methods
public void addEdge(...)
public void bfs(...)
-> These methods:
. Validate inputs
. Control how edges are added
. Prevent invalid operations
This ensures consistency of graph structure.

3. Local Traversal Variables
Inside BFS:
Queue<Integer> queue
Set<Integer> visited
-> These are local variables.
. Not accessible outside the method.
. Protect internal working of algorithm.
. Allow implementation changes without affecting other classes.
Encapsulation improves safety, maintainability, and reliability.

Ques-05: What is the time complexity of your BFS implementation? Explain in terms of V (vertices) and E (edges). What is the space complexity?
Ans: Time Complexity: O(V + E)
-> Where:
. V = number of vertices
. E = number of edges
-> Explanation:
. Each vertex is visited exactly once → O(V)
. Each edge is examined exactly once (in adjacency list) → O(E)
So total time complexity is:
O(V + E)

Space Complexity: O(V)
-> Space is used for:
. Visited set → O(V)
. Queue → O(V)
. Adjacency list (already stored) → O(V + E)
Extra space used by BFS itself is:
O(V)


 */

public class BFSTest {

    public static void main(String[] args) {

        // Normal Case
        System.out.println("Normal Case:");
        Graph g1 = new Graph(4);
        g1.addEdge(0,1);
        g1.addEdge(0,2);
        g1.addEdge(1,3);
        g1.addEdge(2,3);
        g1.bfs(0);

        // Single Vertex
        System.out.println("Single Vertex:");
        Graph g2 = new Graph(1);
        g2.bfs(0);

        // Disconnected Graph
        System.out.println("Disconnected Graph:");
        Graph g3 = new Graph(6);
        g3.addEdge(0,1);
        g3.addEdge(0,2);
        g3.addEdge(3,4);
        g3.addEdge(3,5);
        g3.bfs(0);

        // Complete Graph
        System.out.println("Complete Graph:");
        Graph g4 = new Graph(4);
        g4.addEdge(0,1);
        g4.addEdge(0,2);
        g4.addEdge(0,3);
        g4.addEdge(1,2);
        g4.addEdge(1,3);
        g4.addEdge(2,3);
        g4.bfs(0);

        // Different Starting Points
        System.out.println("Different Start Vertex:");
        g1.bfs(2);

        // Exception Handling Tests
        try {
            Graph g5 = new Graph(0);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception caught: " + e.getMessage());
        }

        try {
            g1.addEdge(0, 10);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception caught: " + e.getMessage());
        }

        try {
            g1.bfs(10);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception caught: " + e.getMessage());
        }
    }
}
