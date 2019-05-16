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
import java.util.Set;
import java.util.concurrent.*;

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

    /** Test eager eval */
    @Test
    public void testSingleton() throws InterruptedException{
        final Set<SingletonClass> result = ConcurrentHashMap.newKeySet();

        for(int i=0; i<threadNumber; i++)
            executor.execute(() -> {
                try {
                    long threadId = Thread.currentThread().getId();
                    logger.info("[Thread-" + threadId + "] is running");
                    result.add(SingletonClass.getSingleton());
                    logger.info("[Thread-" + threadId + "] is finished");
                }
                catch (Throwable t) {
                    logger.error(t, t.getCause());
                }
            });
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        assertEquals("Instance number supposed to be only one, but you got " + result.size(), expectInstances, result.size());
    }
}
