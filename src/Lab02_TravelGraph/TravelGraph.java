package Lab02_TravelGraph;

import java.util.*;

public class TravelGraph {

    private final Map<City, List<City>> adjList;

    public TravelGraph() {
        this.adjList = new HashMap<>();
    }

    // Add city
    public void addCity(City city) {
        if (adjList.containsKey(city)) {
            System.out.println("City already exists");
        } else {
            adjList.put(city, new ArrayList<>());
        }
    }

    // Add undirected connection
    public void addConnection(City c1, City c2) {

        if (!adjList.containsKey(c1)) {
            System.out.println("First add city " + c1.name());
            return;
        }

        if (!adjList.containsKey(c2)) {
            System.out.println("First add city " + c2.name());
            return;
        }

        if (adjList.get(c1).contains(c2)) {
            System.out.println("There is already a connection");
            return;
        }

        // Add connection from the both sides
        adjList.get(c1).add(c2);
        adjList.get(c2).add(c1);
    }

    // DFS traversal
    public List<City> dfs(City start, City end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start or end cities cannot be null");
        }

        if (!adjList.containsKey(start) || !adjList.containsKey(end)) {
            throw new IllegalArgumentException(start.name() + " or " + end.name() + " don't exist in the graph");
        }

        Deque<City> stack = new ArrayDeque<>();
        Set<City> visited = new HashSet<>();
        Map<City, City> parents = new HashMap<>();

        stack.push(start);
        visited.add(start);
        parents.put(start, null);

        while (!stack.isEmpty()) {

            City current = stack.pop();

            if (current.equals(end)) {
                List<City> path = new ArrayList<>();
                City temp = end;
                while(temp != null){
                    path.add(temp);
                    temp = parents.get(temp);
                }
                Collections.reverse(path); // reverse to get path from start to end
                return path;
            }


            for (City neighbour : adjList.getOrDefault(current, new ArrayList<>())) {
                if (!visited.contains(neighbour)) {
                    stack.push(neighbour);
                    parents.put(neighbour, current);
                    visited.add(neighbour);
                }
            }
        }
      //  if end not found
        return new ArrayList<>();
    }


    // BFS traversal
    public List<City> bfs(City start, City end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException("start or end cities cannot be null");
        }

        if (!adjList.containsKey(start) || !adjList.containsKey(end)) {
            throw new IllegalArgumentException(start.name() + " or " + end.name() + " don't exist in the graph");
        }

        Queue<City> queue = new ArrayDeque<>();
        Set<City> visited = new HashSet<>();
        Map<City, City> parent = new HashMap<>();

        queue.offer(start);
        visited.add(start);
        parent.put(start, null);   // since start has no parent

        while (!queue.isEmpty()) {

            City current = queue.poll();

            // If destination found, reconstruct path
            if (current.equals(end)) {

                List<City> path = new ArrayList<>();
                City temp = end;

                while (temp != null) {
                    path.add(temp);
                    temp = parent.get(temp);
                }

                Collections.reverse(path);   // reverse to get path from start to end
                return path;
            }

            for (City neighbour : adjList.getOrDefault(current, new ArrayList<>())) {

                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    parent.put(neighbour, current);  // store parent
                    queue.offer(neighbour);
                }
            }
        }
        // If no path found
        return new ArrayList<>();
    }
}
