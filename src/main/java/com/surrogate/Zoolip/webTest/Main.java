package com.surrogate.Zoolip.webTest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/web/main")
public class Main extends VerticalLayout {
    public Main() {
        add(new Button("Click me", e -> Notification.show("Hello, Spring+Vaadin user!")));
    }
}
