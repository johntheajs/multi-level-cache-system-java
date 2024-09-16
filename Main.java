import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ConcurrentCacheLevel cacheSystem = new ConcurrentCacheLevel();
        Scanner scanner = new Scanner(System.in);

        // Adding initial cache levels
        cacheSystem.addCacheLevel(3, "LRU");
        cacheSystem.addCacheLevel(2, "LFU");

        // Thread pool with multiple threads
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        System.out.println("Cache System Commands:");
        System.out.println("1. put <key> <value>");
        System.out.println("2. get <key>");
        System.out.println("3. addCacheLevel <size> <evictionPolicy(LRU/LFU)>");
        System.out.println("4. removeCacheLevel <level>");
        System.out.println("5. displayCache");
        System.out.println("6. exit");

        // Loop for real-time input
        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine();
            String[] tokens = input.split(" ");

            String command = tokens[0];

            switch (command.toLowerCase()) {
                case "put":
                    if (tokens.length == 3) {
                        String key = tokens[1];
                        String value = tokens[2];
                        executorService.submit(() -> {
                            cacheSystem.put(key, value);
                            System.out.println("PUT operation completed.");
                        });
                    } else {
                        System.out.println("Usage: put <key> <value>");
                    }
                    break;

                case "get":
                    if (tokens.length == 2) {
                        String key = tokens[1];
                        executorService.submit(() -> {
                            String result = cacheSystem.get(key);
                            if (result != null) {
                                System.out.println("GET result: " + result);
                            } else {
                                System.out.println("Key not found.");
                            }
                        });
                    } else {
                        System.out.println("Usage: get <key>");
                    }
                    break;

                case "addcachelevel":
                    if (tokens.length == 3) {
                        try {
                            int size = Integer.parseInt(tokens[1]);
                            String evictionPolicy = tokens[2];
                            executorService.submit(() -> {
                                cacheSystem.addCacheLevel(size, evictionPolicy);
                                System.out.println("Cache level added.");
                            });
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid size. Please enter a valid number.");
                        }
                    } else {
                        System.out.println("Usage: addCacheLevel <size> <evictionPolicy(LRU/LFU)>");
                    }
                    break;

                case "removecachelevel":
                    if (tokens.length == 2) {
                        try {
                            int level = Integer.parseInt(tokens[1]);
                            executorService.submit(() -> {
                                cacheSystem.removeCacheLevel(level - 1); // Cache levels are 0-indexed internally
                                System.out.println("Cache level removed.");
                            });
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid level. Please enter a valid number.");
                        }
                    } else {
                        System.out.println("Usage: removeCacheLevel <level>");
                    }
                    break;

                case "displaycache":
                    executorService.submit(() -> {
                        cacheSystem.displayCache();
                    });
                    break;

                case "exit":
                    System.out.println("Shutting down...");
                    executorService.shutdown();
                    scanner.close();
                    return; // Exit the program

                default:
                    System.out.println("Unknown command. Please try again.");
                    break;
            }
        }
    }
}
