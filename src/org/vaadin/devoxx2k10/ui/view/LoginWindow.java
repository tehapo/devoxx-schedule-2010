package org.vaadin.devoxx2k10.ui.view;

import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.data.domain.MyScheduleUser;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class LoginWindow extends Window {

    public LoginWindow() {
        super("Activate MySchedule or Sign In");
        setStyleName("login-window");
        setModal(true);
        setWidth("450px");
        setContent(createLayout());
        /*-
        RestApiFacade facade = DevoxxScheduleApplication
                .getCurrentInstance().getBackendFacade();
        facade.activateMySchedule("Teemu", "Tester", "tehapo@utu.fi");
         */
    }

    private Layout createLayout() {
        CustomLayout layout = new CustomLayout("login-window");
        layout.addComponent(new SignInForm(), "sign-in-form");
        return layout;
    }

    private class SignInForm extends Form implements Button.ClickListener {

        private static final long serialVersionUID = -2517086646231682994L;

        private BeanItem<MyScheduleUser> item;

        public SignInForm() {
            setFormFieldFactory(new DefaultFieldFactory() {

                private static final long serialVersionUID = -2259575092732484965L;

                public Field createField(Item item, Object propertyId,
                        Component uiContext) {
                    if (propertyId.equals("favourites")) {
                        return null;
                    } else {
                        Field field = super.createField(item, propertyId,
                                uiContext);
                        if (field instanceof TextField) {
                            ((TextField) field).setNullRepresentation("");
                        }
                        return field;
                    }
                }
            });
            item = new BeanItem<MyScheduleUser>(new MyScheduleUser());
            setItemDataSource(item);

            getFooter().addComponent(
                    new Button("Sign In", (Button.ClickListener) this));
        }

        public void buttonClick(ClickEvent event) {
            this.commit();

            DevoxxScheduleApplication app = DevoxxScheduleApplication
                    .getCurrentInstance();
            app.getBackendFacade().getScheduleForUser(item.getBean());
            app.setUser(item.getBean());

            close();
        }
    }

}
