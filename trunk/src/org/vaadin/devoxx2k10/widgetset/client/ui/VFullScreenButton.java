package org.vaadin.devoxx2k10.widgetset.client.ui;

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.vaadin.terminal.gwt.client.ui.VButton;

public class VFullScreenButton extends VButton {

    private Element previousAppParent;

    @Override
    public void onClick(ClickEvent event) {
        if (previousAppParent == null) {
            doFullScreen();
        } else {
            doRestore();
        }
    }

    private void doFullScreen() {
        String rootId = client.getConfiguration().getRootPanelId();
        Element rootElement = Document.get().getElementById(rootId);
        BodyElement bodyElement = Document.get().getBody();

        if (!rootElement.getParentElement().equals(bodyElement)) {
            // Detach the application from possible container div and store
            // the parent for restoring.
            previousAppParent = rootElement.getParentElement();
            bodyElement.appendChild(rootElement);
            setFullScreenStyle(rootElement);

            client.forceLayout();
        }
    }

    private void doRestore() {
        if (previousAppParent != null) {
            String rootId = client.getConfiguration().getRootPanelId();
            Element rootElement = Document.get().getElementById(rootId);
            previousAppParent.appendChild(rootElement);
            setRestoredStyle(rootElement);

            client.forceLayout();
            previousAppParent = null;
        }
    }

    private void setFullScreenStyle(Element rootElement) {
        rootElement.getStyle().setPosition(Position.ABSOLUTE);
        rootElement.getStyle().setTop(0, Unit.PX);
        rootElement.getStyle().setLeft(0, Unit.PX);
        rootElement.getStyle().setWidth(100, Unit.PCT);
        rootElement.getStyle().setHeight(100, Unit.PCT);
    }

    private void setRestoredStyle(Element rootElement) {
        rootElement.getStyle().clearPosition();
        rootElement.getStyle().clearTop();
        rootElement.getStyle().clearLeft();
        rootElement.getStyle().clearWidth();
        rootElement.getStyle().clearHeight();
    }
}
