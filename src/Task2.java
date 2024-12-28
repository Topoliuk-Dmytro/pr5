import java.util.concurrent.*;
import java.util.*;

public class Task2 {

    private static List<String> fetchReviews(String platform) {
        try {
            Thread.sleep((long) (Math.random() * 2000) + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(platform + " Review 1", platform + " Review 2", platform + " Review 3");
    }

    private static String summarizeReviews(List<String> reviews) {
        return "Summary: " + reviews.size() + " reviews processed.";
    }

    private static String analyzeReviews(List<String> reviews) {
        return "Analysis: " + reviews.stream().mapToInt(String::length).average().orElse(0.0) + " average review length.";
    }

    private static String compareReviews(List<String> reviews1, List<String> reviews2) {
        return "Comparison: " + Math.abs(reviews1.size() - reviews2.size()) + " review count difference.";
    }

    public static void main(String[] args) {
        CompletableFuture<List<String>> platform1Reviews = CompletableFuture.supplyAsync(() -> fetchReviews("Platform 1"));
        CompletableFuture<List<String>> platform2Reviews = CompletableFuture.supplyAsync(() -> fetchReviews("Platform 2"));
        CompletableFuture<List<String>> platform3Reviews = CompletableFuture.supplyAsync(() -> fetchReviews("Platform 3"));

        CompletableFuture<Void> allReviewsFetched = CompletableFuture.allOf(platform1Reviews, platform2Reviews, platform3Reviews);

        allReviewsFetched.thenRun(() -> {
            try {
                List<String> combinedReviews = new ArrayList<>();
                combinedReviews.addAll(platform1Reviews.get());
                combinedReviews.addAll(platform2Reviews.get());
                combinedReviews.addAll(platform3Reviews.get());

                String summary = summarizeReviews(combinedReviews);
                System.out.println("Combined Review Summary: " + summary);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        platform1Reviews.thenCompose(reviews -> CompletableFuture.supplyAsync(() -> analyzeReviews(reviews)))
                .thenAccept(analysis -> System.out.println("Analysis of Platform 1 Reviews: " + analysis));

        platform1Reviews.thenCombine(platform2Reviews, (reviews1, reviews2) -> compareReviews(reviews1, reviews2))
                .thenAccept(comparison -> System.out.println("Comparison of Platform 1 and Platform 2: " + comparison));

        CompletableFuture<Object> firstCompleted = CompletableFuture.anyOf(platform1Reviews, platform2Reviews, platform3Reviews);
        firstCompleted.thenAccept(result -> System.out.println("Fastest platform reviews: " + result));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
