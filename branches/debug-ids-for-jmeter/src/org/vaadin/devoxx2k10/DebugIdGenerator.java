package org.vaadin.devoxx2k10;

import org.apache.log4j.Logger;

import com.vaadin.ui.ComponentContainer.ComponentAttachEvent;
import com.vaadin.ui.ComponentContainer.ComponentAttachListener;

public class DebugIdGenerator implements ComponentAttachListener {

    private static final long serialVersionUID = -3278527327221292335L;

    private static final Logger logger = Logger.getLogger(DebugIdGenerator.class);

    private int index = 0;
    private final String prefix;

    public DebugIdGenerator(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void componentAttachedToContainer(final ComponentAttachEvent event) {
        final String debugId = getNextDebugId();
        event.getAttachedComponent().setDebugId(debugId);

        if (logger.isDebugEnabled()) {
            logger.debug("Assigned debug id: " + debugId);
        }
    }

    private String getNextDebugId() {
        return prefix + "-" + index++;
    }
}
