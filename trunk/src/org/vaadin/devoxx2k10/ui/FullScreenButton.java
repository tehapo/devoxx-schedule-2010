package org.vaadin.devoxx2k10.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.ClientWidget;

@ClientWidget(org.vaadin.devoxx2k10.widgetset.client.ui.VFullScreenButton.class)
public class FullScreenButton extends Button {

    private static final long serialVersionUID = -1110596384418541526L;

    public FullScreenButton() {
        setStyleName("full-screen");
    }

}
