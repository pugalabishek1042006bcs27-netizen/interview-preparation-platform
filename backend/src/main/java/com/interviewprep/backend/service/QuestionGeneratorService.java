package com.interviewprep.backend.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionGeneratorService {

    private static final Map<String, Map<String, List<Map<String, String>>>> QUESTION_BANK = new HashMap<>();

    static {

        // ── JavaScript ────────────────────────────────────────────────────────────
        QUESTION_BANK.put("JavaScript", Map.of(
            "Easy", List.of(
                q("What is the difference between == and === in JavaScript?",
                  "'==' checks value with type coercion. '===' checks value AND type without coercion. Always prefer === for predictable comparisons."),
                q("What does 'use strict' do in JavaScript?",
                  "Enforces stricter parsing, prevents undeclared variables, and disables confusing legacy features like with or duplicate parameter names."),
                q("What is a callback function in JavaScript?",
                  "A function passed as an argument and invoked later. Example: setTimeout(() => console.log('done'), 1000).")
            ),
            "Medium", List.of(
                q("Explain the event loop in JavaScript.",
                  "JavaScript is single-threaded. The event loop monitors the call stack and task queue, moving callbacks to the stack when it's empty, enabling async behavior."),
                q("What is the difference between Promise and async/await?",
                  "Both handle async ops. Promises use .then()/.catch() chaining. async/await is syntactic sugar that makes async code look synchronous and easier to read."),
                q("What are WeakMap and WeakSet in JavaScript?",
                  "They hold weak references to objects so those objects can still be garbage collected. Useful for private metadata without memory leaks.")
            ),
            "Hard", List.of(
                q("Explain prototypal inheritance in JavaScript.",
                  "Objects inherit directly from other objects via the prototype chain ([[Prototype]]). Property lookup traverses up until null."),
                q("What is the Temporal Dead Zone (TDZ)?",
                  "TDZ is the period from block start to the let/const declaration. Accessing the variable in TDZ throws ReferenceError."),
                q("How does JavaScript garbage collection work?",
                  "Uses mark-and-sweep: marks all reachable objects from roots (global, stack), then sweeps unreachable ones. WeakRef/WeakMap entries don't prevent GC.")
            )
        ));

        // ── React ─────────────────────────────────────────────────────────────────
        QUESTION_BANK.put("React", Map.of(
            "Easy", List.of(
                q("What is JSX in React?",
                  "JSX is a syntax extension allowing HTML-like code in JS. Babel transforms it into React.createElement() calls."),
                q("What is the difference between props and state in React?",
                  "Props are read-only inputs from a parent. State is mutable data managed within a component that triggers re-renders.")
            ),
            "Medium", List.of(
                q("Explain the useEffect hook and its dependency array.",
                  "useEffect runs side effects. Empty array = once on mount, no array = every render, listed values = when those change."),
                q("What is the Context API and when should you use it?",
                  "Passes data through the tree without prop drilling. Best for global data like theme, user auth, or locale.")
            ),
            "Hard", List.of(
                q("Explain React reconciliation and the Fiber architecture.",
                  "Reconciliation diffs virtual DOM trees. Fiber enables incremental rendering, splitting work into chunks for better responsiveness."),
                q("What are React Server Components?",
                  "Render on the server without adding to the JS bundle. Can access backend resources directly. Client Components handle interactivity.")
            )
        ));

        // ── Java ──────────────────────────────────────────────────────────────────
        QUESTION_BANK.put("Java", Map.of(
            "Easy", List.of(
                q("What is the difference between JDK, JRE, and JVM?",
                  "JVM executes bytecode. JRE = JVM + libraries needed to run Java programs. JDK = JRE + development tools (compiler, debugger)."),
                q("What is the difference between == and .equals() in Java?",
                  "== compares object references (memory address). .equals() compares object content/value. Always use .equals() for String comparison."),
                q("What are the four pillars of OOP in Java?",
                  "Encapsulation (hiding data), Abstraction (hiding implementation), Inheritance (reusing parent class), Polymorphism (many forms via override/overload).")
            ),
            "Medium", List.of(
                q("What is the difference between interface and abstract class in Java?",
                  "Abstract classes can have state and concrete methods. Interfaces define a contract (all abstract before Java 8). A class can implement multiple interfaces but extend only one abstract class."),
                q("What is the difference between ArrayList and LinkedList in Java?",
                  "ArrayList uses a dynamic array (fast random access, slow insert/delete in middle). LinkedList uses doubly-linked nodes (slow access, fast insert/delete at ends)."),
                q("What is the Java Collections hierarchy?",
                  "Collection → List (ArrayList, LinkedList), Set (HashSet, TreeSet), Queue. Map (HashMap, TreeMap) is separate. Iterator is the traversal mechanism.")
            ),
            "Hard", List.of(
                q("What is the Java Memory Model (JMM) and what is the volatile keyword?",
                  "JMM defines how threads interact through memory. volatile ensures visibility of changes across threads and prevents instruction reordering."),
                q("What is the difference between synchronized and ReentrantLock in Java?",
                  "synchronized is simpler and auto-releases. ReentrantLock is more flexible: supports tryLock, interruptible locking, fairness, and multiple conditions."),
                q("Explain garbage collection in Java and the G1 GC.",
                  "Java uses generational GC (Young/Old gen). G1 (Garbage-First) divides heap into regions, prioritizes those with most garbage for efficient collection with predictable pause times.")
            )
        ));

        // ── Python ────────────────────────────────────────────────────────────────
        QUESTION_BANK.put("Python", Map.of(
            "Easy", List.of(
                q("What is the difference between a list and a tuple in Python?",
                  "Lists are mutable (can be changed). Tuples are immutable (cannot be changed after creation). Tuples are faster and used for fixed data."),
                q("What are Python decorators?",
                  "Decorators are functions that wrap another function to extend its behavior. @functools.wraps preserves the original function metadata. Example: @login_required."),
                q("What is PEP 8?",
                  "PEP 8 is Python's official style guide covering naming conventions, indentation (4 spaces), line length (79 chars), and code structure best practices.")
            ),
            "Medium", List.of(
                q("What is the difference between *args and **kwargs in Python?",
                  "*args passes a variable number of positional arguments as a tuple. **kwargs passes keyword arguments as a dictionary. Both allow flexible function signatures."),
                q("What are Python generators and how do they differ from lists?",
                  "Generators use yield to produce values lazily — one at a time. They are memory-efficient for large datasets since they don't store all values in memory at once."),
                q("What is the GIL (Global Interpreter Lock) in Python?",
                  "GIL is a mutex that allows only one thread to execute Python bytecode at a time. It limits CPU-bound parallelism in threads; use multiprocessing for true parallelism.")
            ),
            "Hard", List.of(
                q("What are Python metaclasses?",
                  "A metaclass is the 'class of a class' — it controls how classes are created. type is the default metaclass. Custom metaclasses use __new__ and __init__ to modify class creation."),
                q("Explain Python's memory management and reference counting.",
                  "CPython uses reference counting: each object has a refcount. When it reaches 0, memory is freed. A cyclic garbage collector handles reference cycles."),
                q("What is the difference between deepcopy and shallow copy in Python?",
                  "Shallow copy creates a new container but references the same inner objects. Deep copy recursively copies all nested objects. Use copy.deepcopy() for full independence.")
            )
        ));

        // ── Data Structures & Algorithms ──────────────────────────────────────────
        QUESTION_BANK.put("DSA", Map.of(
            "Easy", List.of(
                q("What is the time complexity of binary search?",
                  "O(log n) — binary search halves the search space at each step, requiring only log₂(n) comparisons for a sorted array of n elements."),
                q("What is the difference between a stack and a queue?",
                  "Stack: LIFO (Last-In-First-Out) — push/pop from top. Queue: FIFO (First-In-First-Out) — enqueue at rear, dequeue from front.")
            ),
            "Medium", List.of(
                q("Explain the difference between BFS and DFS graph traversal.",
                  "BFS uses a queue and explores level by level — good for shortest path. DFS uses a stack/recursion and explores depth-first — good for cycle detection and topological sort."),
                q("What is a Hash Map and how does it handle collisions?",
                  "A HashMap maps keys to values via a hash function. Collisions (two keys → same bucket) are handled by chaining (linked list at bucket) or open addressing (probe for next slot)."),
                q("What is dynamic programming and when is it applicable?",
                  "DP breaks problems into overlapping subproblems and stores results (memoization/tabulation). Applicable when optimal substructure and overlapping subproblems exist (e.g. Fibonacci, Knapsack).")
            ),
            "Hard", List.of(
                q("Explain Dijkstra's algorithm and its time complexity.",
                  "Greedy algorithm for shortest path from source to all vertices in a weighted graph. Uses a min-priority queue. Time: O((V + E) log V) with a binary heap."),
                q("What is the difference between Prim's and Kruskal's algorithms for MST?",
                  "Prim's grows the MST from a starting vertex (vertex-set approach). Kruskal's sorts all edges and uses Union-Find to greedily add edges without cycles (edge-set approach)."),
                q("Explain the concept of amortized time complexity.",
                  "Amortized analysis averages the cost over a sequence of operations. Example: dynamic array doubling is O(n) occasionally but O(1) amortized per append.")
            )
        ));

        // ── Operating Systems ─────────────────────────────────────────────────────
        QUESTION_BANK.put("Operating Systems", Map.of(
            "Easy", List.of(
                q("What is the difference between a process and a thread?",
                  "A process has its own memory space and resources. Threads share the same memory space within a process. Threads are lighter to create and switch between."),
                q("What is a context switch?",
                  "A context switch is when the OS saves the state of a running process/thread and loads the state of another. It has overhead due to saving/restoring registers and cache invalidation.")
            ),
            "Medium", List.of(
                q("Explain deadlock and the four necessary conditions for it.",
                  "Deadlock requires: Mutual Exclusion, Hold and Wait, No Preemption, and Circular Wait. Removing any one condition prevents deadlock."),
                q("What is virtual memory and how does paging work?",
                  "Virtual memory lets processes use more memory than physically available. Paging divides memory into fixed-size pages. The OS maps virtual pages to physical frames using a page table."),
                q("What is the difference between preemptive and non-preemptive scheduling?",
                  "Preemptive: OS can interrupt a running process (e.g. Round Robin). Non-preemptive: process runs until it voluntarily yields or blocks (e.g. FCFS, SJF non-preemptive).")
            ),
            "Hard", List.of(
                q("Explain the difference between mutex and semaphore.",
                  "Mutex is a binary lock owned by a thread — only the owner can release it. Semaphore is a counter for resource access control and can be signaled by any thread. Use mutexes for mutual exclusion, semaphores for signaling."),
                q("What is thrashing in operating systems?",
                  "Thrashing occurs when a process spends more time swapping pages in/out than executing. Caused by insufficient physical memory relative to working set size. Fixed by reducing multiprogramming degree or adding RAM."),
                q("Explain the different disk scheduling algorithms.",
                  "FCFS (in order), SSTF (shortest seek time first — starvation risk), SCAN/Elevator (sweeps back and forth), C-SCAN (circular variant for uniform wait times).")
            )
        ));

        // ── Computer Networks ─────────────────────────────────────────────────────
        QUESTION_BANK.put("Computer Networks", Map.of(
            "Easy", List.of(
                q("What is the OSI model and its 7 layers?",
                  "Physical, Data Link, Network, Transport, Session, Presentation, Application. Remember: 'Please Do Not Throw Sausage Pizza Away'. Each layer has specific protocols."),
                q("What is the difference between TCP and UDP?",
                  "TCP: connection-oriented, reliable, ordered, slower (e.g. HTTP, FTP). UDP: connectionless, unreliable, faster, no ordering (e.g. video streaming, DNS, gaming).")
            ),
            "Medium", List.of(
                q("What is the TCP 3-way handshake?",
                  "Client sends SYN → Server replies SYN-ACK → Client sends ACK. This establishes a TCP connection. 4-way termination uses FIN/ACK pairs."),
                q("What is the difference between IPv4 and IPv6?",
                  "IPv4: 32-bit address (4.3B addresses, nearly exhausted). IPv6: 128-bit address (340 undecillion addresses), no NAT needed, built-in IPsec, no broadcast (uses multicast)."),
                q("What is a subnet mask and how does CIDR notation work?",
                  "Subnet mask divides IP into network and host parts. CIDR (192.168.1.0/24) indicates the number of bits for the network portion (24 here), defining the subnet range.")
            ),
            "Hard", List.of(
                q("Explain how TLS handshake works.",
                  "Client hello → Server hello + certificate → Client verifies cert, sends pre-master secret (encrypted with server's public key) → Both derive session keys → Encrypted communication begins."),
                q("What is BGP and how does internet routing work?",
                  "BGP (Border Gateway Protocol) is the routing protocol of the internet. ISPs exchange routing information via BGP. It's path-vector based and selects routes by policy, prefix length, and AS path.")
            )
        ));

        // ── Database ──────────────────────────────────────────────────────────────
        QUESTION_BANK.put("Database", Map.of(
            "Easy", List.of(
                q("What is the difference between a primary key and a foreign key?",
                  "Primary key uniquely identifies each row. Foreign key references the primary key of another table to establish a relationship."),
                q("What is normalization?",
                  "Organizing data to reduce redundancy. 1NF: atomic values. 2NF: no partial dependencies. 3NF: no transitive dependencies. Reduces update anomalies.")
            ),
            "Medium", List.of(
                q("What is a database transaction and what are ACID properties?",
                  "Transaction = unit of work. ACID: Atomicity (all or nothing), Consistency (data stays valid), Isolation (concurrent transactions don't interfere), Durability (committed data persists)."),
                q("Explain INNER JOIN vs LEFT JOIN vs FULL OUTER JOIN.",
                  "INNER JOIN: only matching rows. LEFT JOIN: all left rows + matched right (NULLs for no match). FULL OUTER JOIN: all rows from both tables with NULLs where no match.")
            ),
            "Hard", List.of(
                q("What is database sharding and when would you use it?",
                  "Splitting data horizontally across multiple databases. Use when single DB can't handle load. Challenges: cross-shard queries, rebalancing, distributed transactions."),
                q("Explain optimistic vs pessimistic locking.",
                  "Optimistic: assumes no conflict, checks version on update, fails if changed. Pessimistic: locks row on read to block concurrent modification. Optimistic is better for low-contention scenarios.")
            )
        ));

        // ── Spring Boot ───────────────────────────────────────────────────────────
        QUESTION_BANK.put("Spring Boot", Map.of(
            "Easy", List.of(
                q("What is Spring Boot and how does it differ from Spring Framework?",
                  "Spring Boot is an opinionated framework built on Spring. It provides auto-configuration, embedded servers (Tomcat), and starter dependencies, removing boilerplate XML configuration."),
                q("What is the purpose of @SpringBootApplication?",
                  "It combines @Configuration, @EnableAutoConfiguration, and @ComponentScan. It marks the main class and triggers component scanning and auto-configuration.")
            ),
            "Medium", List.of(
                q("What is Spring dependency injection and what types does it support?",
                  "DI provides dependencies to beans rather than having them create dependencies. Spring supports constructor injection (preferred), setter injection, and field injection (@Autowired)."),
                q("What is the difference between @Component, @Service, @Repository, and @Controller?",
                  "All are @Component specializations. @Service marks business logic. @Repository marks data access (adds exception translation). @Controller marks MVC controllers.")
            ),
            "Hard", List.of(
                q("Explain Spring Security's filter chain.",
                  "Spring Security uses a chain of servlet filters. Each request passes through: CORS → CSRF → Authentication → Authorization. Custom filters can be inserted at specific positions."),
                q("What is Spring Data JPA and how does it relate to Hibernate?",
                  "Spring Data JPA provides repository abstractions on top of JPA. Hibernate is the default JPA provider. Spring Data generates query implementations from method names (e.g. findByEmailAndActive).")
            )
        ));

        // ── Machine Learning ──────────────────────────────────────────────────────
        QUESTION_BANK.put("Machine Learning", Map.of(
            "Easy", List.of(
                q("What is the difference between supervised and unsupervised learning?",
                  "Supervised: trained on labeled data (classification, regression). Unsupervised: finds patterns in unlabeled data (clustering, dimensionality reduction)."),
                q("What is overfitting and how do you prevent it?",
                  "Overfitting: model learns training data too well and performs poorly on new data. Prevention: more data, regularization (L1/L2), dropout, cross-validation, early stopping.")
            ),
            "Medium", List.of(
                q("What is the bias-variance tradeoff?",
                  "Bias: error from wrong assumptions (underfitting). Variance: error from sensitivity to training data fluctuations (overfitting). Goal: find the sweet spot that minimizes total error."),
                q("What is gradient descent and its variants?",
                  "Gradient descent minimizes loss by moving opposite to the gradient. Variants: Batch GD (all data), SGD (one sample), Mini-batch GD (subset). Adaptive: Adam, RMSprop, Adagrad.")
            ),
            "Hard", List.of(
                q("Explain the Transformer architecture.",
                  "Transformers use self-attention mechanisms instead of recurrence. Multi-head attention lets the model focus on different parts simultaneously. Positional encoding preserves sequence order. Basis for GPT, BERT."),
                q("What is the difference between L1 and L2 regularization?",
                  "L1 (Lasso): adds |weights| to loss → produces sparse weights, useful for feature selection. L2 (Ridge): adds weights² to loss → shrinks all weights evenly, better for correlated features.")
            )
        ));

        // ── C++ ───────────────────────────────────────────────────────────────────
        QUESTION_BANK.put("C++", Map.of(
            "Easy", List.of(
                q("What is the difference between references and pointers in C++?",
                  "References are aliases that must be initialized and can't be null or reassigned. Pointers hold memory addresses, can be null, and can be reassigned. References are safer."),
                q("What is RAII in C++?",
                  "Resource Acquisition Is Initialization: resources (memory, file handles) are acquired in constructors and released in destructors. Ensures cleanup even when exceptions occur. Example: std::unique_ptr.")
            ),
            "Medium", List.of(
                q("What is the difference between stack and heap memory in C++?",
                  "Stack: automatically managed, fast, limited size, stores local variables. Heap: manually managed (new/delete), large, slower allocation, stores dynamic data. Smart pointers automate heap management."),
                q("What are virtual functions and how does vtable work?",
                  "Virtual functions enable runtime polymorphism. C++ compiler creates a vtable (array of function pointers) for each class with virtual functions. Objects store a vptr pointing to their vtable.")
            ),
            "Hard", List.of(
                q("Explain move semantics and rvalue references in C++11.",
                  "Rvalue references (&&) bind to temporary objects. Move semantics transfer resources instead of copying (e.g. std::move). This avoids expensive deep copies for containers like std::vector."),
                q("What is template metaprogramming in C++?",
                  "Using C++ templates to perform computations at compile time. Can implement type traits, conditional types, and even Turing-complete algorithms resolved before runtime.")
            )
        ));

        // ── System Design ─────────────────────────────────────────────────────────
        QUESTION_BANK.put("System Design", Map.of(
            "Medium", List.of(
                q("How would you design a URL shortener like bit.ly?",
                  "Use a hash function (Base62 encoded) to map long URLs to short codes. Store in DB (short→long). Use caching (Redis) for popular URLs. Handle redirects with 301/302. Scale with CDN and read replicas."),
                q("How would you design a rate limiter?",
                  "Use Token Bucket or Sliding Window algorithm. Store request counts in Redis with TTL. Apply at API gateway level. Return 429 Too Many Requests when limit exceeded.")
            ),
            "Hard", List.of(
                q("How would you design a distributed message queue like Kafka?",
                  "Partitioned log-based storage. Producers write to partitions. Consumer groups read independently. Brokers replicate partitions. ZooKeeper/KRaft for leader election. Offsets track consumer position."),
                q("How would you design a globally distributed database?",
                  "Use consistent hashing for sharding. Multi-region replication with configurable consistency (strong/eventual). Leader election via Raft/Paxos. Conflict resolution via CRDTs or last-write-wins.")
            )
        ));

        // ── Web ───────────────────────────────────────────────────────────────────
        QUESTION_BANK.put("Web", Map.of(
            "Easy", List.of(
                q("What is the difference between HTTP and HTTPS?",
                  "HTTPS adds SSL/TLS encryption to HTTP, ensuring confidentiality, integrity, and authentication of data in transit. Uses port 443 vs HTTP's 80."),
                q("What is semantic HTML?",
                  "Using meaningful elements (<header>, <article>, <nav>, <main>) instead of generic <div>/<span>. Improves accessibility, SEO, and code readability.")
            ),
            "Medium", List.of(
                q("What is CORS and how does it work?",
                  "Cross-Origin Resource Sharing: browser security mechanism restricting cross-origin requests. Server must send Access-Control-Allow-Origin headers. Preflight OPTIONS request for non-simple requests."),
                q("Explain the Critical Rendering Path.",
                  "Browser steps: parse HTML → DOM, parse CSS → CSSOM, combine → Render Tree, layout (calculate positions), paint (draw pixels). Optimize by deferring non-critical JS/CSS.")
            ),
            "Hard", List.of(
                q("What is HTTP/2 and what improvements does it offer?",
                  "Multiplexing (multiple requests on one connection), header compression (HPACK), server push, binary framing. Eliminates head-of-line blocking present in HTTP/1.1."),
                q("Explain Content Security Policy (CSP).",
                  "HTTP header declaring trusted sources for scripts, styles, images etc. Prevents XSS by blocking inline scripts and unauthorized external sources. Use report-uri to log violations.")
            )
        ));

        // ── Backend ───────────────────────────────────────────────────────────────
        QUESTION_BANK.put("Backend", Map.of(
            "Easy", List.of(
                q("What is dependency injection and why is it useful?",
                  "Dependencies are provided to a class externally rather than created inside. Promotes loose coupling, testability, and easier swapping of implementations."),
                q("What is the difference between SQL and NoSQL databases?",
                  "SQL: relational, structured schema, ACID transactions (MySQL, PostgreSQL). NoSQL: schema-flexible, horizontally scalable, eventual consistency (MongoDB, Redis, Cassandra).")
            ),
            "Medium", List.of(
                q("What is the difference between authentication and authorization?",
                  "Authentication: verifying identity (who you are). Authorization: verifying permissions (what you can do). Auth first, then authorize."),
                q("Explain REST vs GraphQL.",
                  "REST uses multiple endpoints with fixed response shapes — can over/under-fetch. GraphQL uses a single endpoint and lets clients specify exactly what data they need, reducing over-fetching.")
            ),
            "Hard", List.of(
                q("What is the N+1 query problem and how do you solve it?",
                  "Fetching N records and issuing 1 additional query per record = N+1 queries total. Fix with JOIN FETCH in JPA, batch loading, or DataLoader in GraphQL."),
                q("Explain the Saga pattern for distributed transactions.",
                  "Splits distributed transactions into local transactions with compensating actions for rollbacks. Two flavors: Choreography (events) and Orchestration (central saga orchestrator).")
            )
        ));
    }

    private static Map<String, String> q(String question, String answer) {
        Map<String, String> map = new HashMap<>();
        map.put("question", question);
        map.put("answer", answer);
        return map;
    }

    public Map<String, String> generate(String topic, String difficulty) {
        Map<String, List<Map<String, String>>> topicMap = QUESTION_BANK.getOrDefault(topic, QUESTION_BANK.get("JavaScript"));
        List<Map<String, String>> questions = topicMap.getOrDefault(difficulty, topicMap.get("Medium"));
        if (questions == null || questions.isEmpty()) {
            return Map.of("question", "No question available for this topic and difficulty.",
                          "answer", "Please try a different topic or difficulty level.");
        }
        Map<String, String> selected = questions.get(new Random().nextInt(questions.size()));
        return Map.of("question", selected.get("question"), "answer", selected.get("answer"));
    }

    public Set<String> getTopics() {
        return QUESTION_BANK.keySet();
    }
}
