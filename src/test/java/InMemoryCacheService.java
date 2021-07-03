import com.github.m4schini.servicecache.ServiceCacheBase;
import com.github.m4schini.servicecache.cache.UseCache;

import java.util.Random;

/**
 * Service used to test cache functionality using the in-memory cache
 */
public class InMemoryCacheService extends ServiceCacheBase {
    Random random = new Random();

    /**
     * Get a pseudo random number from 0 to 32.
     *
     * @return number
     */
    @UseCache(path = "random:", maxAge = 1)
    public int getRandomNumber() {
        return cache.process("number", () -> random.nextInt(32));
    }
}
