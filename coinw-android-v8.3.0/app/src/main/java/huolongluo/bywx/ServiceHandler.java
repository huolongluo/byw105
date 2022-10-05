package huolongluo.bywx;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
public class ServiceHandler {
    private static ServiceHandler instance;
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));

    private ServiceHandler() {
    }

    public static ServiceHandler getInstance() {
        if (instance == null) {
            synchronized (ServiceHandler.class) {
                if (instance == null) {
                    instance = new ServiceHandler();
                }
            }
        }
        return instance;
    }

    public void post(IServiceRunnable task) {
        executor.execute(task);
    }

    public void remove(IServiceRunnable task) {
        executor.remove(task);
    }
}
