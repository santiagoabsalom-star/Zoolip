
package com.surrogate.Zoolip.webTest;

import com.surrogate.Zoolip.events.UsuarioNotifier;
import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Institucion.Institucion;
import com.surrogate.Zoolip.models.bussiness.Institucion.Tipo;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.auth.UsuarioService;
import com.surrogate.Zoolip.services.bussiness.InstitucionService;
import com.surrogate.Zoolip.services.bussiness.MascotaService;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Route("instituciones")
@UIScope
@PageTitle("Instituciones")
public class InstitucionView extends FormLayout {
    private final UI ui;
    private final InstitucionService institucionService;
    private final UsuarioService usuarioService;
    private final MascotaService mascotaService;
    private final UsuarioNotifier notifier;
    private final Binder<Institucion> binder = new Binder<>(Institucion.class);
    private Institucion current;

    private final TextField search = new TextField();
    private final ComboBox<UsuarioDto> usuario = new ComboBox<>("Usuario (responsable)");
    private final TextField nombre = new TextField("Nombre");
    private final ComboBox<Tipo> tipo = new ComboBox<>("Tipo");
    private final EmailField email = new EmailField("Email");
    private final TextArea descripcion = new TextArea("Descripción");
    private final TimePicker horarioInicio = new TimePicker("Horario inicio");
    private final TimePicker horarioFin = new TimePicker("Horario fin");

    private final Button save = new Button("Guardar", VaadinIcon.CHECK.create());
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    private final Button clear = new Button("Limpiar", VaadinIcon.ERASER.create());
    private final Button goMascotas = new Button("Ir a Mascotas", VaadinIcon.PLAY.create());

    private final Grid<Institucion> grid = new Grid<>(Institucion.class, false);

    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    public InstitucionView(InstitucionService institucionService,
                           UsuarioService usuarioService,
                           MascotaService mascotaService, UsuarioNotifier notifier) {
        this.institucionService = institucionService;
        this.usuarioService = usuarioService;
        this.mascotaService = mascotaService;
        this.notifier = notifier;
        this.ui = UI.getCurrent();

        // Header + search
        add(new H3("Gestión de Instituciones"));
        search.setPlaceholder("Buscar por nombre...");
        search.setClearButtonVisible(true);
        search.addValueChangeListener(e -> updateGridFilter());
        search.setWidthFull();

        // Inputs styling
        nombre.setWidthFull();
        descripcion.setWidthFull();
        email.setWidth("320px");
        usuario.setWidth("320px");
        tipo.setWidth("200px");
        horarioInicio.setWidth("200px");
        horarioFin.setWidth("200px");

        usuario.setItemLabelGenerator(u -> u == null ? "" : u.nombre());
        if (usuarioService.findAvailableUsersInitialized() != null) {
            usuario.setItems(usuarioService.findAvailableUsersInitialized());
        }

        tipo.setItems(Tipo.values());

        horarioInicio.setStep(Duration.ofHours(1));
        horarioFin.setStep(Duration.ofHours(1));
        horarioInicio.setLocale(new Locale("es", "UY"));
        horarioFin.setLocale(new Locale("es", "UY"));
        horarioInicio.setMin(LocalTime.of(6, 0));
        horarioInicio.setMax(LocalTime.of(23, 0));
        horarioFin.setMin(LocalTime.of(6, 0));
        horarioFin.setMax(LocalTime.of(23, 59));
        horarioInicio.setClearButtonVisible(true);
        horarioFin.setClearButtonVisible(true);
        horarioInicio.setPlaceholder("08:00");
        horarioFin.setPlaceholder("17:00");
        horarioInicio.setHelperText("Apertura");
        horarioFin.setHelperText("Cierre (debe ser mayor que apertura)");

        // Binder + validation
        binder.forField(nombre).asRequired("Nombre requerido").bind(Institucion::getNombre, Institucion::setNombre);
        binder.forField(tipo).asRequired("Tipo requerido").bind(Institucion::getTipo, Institucion::setTipo);
        binder.forField(email)
                .asRequired("Email requerido")
                .withValidator(new EmailValidator("Email inválido"))
                .bind(Institucion::getEmail, Institucion::setEmail);
        binder.forField(descripcion).asRequired("Descripción requerida").bind(Institucion::getDescripcion, Institucion::setDescripcion);
        binder.forField(horarioInicio)
                .asRequired("Horario inicio requerido")
                .withValidator(Objects::nonNull, "Horario inicio inválido")
                .bind(Institucion::getHorario_inicio, Institucion::setHorario_inicio);
        binder.forField(horarioFin)
                .asRequired("Horario fin requerido")
                .withValidator(Objects::nonNull, "Horario fin inválido")
                .bind(Institucion::getHorario_fin, Institucion::setHorario_fin);

        // Grid config
        grid.addColumn(Institucion::getId_institucion).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Institucion::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Institucion::getTipo).setHeader("Tipo").setAutoWidth(true);
        grid.addColumn(Institucion::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Institucion::getDescripcion).setHeader("Descripción");
        grid.addColumn(i -> i.getHorario_inicio() == null ? "-" : i.getHorario_inicio().format(HORA_FMT)).setHeader("Horario inicio");
        grid.addColumn(i -> i.getHorario_fin() == null ? "-" : i.getHorario_fin().format(HORA_FMT)).setHeader("Horario fin");
        grid.addColumn(i -> i.getId_usuario() == null ? "-" : i.getId_usuario().getNombre()).setHeader("Usuario");
        grid.setHeight("420px");
        grid.addThemeName("compact");

