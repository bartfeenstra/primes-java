import java.lang.InterruptedException;
import java.lang.Math;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


class Prime {
    /**
     * Checks if the given number is a prime.
     */
    private static boolean isPrime(int number) {
        // 1 is odd, but not a prime.
        if (1 == number) {
            return false;
        }

        // 2 is even, yet a prime.
        if (2 == number) {
            return true;
        }

        // Catch negative numbers, and positive numbers that are special-cased.
        if (number < 3) {
            return false;
        }

        // Catch all even numbers here, so our complex logic later can focus on odd divisors.
        if (0 == number % 2) {
            return false;
        }

        // Negative numbers and 0 aren't primes, and we already handled 1 and 2.
        int low = 3;
        // The highest value to take into account when checking if a n is a prime, is √n. Round that down to the nearest
        // integer.
        int high = (int)Math.floor(Math.sqrt(number));
        // Use an old-fashioned loop, so we can cut short the operation as soon as we find a divisor.
        for (int possibleDivisor = low; possibleDivisor <= high; possibleDivisor += 2) {
            if (0 == number % possibleDivisor) {
                return false;
            }
        }
        return true;
    }

    /**
     * Computes the primes ranging from the low up to and including the high value.
     */
    public static Stream<Integer> compute(int low, int high) {
        // The indicated range must be positive and go from low to high.
        if (low < 0) {
            throw new IllegalArgumentException("The range must be positive.");
        }
        if (high < 0) {
            throw new IllegalArgumentException("The range must be positive.");
        }
        if (low >= high) {
            throw new IllegalArgumentException("The range must go from low to high.");
        }
        return IntStream.rangeClosed(low, high).filter(Prime::isPrime).boxed();
    }

    /**
     * Prints the primes ranging from the low up to and including the high value.
     */
    public static void printComputed(int low, int high) {
        compute(low, high).forEach(prime -> System.out.println(prime));
    }

    /**
     * Computes the primes ranging from the low up to and including the high value, in parallel.
     */
    public static Stream<Integer> compute(int low, int high, int cores) throws InterruptedException, ExecutionException {
        // Build the computation as usual, but configure it to be run in parallel. Then collect the values to terminate
        // the stream, and block on all parallel operations being completed.
        ForkJoinPool pool = new ForkJoinPool(cores);
        ForkJoinTask<List<Integer>> task = pool.submit(() -> {
            return compute(low, high).parallel().collect(Collectors.toList());
        });
        List<Integer> integers = task.get();
        // Parallel execution returns results in an unpredictable order, so sort them.
        Collections.sort(integers);
        return integers.stream();
    }

    /**
     * Prints the primes ranging from the low up to and including the high value, in parallel.
     */
    public static void printComputed(int low, int high, int cores) throws InterruptedException, ExecutionException {
        compute(low, high, cores).forEach(prime -> System.out.println(prime));
    }
}
