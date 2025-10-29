package com.surrogate.Zoolip.webTest;

import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import com.surrogate.Zoolip.models.bussiness.Mascota.EstadoAdopcion;
import com.surrogate.Zoolip.models.bussiness.Mascota.EstadoSalud;
import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import com.surrogate.Zoolip.models.bussiness.Mascota.Tamanio;
import com.surrogate.Zoolip.services.bussiness.InstitucionService;
import com.surrogate.Zoolip.services.bussiness.MascotaService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@Slf4j
@Route("mascotas")
@PageTitle("Mascotas")
public class MascotaView extends FormLayout {

    private final MascotaService mascotaService;
    private final InstitucionService institucionService;

    private final Binder<Mascota> binder = new Binder<>(Mascota.class);
    private Mascota current;

    private final TextField search = new TextField();
    private final ComboBox<Tamanio> tamanio = new ComboBox<>("Tamaño");
    private final ComboBox<EstadoAdopcion> estadoAdopcion = new ComboBox<>("Estado adopción");
    private final ComboBox<EstadoSalud> estadoSalud = new ComboBox<>("Estado salud");
    private final IntegerField edad = new IntegerField("Edad");
    private final TextField raza = new TextField("Raza");
    private final TextField especie = new TextField("Especie");
    private final ComboBox<Institucion> institucion = new ComboBox<>("Institución");

    private final Button save = new Button("Guardar", VaadinIcon.CHECK.create());
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    private final Button clear = new Button("Limpiar", VaadinIcon.ERASER.create());
    private final Button goInstituciones = new Button("Ir a Instituciones", VaadinIcon.BUILDING.create());

    private final Grid<Mascota> grid = new Grid<>(Mascota.class, false);

    @Autowired
    public MascotaView(MascotaService mascotaService,
                       InstitucionService institucionService) {
        this.mascotaService = mascotaService;
        this.institucionService = institucionService;

        // Search
        search.setPlaceholder("Buscar por especie o raza...");
        search.setClearButtonVisible(true);
        search.addValueChangeListener(e -> updateGridFilter());
        search.setWidthFull();

        // Inputs
        tamanio.setItems(Tamanio.values());
        estadoAdopcion.setItems(EstadoAdopcion.values());
        estadoSalud.setItems(EstadoSalud.values());
        edad.setMin(0);
        edad.setInvalid(false);

        try {
            institucion.setItems(institucionService.buscarInstituciones());
        } catch (NoSuchMethodError | AbstractMethodError e) {
            institucion.setItems(institucionService.buscarInstituciones());
        }
        institucion.setItemLabelGenerator(Institucion::getNombre);

        especie.setWidth("300px");
        raza.setWidth("300px");
        edad.setWidth("150px");
        tamanio.setWidth("200px");
        estadoAdopcion.setWidth("220px");
        estadoSalud.setWidth("220px");
        institucion.setWidth("320px");

        // Grid
        grid.addColumn(Mascota::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Mascota::getEspecie).setHeader("Especie");
        grid.addColumn(Mascota::getRaza).setHeader("Raza");
        grid.addColumn(Mascota::getEdad).setHeader("Edad");
        grid.addColumn(Mascota::getTamanio).setHeader("Tamaño");
        grid.addColumn(Mascota::getEstadoAdopcion).setHeader("Adopción");
        grid.addColumn(Mascota::getEstadoSalud).setHeader("Salud");
        grid.addColumn(m -> {
            Institucion inst = m.getId_institucion();
            return inst == null ? "-" : inst.getNombre();
        }).setHeader("Institución");
        grid.setHeight("420px");
        grid.addThemeName("compact");

        // Layout responsive
        setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("720px", 2)
        );

        // Binder
        binder.forField(edad)
                .asRequired("Edad es requerida")
                .withValidator(v -> v != null && v >= 0, "Edad debe ser >= 0")
                .bind(Mascota::getEdad, Mascota::setEdad);

        binder.forField(raza)
                .asRequired("Raza requerida")
                .bind(Mascota::getRaza, Mascota::setRaza);

        binder.forField(especie)
                .asRequired("Especie requerida")
                .bind(Mascota::getEspecie, Mascota::setEspecie);

        binder.forField(tamanio)
                .asRequired("Tamaño requerido")
                .bind(Mascota::getTamanio, Mascota::setTamanio);

        binder.forField(estadoAdopcion)
                .asRequired("Estado de adopción requerido")
                .bind(Mascota::getEstadoAdopcion, Mascota::setEstadoAdopcion);

        binder.forField(estadoSalud)
                .asRequired("Estado de salud requerido")
                .bind(Mascota::getEstadoSalud, Mascota::setEstadoSalud);

        binder.forField(institucion)
                .asRequired("Institucion requerida")
                .bind(Mascota::getId_institucion, Mascota::setId_institucion);

        // Save enable only when valid
        save.setEnabled(false);
        binder.addStatusChangeListener(s -> save.setEnabled(binder.isValid()));

        // Buttons style
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        goInstituciones.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        // Actions layout
        HorizontalLayout actions = new HorizontalLayout(save, delete, clear, goInstituciones);

        // Add components
        add(search, tamanio, estadoAdopcion, estadoSalud, edad, raza, especie, institucion, actions, grid);
        setColspan(search, 2);
        setColspan(actions, 2);
        setColspan(grid, 2);

        // Listeners
        grid.asSingleSelect().addValueChangeListener(evt -> {
            if (evt.getValue() == null) {
                edit(null);
            } else {
                edit(evt.getValue());
            }
        });

        save.addClickListener(e -> save());
        delete.addClickListener(e -> confirmDelete());
        clear.addClickListener(e -> edit(null));
        goInstituciones.addClickListener(e -> UI.getCurrent().navigate("instituciones"));

        refreshGrid();
        edit(null);
    }

    private void edit(Mascota m) {
        if (m == null) {
            current = null;
            binder.readBean(new Mascota());
            delete.setEnabled(false);
            save.setText("Crear");
        } else {
            current = m;
            binder.readBean(current);
            delete.setEnabled(true);
            save.setText("Actualizar");
        }
    }

    private void save() {
        Mascota toSave = current == null ? new Mascota() : current;
        try {
            binder.writeBean(toSave);
        } catch (Exception ex) {
            log.error("Validation error", ex);
            Notification.show("Corrige los errores del formulario");
            return;
        }

        try {
            mascotaService.agregarMascota(toSave);
            Notification.show("Mascota guardada");
            refreshGrid();
            edit(null);
        } catch (Exception ex) {
            Notification.show("Error al guardar: " + ex.getMessage());
        }
    }

    private void confirmDelete() {
        if (current == null) return;
        Dialog d = new Dialog();
        d.add("¿Confirmar eliminación de la mascota?");
        Button yes = new Button("Eliminar", ev -> {
            try {
                mascotaService.eliminarMascota(current.getId());
                Notification.show("Mascota eliminada");
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
        grid.setItems(mascotaService.buscarMascotas());
        try {
            institucion.setItems(institucionService.buscarInstituciones());
        } catch (Exception e) {
            institucion.setItems(institucionService.buscarInstituciones());
        }
    }

    private void updateGridFilter() {
        String f = search.getValue() == null ? "" : search.getValue().trim().toLowerCase();
        grid.setItems(mascotaService.buscarMascotas().stream()
                .filter(m -> (m.getEspecie() != null && m.getEspecie().toLowerCase().contains(f)) ||
                        (m.getRaza() != null && m.getRaza().toLowerCase().contains(f)))
                .collect(Collectors.toList()));
    }
}