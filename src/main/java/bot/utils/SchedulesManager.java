package bot.utils;

import org.apache.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulesManager {

    private static Logger log = Logger.getLogger(SchedulesManager.class);

    public SchedulesManager() {
    }

    public void init(){
        initYoDataCleaner();
        initMyWorldCleaner();
        initVideoNoteCleaner();
    }

    private void initYoDataCleaner() {
        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(() -> {
                CompletableFuture<Void> check = CompletableFuture.runAsync(() ->
                {
                    DBUtils dbUtils = new DBUtils();
                    boolean success = dbUtils.checkYoDataCleaner();
                    if (!success) log.error("Expired YO task error");
                    dbUtils.connectionClose();
                });
                try {
                    check.get();
                } catch (Exception e) {
                    log.error("Expired YO task error:" + e);
                }
        }, 0, 5, TimeUnit.MINUTES);
    }

    private void initMyWorldCleaner() {
        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(() -> {
            CompletableFuture<Void> check = CompletableFuture.runAsync(() ->
            {
                DBUtils dbUtils = new DBUtils();
                boolean success = dbUtils.checkMyWorldDataCleaner();
                if (!success) log.error("Expired My World task error");
                dbUtils.connectionClose();
            });
            try {
                check.get();
            } catch (Exception e) {
                log.error("Expired My World task error:" + e);
            }
        }, 0, 2, TimeUnit.MINUTES);
    }

    private void initVideoNoteCleaner() {
        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(() -> {
            CompletableFuture<Void> check = CompletableFuture.runAsync(() ->
            {
                DBUtils dbUtils = new DBUtils();
                boolean success = dbUtils.checkMyWorldDataCleaner();
                if (!success) log.error("Expired Video Note task error");
                dbUtils.connectionClose();
            });
            try {
                check.get();
            } catch (Exception e) {
                log.error("Expired Video Note task error:" + e);
            }
        }, 0, 2, TimeUnit.MINUTES);
    }
}