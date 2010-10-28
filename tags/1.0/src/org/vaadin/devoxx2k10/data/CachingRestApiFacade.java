package org.vaadin.devoxx2k10.data;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;
import org.vaadin.devoxx2k10.data.http.HttpClient;

public class CachingRestApiFacade extends RestApiFacadeImpl {

    private static final ConcurrentHashMap<String, List<DevoxxPresentation>> scheduleCache = new ConcurrentHashMap<String, List<DevoxxPresentation>>();

    private static long CACHE_EXPIRATION_IN_MS = 1000 * 60 * 60;

    private static Logger logger = Logger.getLogger(CachingRestApiFacade.class);

    static {
        // Start a timer for clearing the cache periodically.
        Timer cacheClearTimer = new Timer();
        cacheClearTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.info("Clearing cached schedule data.");
                scheduleCache.clear();
            }
        }, CACHE_EXPIRATION_IN_MS, CACHE_EXPIRATION_IN_MS);
    }

    public CachingRestApiFacade() {
        super();
    }

    public CachingRestApiFacade(HttpClient jsonProvider) {
        super(jsonProvider);
    }

    @Override
    public List<DevoxxPresentation> getFullSchedule() {
        // Note that with some unlucky timing, two threads might
        // call the super version of this method. In case of this
        // application this is not a problem. Could be solved by
        // using Futures instead of the resulting List directly.

        List<DevoxxPresentation> scheduleData = scheduleCache.get("schedule");
        if (scheduleData == null) {
            // cache miss
            scheduleData = super.getFullSchedule();
            scheduleCache.put("schedule", scheduleData);
        }
        return scheduleData;
    }

}
