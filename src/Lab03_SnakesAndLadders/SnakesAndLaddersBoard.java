package Lab03_SnakesAndLadders;

import Lab01_Graph_BFS.Graph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SnakesAndLaddersBoard extends Graph
{
    private final Map<Integer, Integer> snakes;
    private final Map<Integer, Integer> ladders;

    private final Map<Integer, List<Integer>> directedAdjList;

    public SnakesAndLaddersBoard(Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
        super(101);

        validatePositions(snakes, ladders);

        // fresh immutable copy
        this.snakes = Map.copyOf(snakes);
        this.ladders = Map.copyOf(ladders);

        this.directedAdjList = new HashMap<>();

        // Build directed graph
        buildGraph();

    }

    @Override // To disallow bidirectional edges
    public void addEdge(int v1, int v2) { }

    @Override
    public void bfs(int start){
        System.out.println("Use findMinimumRolls() for this board");
    }

    // To add directed edge
    private void addDirectedEdge(int v1, int v2) {
        if (!isValidSquare(v1) || !isValidSquare(v2)){
            throw new IllegalArgumentException("Invalid square: " + v1+ " or " + v2);
        }
        directedAdjList.computeIfAbsent(v1, k -> new ArrayList<>()).add(v2);
    }

    // to build the graph
    private void buildGraph() {
        for (int square = 1; square <= 100; square++) {
            final int currentSquare = square;
            IntStream.rangeClosed(1, 6)
                    .map(dice -> currentSquare + dice)
                    .filter(next -> next <= 100)
                    .map(next -> getJumpDestination(next).orElse(next))
                    .forEach(dest -> addDirectedEdge(currentSquare, dest));
        }
    }

    // Task 1
    // 1. getJumpDestination

    public Optional<Integer> getJumpDestination(int square){
        if(snakes.containsKey(square)){
            return Optional.of(snakes.get(square));
        }
        if (ladders.containsKey(square)){
            return Optional.of(ladders.get(square));
        }
        return Optional.empty();
    }

    // 2. getPossibleMoves

    public List<Integer> getPossibleMoves(int currSqare){
        return IntStream.rangeClosed(1,6)
                .map(dice -> currSqare  + dice)
                .filter(sq -> sq <= 100)
                .map(sq -> getJumpDestination(sq).orElse(sq))
                .boxed()
                .collect(Collectors.toList());
    }

    // 3. isValidSquare

    public boolean isValidSquare(int square){
        return square >= 1 && square <= 100;
    }


    // Task2: findMinimumRolls - bfs

    public int findMinimumRolls(){
        Queue<int[]> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        queue.offer(new int[]{1,0});
        visited.add(1);

        while (!queue.isEmpty()){
            int[] curr = queue.poll();
            int square = curr[0];
            int rolls = curr[1];

            if (square == 100) return rolls;

            for (int neighbor : directedAdjList.getOrDefault(square, new ArrayList<>())){
                if (!visited.contains(neighbor)){
                    visited.add(neighbor);
                    queue.offer(new int[]{neighbor, rolls + 1});
                }
            }
        }
        return -1;
    }

//    Task 3
// 1. getSnakesPositions

    public List<Integer> getSnakePositions(){
        return snakes.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    // 2. getLadderPositions
    public List<Integer> getLadderPositions(){
        return ladders.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    // 3. countDangerousSquare
    public long countDangerousSquares(){
        return snakes.entrySet().stream()
                .filter(e -> (e.getKey() - e.getValue()) > 10)
                .count();
    }

    // 4. findBestLadder
    public Optional<Integer> findBestLadder(){
        return ladders.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue() - e.getKey()))
                .map(Map.Entry::getKey);
    }

    // Task4: validatePositions

    private void validatePositions(Map<Integer, Integer> snakes, Map<Integer, Integer> ladders){
        for (Map.Entry<Integer, Integer> e : snakes.entrySet()) {
            int from = e.getKey(), to = e.getValue();
            if (from < 1 || from > 100 || to < 1 || to > 100)
                throw new IllegalArgumentException(
                        "Snake invalid square: " + from + " -> " + to);
            if (from == 100)
                throw new IllegalArgumentException(
                        "Snake cannot start at square 100.");
            if (from <= to)
                throw new IllegalArgumentException(
                        "Snake must go DOWN. Invalid: " + from + " -> " + to);
        }

        // Ladder validation
        for (Map.Entry<Integer, Integer> e : ladders.entrySet()) {
            int from = e.getKey(), to = e.getValue();
            if (from < 1 || from > 100 || to < 1 || to > 100)
                throw new IllegalArgumentException(
                        "Ladder invalid square: " + from + " -> " + to);
            if (from == 100)
                throw new IllegalArgumentException(
                        "Ladder cannot start at square 100.");
            if (from >= to)
                throw new IllegalArgumentException(
                        "Ladder must go UP. Invalid: " + from + " -> " + to);
        }

        // Same square pe dono nahi ho sakte
        for (int sq : snakes.keySet()) {
            if (ladders.containsKey(sq))
                throw new IllegalArgumentException(
                        "Square " + sq + " cannot have both snake and ladder.");
        }
    }
}
