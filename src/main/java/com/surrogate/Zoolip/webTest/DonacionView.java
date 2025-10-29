package com.surrogate.Zoolip.webTest;

import com.surrogate.Zoolip.models.DTO.DonacionDTO;
import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Donacion.Donacion;
import com.surrogate.Zoolip.models.bussiness.Donacion.ESTATUS;
import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.auth.UsuarioService;
import com.surrogate.Zoolip.services.bussiness.DonacionService;
import com.surrogate.Zoolip.services.bussiness.InstitucionService;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@Slf4j
@Route("donaciones")
@UIScope
@PageTitle("Donaciones")
public class DonacionView extends FormLayout {
    private final DonacionService donacionService;
    private final UsuarioService usuarioService;
    private final InstitucionService institucionService;
    private final UI ui;

    private final Binder<Donacion> binder = new Binder<>(Donacion.class);
    private Donacion current;

    private final NumberField monto = new NumberField("Monto");
    private final ComboBox<ESTATUS> status = new ComboBox<>("Estado");
    private final ComboBox<UsuarioDto> usuario = new ComboBox<>("Usuario");
    private final ComboBox<Institucion> institucion = new ComboBox<>("Institución");

    private final Button save = new Button("Guardar", VaadinIcon.CHECK.create());
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    private final Button clear = new Button("Limpiar", VaadinIcon.ERASER.create());

    private final Grid<DonacionDTO> grid = new Grid<>(DonacionDTO.class, false);

    @Autowired
    public DonacionView(DonacionService donacionService,
                        UsuarioService usuarioService,
                        InstitucionService institucionService) {
        this.donacionService = donacionService;
        this.usuarioService = usuarioService;
        this.institucionService = institucionService;
        this.ui = UI.getCurrent();

        add(new H3("Gestión de Donaciones"));

        monto.setMin(0);
        monto.setStep(0.5);
        monto.setWidth("200px");

        status.setItems(ESTATUS.values());
        status.setWidth("200px");

        usuario.setItemLabelGenerator(u -> u == null ? "" : u.nombre());
        usuario.setWidth("320px");
        if (usuarioService.findAvailableUsersInitialized() != null) {
            usuario.setItems(usuarioService.findAvailableUsersInitialized());
        }

        institucion.setItems(institucionService.buscarInstituciones());
        institucion.setItemLabelGenerator(Institucion::getNombre);
        institucion.setWidth("320px");

        // Grid
        grid.addColumn(DonacionDTO::idDonacion).setHeader("ID");
        grid.addColumn(DonacionDTO::monto).setHeader("Monto");
        grid.addColumn(DonacionDTO::fechaInicio).setHeader("Fecha");
        grid.addColumn(DonacionDTO::estado).setHeader("Estado");
        grid.addColumn(DonacionDTO::nombreUsuario).setHeader("Usuario");
        grid.addColumn(DonacionDTO::nombreInstitucion).setHeader("Institución");
        grid.setHeight("420px");
        grid.addThemeName("compact");

        // Binder
        binder.forField(monto)
                .asRequired("Monto requerido")
                .withValidator(v -> v != null && v.doubleValue() >= 0, "Monto debe ser >= 0")
                .bind(Donacion::getMonto, Donacion::setMonto);

        binder.forField(status)
                .asRequired("Estado requerido")
                .bind(Donacion::getStatus, Donacion::setStatus);

        binder.forField(usuario)
                .asRequired("Usuario requerido")
                .bind(d -> d.getId_usuario() == null ? null : new UsuarioDto(d.getId_usuario().getId(), d.getId_usuario().getNombre(), d.getId_usuario().getRol()),
                        (d, u) -> {});

        binder.forField(institucion)
                .asRequired("Institución requerida")
                .bind(Donacion::getId_institucion, Donacion::setId_institucion);

        save.setEnabled(false);
        binder.addStatusChangeListener(s -> save.setEnabled(binder.isValid()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(save, delete, clear);

        add(monto, status, usuario, institucion, actions, grid);
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

    private void edit(Donacion d) {
        if (d == null) {
            current = null;
            binder.readBean(new Donacion());
            delete.setEnabled(false);
            save.setText("Crear");
            usuario.clear();
        } else {
            current = d;
            binder.readBean(current);
            delete.setEnabled(true);
            save.setText("Actualizar");
            if (current.getId_usuario() != null) {
                usuarioService.findAvailableUsersInitialized().stream()
                        .filter(u -> u.id().equals(current.getId_usuario().getId()))
                        .findFirst()
                        .ifPresent(usuario::setValue);
            }
        }
    }

    private void editByDto(DonacionDTO dto) {
        if (dto == null) { edit(null); return; }
        Donacion d = new Donacion();
        d.setId_donacion(dto.idDonacion());
        d.setMonto(dto.monto());
        d.setStatus(dto.estado());
        // users/institutions are left for selection in UI
        edit(d);
    }

    private void save() {
        Donacion toSave = current == null ? new Donacion() : current;
        try {
            binder.writeBean(toSave);
        } catch (Exception ex) {
            Notification.show("Corrige los errores del formulario");
            return;
        }

        if (usuario.getValue() != null) {
            Usuario u = usuarioService.findById(usuario.getValue().id()).orElseThrow();
            toSave.setId_usuario(u);
        }
        if (institucion.getValue() != null) {
            toSave.setId_institucion(institucion.getValue());
        }

        try {
            Response response;
            if (toSave.getId_donacion() == null) {
                response = donacionService.crearDonacion(toSave);
            } else {
                response = donacionService.actualizarDonacion(toSave);
            }
            if (response.getStatus().equals("error")) {
                Notification.show("Error: " + response.getMessage());
            } else {
                Notification.show("Donación guardada");
                refreshGrid();
                edit(null);
            }
        } catch (Exception ex) {
            Notification.show("Error al guardar: " + ex.getMessage());
            log.error("Save donacion failed", ex);
        }
    }

    private void confirmDelete() {
        if (current == null || current.getId_donacion() == null) return;
        Dialog d = new Dialog();
        d.add("¿Confirmar eliminación de la donación?");
        Button yes = new Button("Eliminar", ev -> {
            try {
                donacionService.eliminarDonacion(current);
                Notification.show("Donación eliminada");
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
            var items = donacionService.obtenerDonaciones();
            if (items != null) grid.setItems(items);
        } catch (Exception e) {
            log.error("Failed to refresh donaciones", e);
        }
        try {
            usuario.setItems(usuarioService.findAvailableUsersInitialized());
        } catch (Exception ignore) {}
        try {
            institucion.setItems(institucionService.buscarInstituciones());
        } catch (Exception ignore) {}
    }

    private void updateGridFilter() {
        // simple filter placeholder - not implemented
        grid.getDataProvider().refreshAll();
    }
}
