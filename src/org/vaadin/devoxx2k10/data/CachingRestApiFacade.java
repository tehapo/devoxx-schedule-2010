package org.vaadin.devoxx2k10.data;

import java.util.Collections;
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
        final Timer cacheClearTimer = new Timer();

        cacheClearTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (logger.isInfoEnabled()) {
                    logger.info("Clearing cached schedule data.");
                }
                scheduleCache.clear();
            }
        }, CACHE_EXPIRATION_IN_MS, CACHE_EXPIRATION_IN_MS);
    }

    public CachingRestApiFacade() {
        super();
    }

    public CachingRestApiFacade(final HttpClient jsonProvider) {
        super(jsonProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DevoxxPresentation> getFullSchedule() {
        // Note that with some unlucky timing, two threads might
        // call the super version of this method. In case of this
        // application this is not a problem. Could be solved by
        // using Futures instead of the resulting List directly.

        List<DevoxxPresentation> scheduleData = scheduleCache.get("schedule");

        if (scheduleData == null) {
            // cache miss
            scheduleData = Collections.unmodifiableList(super.getFullSchedule());
            scheduleCache.put("schedule", scheduleData);
        }
        return scheduleData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DevoxxPresentation> search(final String tag) {
        // Note that with some unlucky timing, two threads might
        // call the super version of this method. In case of this
        // application this is not a problem. Could be solved by
        // using Futures instead of the resulting List directly.

        List<DevoxxPresentation> scheduleData = scheduleCache.get("search-" + tag);
        if (scheduleData == null) {
            // cache miss
            scheduleData = Collections.unmodifiableList(super.search(tag));
            scheduleCache.put("search-" + tag, scheduleData);
        }
        return scheduleData;
    }
}
