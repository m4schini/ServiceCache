import com.github.m4schini.servicecache.ServiceCacheBase;
import com.github.m4schini.servicecache.cache.UseCache;

import java.util.Random;

/**
 * This Example services extends the ServiceCacheBase to enable the
 * ServiceCache system.
 */
public class ExampleService extends ServiceCacheBase {
    Random random = new Random();

    /**
     * Get a pseudo random number from 0 to 32.
     * Because we use the Cache, we expect for the specified max age that
     * the last result will be returned.
     * After the max-age expires the lambda function will be executed again.
     *
     * @return number
     */
    @UseCache(path = "random:", maxAge = 1)
    public int getRandomNumber() {
        return cache.process("number", () -> random.nextInt(32));
    }
}
