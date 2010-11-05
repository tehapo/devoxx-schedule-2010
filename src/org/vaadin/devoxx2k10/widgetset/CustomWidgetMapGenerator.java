package org.vaadin.devoxx2k10.widgetset;

import java.util.Collection;
import java.util.LinkedList;

import org.vaadin.addthis.AddThis;
import org.vaadin.browsercookies.BrowserCookies;
import org.vaadin.devoxx2k10.ui.FullScreenButton;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.gwt.widgetsetutils.WidgetMapGenerator;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * WidgetMapGenerator to create an optimized WidgetSet without unnecessary
 * widgets.
 */
public class CustomWidgetMapGenerator extends WidgetMapGenerator {

    @Override
    protected Collection<Class<? extends Paintable>> getUsedPaintables() {

        final Collection<Class<? extends Paintable>> usedPaintables = new LinkedList<Class<? extends Paintable>>();

        // core Paintables
        usedPaintables.add(Button.class);
        usedPaintables.add(Panel.class);
        usedPaintables.add(VerticalLayout.class);
        usedPaintables.add(HorizontalLayout.class);
        usedPaintables.add(UriFragmentUtility.class);
        usedPaintables.add(CheckBox.class);
        usedPaintables.add(Label.class);
        usedPaintables.add(CssLayout.class);
        usedPaintables.add(CustomLayout.class);
        usedPaintables.add(CustomComponent.class);
        usedPaintables.add(TextField.class);
        usedPaintables.add(Window.class);
        usedPaintables.add(Link.class);

        // add-on Paintables
        usedPaintables.add(BrowserCookies.class);
        usedPaintables.add(GoogleAnalyticsTracker.class);
        usedPaintables.add(AddThis.class);
        usedPaintables.add(Calendar.class);

        // project Paintables
        usedPaintables.add(FullScreenButton.class);

        return usedPaintables;

    }
}