        // Responsive form layout
        setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("700px", 2)
        );

        // Bind status -> enable save only when valid
        save.setEnabled(false);
        binder.addStatusChangeListener(s -> save.setEnabled(binder.isValid()));

        // Buttons styling
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        goMascotas.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        // Actions
        HorizontalLayout actions = new HorizontalLayout(save, delete, clear, goMascotas);

        // Add to layout
        add(search, usuario, nombre, tipo, email, descripcion, horarioInicio, horarioFin, actions, grid);
        setColspan(search, 2);
        setColspan(descripcion, 2);
        setColspan(actions, 2);
        setColspan(grid, 2);

        // Listeners
        grid.asSingleSelect().addValueChangeListener(evt -> {
            if (evt.getValue() == null) edit(null);
            else edit(evt.getValue());
        });

        save.addClickListener(e -> save());
        delete.addClickListener(e -> confirmDelete());
        clear.addClickListener(e -> edit(null));
        goMascotas.addClickListener(e -> UI.getCurrent().navigate("mascotas"));

        refreshGrid();

        notifier.addListener(e -> {
            getUI().ifPresent(u -> u.access(() -> {
                refreshGrid();
                Notification.show("Nuevo usuario creado");
            }));
        });

        edit(null);
    }

    private void edit(Institucion inst) {
        if (inst == null) {
            current = null;
            Institucion empty = new Institucion();
            empty.setHorario_inicio(LocalTime.of(9, 0));
            empty.setHorario_fin(LocalTime.of(17, 0));
            binder.readBean(empty);
            delete.setEnabled(false);
            save.setText("Crear");
            usuario.clear();
        } else {
            current = inst;
            binder.readBean(current);
            delete.setEnabled(true);
            save.setText("Actualizar");
            if (inst.getId_usuario() != null) {
                Long uid = inst.getId_usuario().getId();
                usuarioService.findAvailableUsersInitialized().stream()
                        .filter(d -> d.id().equals(uid))
                        .findFirst()
                        .ifPresent(usuario::setValue);

                if (usuario.getValue() == null) {
                    usuarioService.findById(uid).ifPresent(u -> usuario.setValue(new UsuarioDto(u.getId(), u.getNombre(), u.getRol())));
                }
            } else {
                usuario.clear();
            }
        }
    }

    private void save() {
        Institucion toSave = current == null ? new Institucion() : current;
        try {
            binder.writeBean(toSave);
        } catch (Exception ex) {
            Notification.show("Corrige los errores del formulario");
            return;
        }

        if (usuario.getValue() != null) {
            Usuario u = usuarioService.findById(usuario.getValue().id())
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
            toSave.setId_usuario(u);
        } else {
            toSave.setId_usuario(null);
        }

        LocalTime start = toSave.getHorario_inicio();
        LocalTime end = toSave.getHorario_fin();
        if (start == null || end == null) {
            Notification.show("Ambos horarios deben estar definidos");
            return;
        }
        if (!end.isAfter(start)) {
            Notification.show("El horario de fin debe ser mayor que el de inicio");
            return;
        }

        try {
            Response response = institucionService.crear(toSave);
            if (Objects.equals(response.getStatus(), "error")) {
                Notification.show("Error al crear la institución: " + response.getMessage());
            } else {
                Notification.show("Institución guardada");
                refreshGrid();
                edit(null);
            }
        } catch (Exception ex) {
            Notification.show("Error al guardar: " + ex.getMessage());
            log.error("Save institucion failed", ex);
        }
    }

    private void confirmDelete() {
        if (current == null) return;
        Dialog d = new Dialog();
        d.add("¿Confirmar eliminación de la institución?");
        Button yes = new Button("Eliminar", ev -> {
            try {
                institucionService.eliminar(current.getId_institucion());
                Notification.show("Institución eliminada");
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
        grid.setItems(institucionService.buscarInstituciones());
        usuario.setItems(usuarioService.findAvailableUsersInitialized());
    }

    private void updateGridFilter() {
        String f = search.getValue() == null ? "" : search.getValue().trim().toLowerCase();
        grid.setItems(institucionService.buscarInstituciones().stream()
                .filter(i -> i.getNombre() != null && i.getNombre().toLowerCase().contains(f))
                .collect(Collectors.toList()));
    }
}