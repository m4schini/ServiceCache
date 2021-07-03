import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing InMemory cache processor
 */
public class TestInMemoryCacheService {
    InMemoryCacheService service = new InMemoryCacheService();

    /**
     * We expect to see the same result immediately after first processing
     */
    @Test
    public void TestCacheProcessor_useCachedResult() {
        int expected = service.getRandomNumber();
        int actual = service.getRandomNumber();

        assertEquals(expected, actual);
    }

    /**
     * After maxAge passed we expect to see a different value from before,
     * because we actually rerun the function
     */
    @Test
    public void TestCacheProcessor_refreshResult() throws InterruptedException {
        int expected = service.getRandomNumber();
        Thread.sleep(1100);
        int actual = service.getRandomNumber();

        assertNotEquals(expected, actual);
    }
}