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

### 🔹 Lab 04 – Segment Tree Implementation

- Efficient range query handling using **Segment Tree**
- Supports **range sum queries and point updates**
- Tree built using **array-based representation**
- Optimized space using **power-of-two padding**
- Recursive tree construction and query resolution

**Advanced Implementation:**
- Generic Segment Tree using **Java Generics (`<T>`)**
- Supports multiple operations via **`BinaryOperator<T>`**
- Implemented for **Sum, Minimum, and GCD queries**

---

### 🔹 Lab 05 – Behavioral Design Patterns

#### Observer Pattern (Stock Market System)

- Implements **publish-subscribe model**
- Multiple observers: Dashboard, Alerts, Logging, Mobile Push
- Loose coupling between subject and observers
- Fault-tolerant notification mechanism

#### Strategy Pattern (Delivery Pricing System)

- Multiple pricing strategies: Standard, Express, Scheduled, Subscription
- Runtime strategy switching using **setter injection**
- Demonstrates **Open/Closed Principle**
- Real-world pricing logic simulation

---

### 🔹 Lab 06 – Structural Design Patterns

#### Adapter Pattern (Weather API Integration)

- Unified interface for multiple third-party weather APIs
- Handles:
    - Unit conversion (Fahrenheit → Celsius)
    - Data format transformation
- Demonstrates integration of incompatible systems

#### Decorator Pattern (Message Processing System)

- Dynamic addition of behaviors:
    - Encryption
    - Compression
    - Timestamping
    - Translation
- Supports flexible chaining of operations
- Demonstrates **Open/Closed Principle**

---

### 🔹 Lab 07 – Refactoring: E-Commerce System

- Real-world system combining multiple design patterns:
    - **Strategy** (Discount handling)
    - **Adapter** (Payment gateway integration)
    - **Observer** (Order notifications)
- Clean separation of concerns
- Easily extensible architecture

---

### 🔹 Lab 08 – Creational Design Patterns

#### Factory Method Pattern

- Eliminates large conditional logic using **polymorphism**
- Parent class defines workflow (**Template Method**)
- Subclasses handle object creation
- Implemented using:
    - Abstract class version
    - Interface-based version
- Demonstrates **Open/Closed Principle**

#### Singleton Pattern

- Ensures single instance across application
- Implemented using:
    - **Double-Checked Locking (thread-safe)**
    - **Enum Singleton (recommended approach)**
- Demonstrates global access and shared state consistency

---

### 🔬 Lab 09 — Multithreading (Stock Trading Exchange Simulator)

#### ReentrantLock Producer-Consumer
- Implementation using **ReentrantLock and Condition**
- Supports multiple producers and consumers
- Avoids race conditions and deadlocks
- Uses `signalAll()` for proper thread coordination

#### Stock Exchange Simulator
- Real-world concurrency simulation of a live trading exchange
- **Immutable** `Order` and `MatchedPair` objects (inherently thread-safe)
- `OrderBook` using **ReentrantLock (fair=true)** + **two Conditions**:
  - `ordersAvailable` — engine waits here; traders signal on new order
  - `bookNotFull` — traders wait here; engine signals after consuming a match
- `ExchangeStats` using **AtomicInteger / AtomicLong** (lock-free counters) + **ReadWriteLock** for consistent price snapshots
- `Trader` implements **Callable\<String\>** — returns order summary via `Future`
- `MatchingEngine` implements **Callable\<String\>** — single-thread consumer
- `Dashboard` using **ScheduledExecutorService** (prints live stats every 2s)
- `StockExchange` wires everything: **newFixedThreadPool(3)** for 5 traders, **newSingleThreadExecutor()** for engine
- Full graceful shutdown: `shutdown()` → `awaitTermination()` → `shutdownNow()`
- `AtomicBoolean exchangeOpen` as shared shutdown signal across all threads

---

## 🧠 Concepts Covered

- Encapsulation *(Lab 01)*
- Abstraction *(Lab 01)*
- Inheritance & Method Overriding *(Lab 02, Lab 03)*
- Modular class design *(Lab 01, Lab 02)*
- Graph modeling using adjacency list *(Lab 01, Lab 02)*
- BFS traversal algorithm *(Lab 01, Lab 03)*

- Object-oriented problem modeling *(Lab 02)*
- Exception handling *(Lab 01, Lab 02, Lab 03)*

- Functional Programming (Streams, Lambdas, Predicates) *(Lab 03)*
- Null Safety using `Optional` *(Lab 03)*
- Immutability *(Lab 03)*

- Segment Tree (Range Queries) *(Lab 04)*
- Recursion and divide-and-conquer techniques *(Lab 04)*
- Generics and functional interfaces (`BinaryOperator`) *(Lab 04)*

- Behavioral Design Patterns (Observer, Strategy) *(Lab 05)*
- Polymorphism *(Lab 05, Lab 06, Lab 07)*
- Open/Closed Principle *(Lab 05, Lab 06, Lab 08)*

- Structural Design Patterns (Adapter, Decorator) *(Lab 06)*

- System design using multiple patterns *(Lab 07)*

- Creational Design Patterns (Factory Method, Singleton) *(Lab 08)*
- Template Method Pattern *(Lab 08)*
- Thread-safe design (Double-Checked Locking, Enum Singleton) *(Lab 08)*

- Concurrency and Multithreading *(Lab 09)*
- Synchronization using `ReentrantLock` and `Condition` *(Lab 09)*
- Immutability as thread-safety strategy *(Lab 09)*
- `volatile` visibility guarantee and `AtomicBoolean` for shutdown flags *(Lab 09)*
- `AtomicInteger` / `AtomicLong` lock-free CAS operations *(Lab 09)*
- `ReadWriteLock` — multiple concurrent readers, exclusive writer *(Lab 09)*
- `Callable` and `Future` for retrieving results from threads *(Lab 09)*
- Thread pool types: `newFixedThreadPool`, `newSingleThreadExecutor` *(Lab 09)*
- `ScheduledExecutorService` for periodic background tasks *(Lab 09)*
- Graceful executor shutdown pattern *(Lab 09)*
- Two-Condition pattern (targeted `signal()` vs wasteful `notifyAll()`) *(Lab 09)*
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
│   ├── Lab02_TravelGraph
│   ├── Lab03_SnakesAndLadders
│   ├── Lab04_SegmentTree
│   ├── Lab05_BehavioralDesignPatterns
│   ├── Lab06_StructuralDesignPatterns
│   ├── Lab07_Refactoring
│   ├── Lab08_CreationalDesignPattern
│   └── Lab09_multithreading
│
├── README.md
└── .gitignore