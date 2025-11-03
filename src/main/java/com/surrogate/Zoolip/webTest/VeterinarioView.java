package com.surrogate.Zoolip.webTest;

import com.surrogate.Zoolip.models.DTO.VeterinarioDTO;
import com.surrogate.Zoolip.models.bussiness.Atencion.Veterinario;
import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.bussiness.InstitucionService;
import com.surrogate.Zoolip.services.bussiness.VeterinarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route("veterinarios")
@UIScope
@PageTitle("Veterinarios")
public class VeterinarioView extends FormLayout {
    private final VeterinarioService veterinarioService;
    private final InstitucionService institucionService;

    private final Binder<Veterinario> binder = new Binder<>(Veterinario.class);
    private Veterinario current;

    private final TextField nombre = new TextField("Nombre");
    private final ComboBox<Institucion> institucion = new ComboBox<>("Institución");

    private final Button save = new Button("Guardar", VaadinIcon.CHECK.create());
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    private final Button clear = new Button("Limpiar", VaadinIcon.ERASER.create());

    private final Grid<VeterinarioDTO> grid = new Grid<>(VeterinarioDTO.class, false);

    @Autowired
    public VeterinarioView(VeterinarioService veterinarioService,
                           InstitucionService institucionService) {
        this.veterinarioService = veterinarioService;
        this.institucionService = institucionService;

        add(new H3("Gestión de Veterinarios"));

        institucion.setItems(institucionService.buscarInstituciones());
        institucion.setItemLabelGenerator(Institucion::getNombre);

        grid.addColumn(VeterinarioDTO::id).setHeader("ID");
        grid.addColumn(VeterinarioDTO::nombre).setHeader("Nombre");
        grid.addColumn(VeterinarioDTO::nombreInstitucion).setHeader("Institución");
        grid.setHeight("420px");
        grid.addThemeName("compact");

        binder.forField(nombre)
                .asRequired("Nombre requerido")
                .bind(Veterinario::getNombre, Veterinario::setNombre);

        binder.forField(institucion)
                .asRequired("Institución requerida")
                .bind(Veterinario::getId_institucion, Veterinario::setId_institucion);

        save.setEnabled(false);
        binder.addStatusChangeListener(s -> save.setEnabled(binder.isValid()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(save, delete, clear);

        add(nombre, institucion, actions, grid);
        setColspan(actions, 2);
        setColspan(grid, 2);

        grid.asSingleSelect().addValueChangeListener(evt -> {
            if (evt.getValue() == null) edit(null);
            else editByDto(evt.getValue());
        });

        save.addClickListener(e -> save());
        delete.addClickListener(e -> confirmDelete());
        clear.addClickListener(e -> edit(null));

        refreshGrid();
        edit(null);
    }

    private void edit(Veterinario v) {
        if (v == null) {
            current = null;
            binder.readBean(new Veterinario());
            delete.setEnabled(false);
            save.setText("Crear");
        } else {
            current = v;
            binder.readBean(current);
            delete.setEnabled(true);
            save.setText("Actualizar");
        }
    }

    private void editByDto(VeterinarioDTO dto) {
        if (dto == null) {
            edit(null);
            return;
        }
        Veterinario v = new Veterinario();
        v.setId_veterinario(dto.id());
        v.setNombre(dto.nombre());
        edit(v);
    }

    private void save() {
        Veterinario toSave = current == null ? new Veterinario() : current;
        try {
            binder.writeBean(toSave);
        } catch (Exception ex) {
            Notification.show("Corrige los errores del formulario");
            return;
        }

        if (institucion.getValue() != null) {
            toSave.setId_institucion(institucion.getValue());
        }

        try {
            Response res;
            if (toSave.getId_veterinario() == null) {
                res = veterinarioService.crear(toSave);
            } else {
                res = veterinarioService.actualizar(toSave);
            }
            if (res.getStatus().equals("error")) {
                Notification.show("Error: " + res.getMessage());
            } else {
                Notification.show("Veterinario guardado");
                refreshGrid();
                edit(null);
            }
        } catch (Exception ex) {
            Notification.show("Error al guardar: " + ex.getMessage());
            log.error("Save veterinario failed", ex);
        }
    }

    private void confirmDelete() {
        if (current == null || current.getId_veterinario() == null) return;
        Dialog d = new Dialog();
        d.add("¿Confirmar eliminación del veterinario?");
        Button yes = new Button("Eliminar", ev -> {
            try {
                veterinarioService.eliminar(current.getId_veterinario());
                Notification.show("Veterinario eliminado");
                refreshGrid();
                edit(null);
            } catch (Exception ex) {
                Notification.show("Error al eliminar: " + ex.getMessage());
            } finally {
                d.close();
            }
        });
        yes.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button no = new Button("Cancelar", ev -> d.close());
        no.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        d.add(new HorizontalLayout(yes, no));
        d.open();
    }

    private void refreshGrid() {
        try {
            var items = veterinarioService.obtenerVeterinarios();
            if (items != null) grid.setItems(items);
        } catch (Exception e) {
            log.error("Failed to refresh veterinarios", e);
        }
        try {
            institucion.setItems(institucionService.buscarInstituciones());
        } catch (Exception ignored) {
        }
    }
}

