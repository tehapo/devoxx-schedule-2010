package org.vaadin.devoxx2k10.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.ClientWidget;

/**
 * Extended Button with additional client-side logic. This button allows full
 * screening and restoring a Vaadin application that is embedded inside a div
 * element.
 */
@ClientWidget(org.vaadin.devoxx2k10.widgetset.client.ui.VFullScreenButton.class)
public class FullScreenButton extends Button {

    private static final long serialVersionUID = -1110596384418541526L;

    public FullScreenButton() {
        setStyleName("full-screen");
        setDescription("Toggle full screen mode");
    }

}
