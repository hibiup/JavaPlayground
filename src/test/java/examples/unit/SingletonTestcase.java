package examples.unit;

import examples.SingletonClass;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

@RunWith(Parameterized.class)
public class SingletonTestcase extends TestCase {
    private static final Logger logger = Logger.getLogger(SingletonTestcase.class);
    private ExecutorService executor;

    /** Create 10 threads */
    @Parameterized.Parameters
    public static Collection<Object[]> repeat() {
        return Arrays.asList(new Object[][]{
                {10, 1}
        });
    }

    @Before
    @Override
    public void setUp() {
        executor = Executors.newFixedThreadPool(threadNumber);
    }

    @Parameterized.Parameter
    public int threadNumber;

    @Parameterized.Parameter(1)
    public int expectInstances;

    /** Test laziness eval */
    @Test
    public void testLazySingleton() throws InterruptedException {
        testSingleton(SingletonClass::getInstance);
    }

    /** Test eager eval */
    @Test
    public void testEagerSingleton() throws InterruptedException {
        testSingleton(SingletonClass::getSingleton);
    }

    private void testSingleton(Supplier<SingletonClass> fn) throws InterruptedException{
        //final Set<SingletonClass> results = ConcurrentHashMap.newKeySet();
        final SingletonClass[] results = new SingletonClass[threadNumber];

        for(int i=0; i<threadNumber; i++) {
            final int finalI = i;
            executor.execute(() -> {
                try {
                    long threadId = Thread.currentThread().getId();
                    logger.info("[Thread-" + threadId + "] is running");
                    results[finalI] = (fn.get());
                    logger.info("[Thread-" + threadId + "] is finished");
                } catch (Throwable t) {
                    logger.error(t, t.getCause());
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        long refs = Arrays.stream(results).filter(Objects::nonNull).count();
        assertEquals(threadNumber + " reference supposed to be received, but you got " + refs + " only", threadNumber, refs);

        long count = Arrays.stream(results).distinct().count();
        assertEquals("Instance number supposed to be only one, but you got " + count, expectInstances, count);
    }
}
