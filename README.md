# Multi-Level Cache System in Java

This repository contains a dynamic multilevel cache system implemented in Java. The system supports both LRU (Least Recently Used) and LFU (Least Frequently Used) eviction policies, allowing multiple levels of caching and concurrent access through multithreading.

## Overview

### Node and DoublyLinkedList

The system starts with the creation of a `Node` class and a `DoublyLinkedList` class. These are the core data structures used to implement the LRU and LFU cache mechanisms:

- `Node`: Represents each cache entry with key-value pairs.
- `DoublyLinkedList`: Used for efficient management of cache entries in the LRU implementation.

### EvictionPolicy Interface

The cache policies (LRU, LFU) implement the `EvictionPolicy` interface, which defines the following methods:

- `void accessNode(String key);`
- `Node insertNode(String key, String value);`
- `Node getNode(String key);`
- `void display();`

This allows LRU and LFU caches to have a common structure and method set, enabling flexible cache management.

### LRU Cache (Least Recently Used)

The LRU cache uses:

- A **HashMap** for constant-time access to cache entries.
- A **DoublyLinkedList** to maintain the order of access.

On each access, the node becomes the head of the list, and new insertions are placed at the head. If the cache is full, the tail (least recently used) node is evicted.

### LFU Cache (Least Frequently Used)

The LFU cache uses:

- A **HashMap** for constant-time access.
- A **frequencyMap** to track the frequency of access for each cache entry.

When the cache is full, the least frequently accessed element is evicted. The frequency is updated on each access.

### CacheLevel Class

The `CacheLevel` class contains an `ArrayList` of `EvictionPolicy`, representing different cache levels (either LRU or LFU). The class provides:

- `addCacheLevel(int size, String evictionPolicy)` – Add a cache level.
- `removeCacheLevel(int level)` – Remove a specific cache level.
- `put(String key, String value)` – Insert data into the cache.
- `get(String key)` – Retrieve data from the cache.
- `displayCache()` – Display the contents of all cache levels.

It also contains internal helper methods:

- `moveToLowerLevels(Node node, int level)` – Move evicted nodes to lower cache levels.
- `promoteToHigherLevels(String key, String value, int currentLevel)` – Promote accessed nodes to higher cache levels.

### System Design

- If an element is found in a higher cache level, it is updated (moved to the front if LRU, frequency incremented if LFU) and returned.
- If an element is not found, it is inserted, and any necessary evictions are managed by moving evicted elements to lower cache levels.

## Implementation Details

This repository contains three different implementations of the caching system, each available in different commits:

### 1. **Task Errors Solved:**

- Basic implementation that uses static input (hardcoded values).
- This version doesn't allow dynamic user input or multithreading.
- It runs simple sample instructions, and the output is displayed on the command line.

### 2. **Dynamic Inputs and Threads:**

- This version introduces dynamic input from the user via the command line.
- The `CacheLevel` class is updated to `ConcurrentCacheLevel`, using `ReentrantReadWriteLock` to handle read and write actions concurrently.
- The `Main` class uses threads to simulate multiple users interacting with the system at the same time.

### 3. **Used ExecutorService Instead of Threads:**

- Similar to the previous version but uses Java's `ExecutorService` to handle concurrent tasks instead of managing threads directly.
- This version ensures better thread safety and easier management of concurrent access.

## Running the Application

1. Clone the repository:
   `git clone https://github.com/johntheajs/multi-level-cache-system-java.git`
2. Compile the Java files:
   `javac *.java`
3. Run the application:
   `java Main`

## Usage

When the application runs, you can input commands through the terminal:

    -  put <key> <value>: Insert a key-value pair into the cache.
    -  get <key>: Retrieve a value from the cache.
    - addCacheLevel <size> <evictionPolicy(LRU/LFU)>: Add a new cache level.
    - removeCacheLevel <level>: Remove a cache level.
    - displayCache: Display the contents of all cache levels.
    - exit: Exit the program.

### Example commands

    - put A 1
    - put B 2
    - get A
    - addCacheLevel 3 LRU
    - displayCache

### Sample Run

- Cache level added: LRU
- Cache level added: LFU
- Cache System Commands:

1. put <key> <value>
2. get <key>
3. addCacheLevel <size> <evictionPolicy(LRU/LFU)>
4. removeCacheLevel <level>
5. displayCache
6. exit
   - Enter command: put A 1
   - Enter command: PUT operation completed.
   - put B 2
   - Enter command: PUT operation completed.
   - get B
   - Enter command: GET result: 2
   - displayCache
   - Enter command: Total number of levels: 2
   - Cache Level 1:
   - B: 2 A: 1
   - Cache Level 2:
   - put C 3
   - Enter command: PUT operation completed.
   - put D 4
   - Enter command: PUT operation completed.
   - displayCache
   - Enter command: Total number of levels: 2
   - Cache Level 1:
   - D: 4 C: 3 B: 2
   - Cache Level 2:
   - A: 1 (Freq: 1)
   - get A
   - Enter command: GET result: 1
   - displayCache
   - Enter command: Total number of levels: 2
   - Cache Level 1:
   - A: 1 D: 4 C: 3
   - Cache Level 2:
   - B: 2 (Freq: 1) A: 1 (Freq: 2)
   - exit
   - Shutting down...
