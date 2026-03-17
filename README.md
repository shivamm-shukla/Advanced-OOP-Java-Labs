# Advanced OOP Java Labs

This repository contains a collection of lab assignments completed as part of the **Advanced Object-Oriented Programming** course.

The implementations focus on applying core OOP principles and algorithmic problem-solving using Java.

---

## 📚 Implemented Labs

### 🔹 Lab 01 – Graph & BFS Implementation

- Graph implemented using **Adjacency List**
- Adjacency structure built using **Map-based representation**
- Breadth First Search (BFS) traversal
- Input validation and exception handling
- Modular class design

**Key Implementation Detail:**
The graph is represented using a `Map<Integer, List<Integer>>` to efficiently model connections between vertices.

---

### 🔹 Lab 02 – Travel Graph System

- Object-oriented modeling of cities and routes
- Custom graph structure for representing travel connections
- Separation of concerns (City, Graph, Test classes)
- Validation and structured exception handling

---

### 🔹 Lab 03 – Snakes and Ladders (Functional Programming & Null Safety)

- Snakes and Ladders board modeled as a **directed graph**
- Extends `Graph` class from Lab 01 using **inheritance**
- **Immutable** snake and ladder positions using `Map.copyOf()`
- Minimum dice rolls to win calculated using **BFS**
- `Optional<Integer>` used for null-safe jump destination handling
- **Stream API** and **lambda expressions** for functional game logic
- Input validation with `IllegalArgumentException` for invalid board configurations

**Key Implementation Detail:**
The board overrides `addEdge()` to block bidirectional edges and maintains its own directed adjacency list to model one-way moves including snake and ladder jumps.

---

(More labs will be added progressively.)

---

## 🧠 Concepts Covered

- Encapsulation
- Abstraction
- Inheritance & Method Overriding
- Modular class design
- Graph modeling using adjacency list
- BFS traversal algorithm
- Functional Programming (Streams, Lambdas, Predicates)
- Null Safety using `Optional`
- Immutability
- Exception handling
- Object-oriented problem modeling

---

## 🛠 Tools & Technologies

- Java
- IntelliJ IDEA
- Git & GitHub

---

## 📁 Repository Structure
```text
advanced-oop-java-labs
│
├── src
│   ├── Lab01_Graph_BFS
│   │   ├── Graph.java
│   │   └── BFSTest.java
│   │
│   ├── Lab02_TravelGraph
│   │   ├── City.java
│   │   ├── TravelGraph.java
│   │   └── TravelPlannerTest.java
│   │
│   └── Lab03_SnakesAndLadders
│       ├── SnakesAndLaddersBoard.java
│       └── SnakesAndLaddersTest.java
│
├── README.md
└── .gitignore
```

<sub>Note: README documentation was structured with the assistance of AI tools.</sub>