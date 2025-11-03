package com.surrogate.Zoolip.config;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import org.springframework.stereotype.Component;

@Component
@Push(PushMode.AUTOMATIC)
public class VaadinAppShell implements AppShellConfigurator {
}