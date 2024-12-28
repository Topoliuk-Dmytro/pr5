import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Task1 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            simulateDelay(1000);
            System.out.println("Task 1 completed");
            return 10;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            simulateDelay(1500);
            System.out.println("Task 2 completed");
            return 20;
        });

        CompletableFuture<Integer> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
            System.out.println("\nCombining results: " + result1 + " + " + result2);
            return result1 + result2;
        });

        CompletableFuture<Integer> composedFuture = combinedFuture.thenCompose(sum -> CompletableFuture.supplyAsync(() -> {
            System.out.println("Multiplication of the sum " + sum);
            return sum * 2;
        }));

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, composedFuture);

        allFutures.join();

        System.out.println("\nAll tasks completed!");
        System.out.println("Combined result: " + combinedFuture.get());
        System.out.println("Composed result: " + composedFuture.get());
    }

    private static void simulateDelay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
