package org.vaadin.devoxx2k10.ui.view;

import org.vaadin.devoxx2k10.DebugIdGenerator;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Link;

public class FooterLinksLayout extends CssLayout {

    private static final long serialVersionUID = -2609431346862645528L;

    private static final String VAADIN_LINK_URL = "http://vaadin.com";
    private static final String SOURCE_LINK_URL = "http://code.google.com/p/devoxx-schedule-2010/";

    public FooterLinksLayout() {
        setWidth("100%");
        setStyleName("footer-links");
        addListener(new DebugIdGenerator("footer-links"));

        final Link vaadinLink = new Link("", new ExternalResource(VAADIN_LINK_URL));
        vaadinLink.setHeight("45px");
        vaadinLink.setStyleName("vaadin");

        final Link projectLink = new Link("Source code", new ExternalResource(SOURCE_LINK_URL));
        projectLink.setStyleName("project");

        addComponent(projectLink);
        addComponent(vaadinLink);
    }

}
