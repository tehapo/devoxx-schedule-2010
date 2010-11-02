package org.vaadin.devoxx2k10.ui.view;

import java.util.List;

import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.domain.DevoxxPresentation;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Layout for displaying talks related to a single tag assignable via the
 * {@link #setTag(String, DevoxxPresentation)} method.
 */
public class RelatedTalksLayout extends CssLayout implements Button.ClickListener {

    private static final long serialVersionUID = -8231650018794943524L;

    private final MainView mainView;

    public RelatedTalksLayout(final MainView mainView) {
        this.mainView = mainView;
        setWidth("100%");
        setStyleName("related-talks");
    }

    /**
     * Set the tag to display related talks for excluding the given talk.
     * 
     * @param tag
     * @param exclude
     */
    public void setTag(final String tag, final DevoxxPresentation exclude) {
        final List<DevoxxPresentation> related = DevoxxScheduleApplication.getCurrentInstance().getBackendFacade()
                .search(tag);
        removeAllComponents();

        boolean relatedTalksFound = false;
        for (final DevoxxPresentation relatedTalk : related) {
            if (!relatedTalk.equals(exclude)) {
                relatedTalksFound = true;

                final Button relatedTalkButton = new Button(relatedTalk.getTitle(), this);
                relatedTalkButton.setStyleName(BaseTheme.BUTTON_LINK);
                relatedTalkButton.setData(relatedTalk.getId());
                addComponent(relatedTalkButton);
            }
        }

        // no other related talks found -> add sorry label
        if (!relatedTalksFound) {
            addComponent(new Label("Sorry, no other talks for keyword <i>" + tag + "</i>", Label.CONTENT_XHTML));
        }
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        mainView.selectPresentationWithId((Integer) event.getButton().getData());
    }

}
