package Lab02_TravelGraph;

import java.util.List;

/*
 ..................Conceptual Questions Answers........................

 1. Stack vs Queue: Why does DFS use a Stack while BFS uses a Queue? What
 fundamental difference in exploration strategy does this create?
 Ans: DFS (Depth First Search) explores the graph by going as deep as possible before backtracking.
 A stack follows the LIFO (Last In, First Out) principle.
 This means:
 . The most recently discovered vertex is processed first.
 . The algorithm keeps moving forward along one branch.
 . When it reaches a dead end, it backtracks.
 Because of LIFO behavior, DFS naturally dives deep into one path before exploring
 others.
BFS (Breadth First Search) explores the graph level by level.
A queue follows the FIFO (First In, First Out) principle.
This means:
. The first discovered vertex is processed first.
. All neighbors at the current level are visited before moving to the next level.
Because of FIFO behavior, BFS expands outward and explores shallowest node from source
node.

 2. Immutability Benefits: What are three key advantages of making the City class
 immutable? How does this help with using City objects as Map keys?
 Ans: Making a class immutable means that once an object is created, its state cannot be changed.
For example, if City has:
. name
. country
. timeZone
After construction, these values cannot be modified.

Advantages:
-> Safety and Data Integrity
. No one can accidentaly change its name or country
. The object remains consistent throughout the program
. It prevents unexpected bugs caused by modification

-> Thread Safety
Immutable objects are automatically thread safe because:
. No thread can modify the object
. Multiple threads can safely read it at the same time

-> Reliable Use as Map Keys
For objects to work correctly as keys in a HashMap:
. hashCode() must remain consistent
. equals() must remain consistent

 3. Path Optimality: Does DFS guarantee finding the shortest path? If not, which
 algorithm would? Why?
 Ans: No, DFS does not guarantee the shortest path.
 DFS explores one path completely before exploring other alternatives, it goes as deep as possible in one direction before backtracking.
 Because of this behavior:
 . It may stuck in in an infinite loop
 . It may find a path quickly but that path may not be the shortest, it simply returns the first path it discovers.

 In unweighted graphs, BFS guarantees the shortest path because:
 . Explores level by level
 . Visits all nodes at distance 1 before distance 2
 So the first time it reaches the destination it must be through the minimum number of edges.

 4. Real-world Application: In a real flight booking system, what additional
 information would you add to make this more realistic? (Consider flight duration,
 cost, layover time)
 Ans: We must add additional information to make the system practical and realistic.
 -> Flight Duration
. Each connection should include:
. Total flight time (e.g., 7 hours 30 minutes)
. Departure time
. Arrival time
. Time zone differences

-> Ticket Cost
. Base ticket price
. Taxes
. Dynamic prices (based on demand)
. Class Type(Economy, business, First)

-> Layover Time
. Layover duration
. Minimum connection time
. Terminal change requirements

 5. Encapsulation: Identify and explain three examples of encapsulation in your
 design.
 Ans: Encapsulation is used in the following ways:
 -> Private Adjacency List (adjList)
. The graph’s internal structure is hidden from outside classes.
. No external class can directly modify the adjacency list.
. All modifications must go through controlled methods like:
    . addCity()
    . addConnection()
    . dfs()
    . bfs()

-> Controlled Modification Through Public Methods
Instead of allowing direct access to connections, we provide:
public void addCity(City city)
public void addConnection(City c1, City c2)
These methods:
. Validate inputs
. Check if cities exist
. Prevent duplicate connections
So external code cannot directly insert invalid data into the graph.

-> Internal Algorithm Logic Hidden (DFS & BFS)
My traversal logic:
public List<City> dfs(...)
public List<City> bfs(...)
Inside these methods:
. visited
. queue
. stack
. parent map
are all local and private to the method.
The internal working of the algorithm is hidden.
External classes only see:
graph.bfs(start, end);

*/

public class TrabelPlannerTest {
    public static void main(String[] args) {
        // Real World Cities
        City delhi = new City("Delhi", "India", "IST");
        City mumbai = new City("Mumbai", "India", "IST");
        City london = new City("London", "UK", "GMT");
        City paris = new City("Paris", "France", "CET");
        City dubai = new City("Dubai", "UAE", "GST");
        City newYork = new City("New York", "USA", "EST");
        City singapore = new City("Singapore", "Singapore", "SGT");
        City tokyo = new City("Tokyo", "Japan", "JST");
        City sydney = new City("Sydney", "Australia", "AEST");

        // City Constructor Validation

        try {
            City invalidCity1 = new City(null, "India", "IST");
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
        }
        // Create Graph
        TravelGraph graph = new TravelGraph();

        // Add Cities
        graph.addCity(delhi);
        graph.addCity(mumbai);
        graph.addCity(london);
        graph.addCity(paris);
        graph.addCity(dubai);
        graph.addCity(newYork);
        graph.addCity(singapore);
        graph.addCity(tokyo);
        graph.addCity(sydney);


        // Add Flight Connections
        graph.addConnection(delhi, dubai);
        graph.addConnection(dubai, london);
        graph.addConnection(london, newYork);
        graph.addConnection(delhi, mumbai);
        graph.addConnection(mumbai, singapore);
        graph.addConnection(singapore, tokyo);
        graph.addConnection(tokyo, sydney);
        graph.addConnection(sydney, newYork);



        // Test Case 1
        System.out.print("\nFlight: Delhi -> New York \nRoute: ");
        printRoute(graph.dfs(delhi, newYork));

        System.out.print("Shortest Route: ");
        printRoute(graph.bfs(delhi, newYork));

        // Test Case 2
        System.out.print("\nFlight: Mumbai -> Tokyo \nRoute: ");
        printRoute(graph.dfs(mumbai, tokyo));

        System.out.print("Shortest Route: ");
        printRoute(graph.bfs(mumbai, tokyo));

        //  Test Case 3
        System.out.print("\nFlight: Paris -> Singapore \nRoute: ");
        printRoute(graph.dfs(paris, singapore));

        System.out.print("Shortest Route: ");
        printRoute(graph.bfs(paris, singapore));

        // Test Case 4
        System.out.print("\nFlight: sydney -> tokyo \nRoute: ");
        printRoute(graph.dfs(sydney, tokyo));

        System.out.print("Shortest Route: ");
        printRoute(graph.bfs(sydney, tokyo));

        // findPath Null Parameter Check
        try {
            graph.dfs(null, mumbai);
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
        }
        try {
            graph.bfs(null, mumbai);
        } catch (IllegalArgumentException e) {
            System.out.println("Shortest path: " + e.getMessage());
        }

        // City Not Present in Graph
        City Bhinga = new City("Bhinga", "India", "IST");

        try {
            graph.dfs(delhi, Bhinga);
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
        }
        try {
            graph.bfs(delhi, Bhinga);
        } catch (IllegalArgumentException e) {
            System.out.println("Shortest path: " + e.getMessage());
        }
    }

    // Method to print travel route
    public static void printRoute(List<City> path) {

        if (path.isEmpty()) {
            System.out.println("No route found.");
            return;
        }

        for (int i = 0; i < path.size(); i++) {

            System.out.print(path.get(i).name());

            if (i != path.size() - 1) {
                System.out.print("  ->  ");
            }
        }

        System.out.println();
    }
}



