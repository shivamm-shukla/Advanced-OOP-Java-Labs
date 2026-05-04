package Lab03_SnakesAndLadders;

import java.util.HashMap;
import java.util.Map;

public class SnakesAndLaddersTest {

        public static void main(String[] args) {

            // ── Board Setup ──────────────────────────────────────────
            Map<Integer, Integer> snakes = new HashMap<>();
            snakes.put(16, 6);   snakes.put(47, 26);
            snakes.put(49, 11);  snakes.put(56, 53);
            snakes.put(62, 19);  snakes.put(64, 60);
            snakes.put(87, 24);  snakes.put(93, 73);
            snakes.put(98, 78);

            Map<Integer, Integer> ladders = new HashMap<>();
            ladders.put(2,  38); ladders.put(7,  14);
            ladders.put(8,  31); ladders.put(15, 26);
            ladders.put(21, 42); ladders.put(28, 84);
            ladders.put(36, 44); ladders.put(51, 67);
            ladders.put(71, 91); ladders.put(78, 98);

            SnakesAndLaddersBoard board =
                    new SnakesAndLaddersBoard(snakes, ladders);

            // ── Task 2: Minimum Rolls ────────────────────────────────
            System.out.println("====== Minimum Rolls ======");
            System.out.println("Minimum dice rolls to win: "
                    + board.findMinimumRolls());

            // ── Task 1: getPossibleMoves ─────────────────────────────
            System.out.println("\n====== Possible Moves from Square 1 ======");
            System.out.println("Moves: " + board.getPossibleMoves(1));

            // ── Task 1: getJumpDestination ───────────────────────────
            System.out.println("\n====== Jump Destinations ======");

            // Optional.ifPresent() — value ho tabhi print karo
            board.getJumpDestination(16).ifPresent(
                    dest -> System.out.println("Square 16 (snake)  -> " + dest));

            board.getJumpDestination(2).ifPresent(
                    dest -> System.out.println("Square 2  (ladder) -> " + dest));

            // Optional.orElse() — value na ho toh default do
            int dest = board.getJumpDestination(50).orElse(50);
            System.out.println("Square 50 (no jump) -> " + dest);

            // ── Task 1: isValidSquare ────────────────────────────────
            System.out.println("\n====== Square Validation ======");
            System.out.println("Is 50 valid?  " + board.isValidSquare(50));
            System.out.println("Is 101 valid? " + board.isValidSquare(101));

            // ── Task 3: Snake Positions ──────────────────────────────
            System.out.println("\n====== Snake Positions ======");
            System.out.println(board.getSnakePositions());

            // ── Task 3: Ladder Positions ─────────────────────────────
            System.out.println("\n====== Ladder Positions ======");
            System.out.println(board.getLadderPositions());

            // ── Task 3: Dangerous Squares ────────────────────────────
            System.out.println("\n====== Dangerous Squares (drop > 10) ======");
            System.out.println("Count: " + board.countDangerousSquares());

            // ── Task 3: Best Ladder ──────────────────────────────────
            System.out.println("\n====== Best Ladder ======");
            board.findBestLadder().ifPresent(
                    sq -> System.out.println("Best ladder starts at: " + sq));

            // ── Task 5: Validation Error Tests ──────────────────────
            System.out.println("\n====== Validation Tests ======");

            // Test 1: Snake at square 100
            try {
                Map<Integer, Integer> badSnakes = new HashMap<>();
                badSnakes.put(100, 50);
                new SnakesAndLaddersBoard(badSnakes, new HashMap<>());
            } catch (IllegalArgumentException e) {
                System.out.println("Caught: " + e.getMessage());
            }

            // Test 2: Ladder going down
            try {
                Map<Integer, Integer> badLadders = new HashMap<>();
                badLadders.put(50, 30);
                new SnakesAndLaddersBoard(new HashMap<>(), badLadders);
            } catch (IllegalArgumentException e) {
                System.out.println("Caught: " + e.getMessage());
            }

            // Test 3: Same square — snake aur ladder dono
            try {
                Map<Integer, Integer> s = new HashMap<>();
                Map<Integer, Integer> l = new HashMap<>();
                s.put(20, 5);
                l.put(20, 40);
                new SnakesAndLaddersBoard(s, l);
            } catch (IllegalArgumentException e) {
                System.out.println("Caught: " + e.getMessage());
            }
        }
    }

