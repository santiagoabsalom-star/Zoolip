package com.surrogate.Zoolip.webTest;

import com.surrogate.Zoolip.models.DTO.AtiendeDTO;
import com.surrogate.Zoolip.models.bussiness.Atencion.Atiende;
import com.surrogate.Zoolip.models.bussiness.Atencion.AtiendeId;
import com.surrogate.Zoolip.models.bussiness.Atencion.Veterinario;
import com.surrogate.Zoolip.models.bussiness.Atencion.Diagnostico;
import com.surrogate.Zoolip.models.bussiness.Mascota.Mascota;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.repository.bussiness.DiagnosticoRepository;
import com.surrogate.Zoolip.services.bussiness.AtencionService;
import com.surrogate.Zoolip.services.bussiness.MascotaService;
import com.surrogate.Zoolip.services.bussiness.VeterinarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Route("atenciones")
@UIScope
@PageTitle("Atenciones")
public class AtencionView extends FormLayout {
    private final AtencionService atencionService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final DiagnosticoRepository diagnosticoRepository;

    private final Binder<Atiende> binder = new Binder<>(Atiende.class);
    private Atiende current;

    private final ComboBox<Mascota> mascota = new ComboBox<>("Mascota");
    private final ComboBox<Diagnostico> diagnostico = new ComboBox<>("Diagnóstico");
    private final ComboBox<Veterinario> veterinario = new ComboBox<>("Veterinario");

    private final DatePicker fechaInicio = new DatePicker("Fecha inicio");
    private final DatePicker fechaFinal = new DatePicker("Fecha final");

    private final Button save = new Button("Guardar", VaadinIcon.CHECK.create());
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    private final Button clear = new Button("Limpiar", VaadinIcon.ERASER.create());

    private final Grid<AtiendeDTO> grid = new Grid<>(AtiendeDTO.class, false);

    @Autowired
    public AtencionView(AtencionService atencionService,
                        MascotaService mascotaService,
                        VeterinarioService veterinarioService,
                        DiagnosticoRepository diagnosticoRepository) {
        this.atencionService = atencionService;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
        this.diagnosticoRepository = diagnosticoRepository;

        add(new H3("Gestión de Atenciones"));

        mascota.setItems(mascotaService.buscarMascotas());
        mascota.setItemLabelGenerator(m -> m == null ? "" : (m.getId() + " - " + m.getEspecie()));
        Veterinario veterinario1 = new Veterinario();

        veterinarioService.crear()
        veterinario.setItems(veterinarioService.obtenerVeterinarios().stream().map(dto -> {
            Veterinario v = new Veterinario();
            v.setId_veterinario(dto.id());
            v.setNombre(dto.nombre());
            return v;
        }).toList());
        veterinario.setItemLabelGenerator(Veterinario::getNombre);

        diagnostico.setItems(diagnosticoRepository.findAll());
        diagnostico.setItemLabelGenerator(d -> d == null ? "" : d.getExamen_fisico());

        grid.addColumn(AtiendeDTO::idDiagnostico).setHeader("Diagnóstico ID");
        grid.addColumn(AtiendeDTO::idMascota).setHeader("Mascota ID");
        grid.addColumn(AtiendeDTO::idVeterinario).setHeader("Veterinario ID");
        grid.addColumn(AtiendeDTO::fechaInicio).setHeader("Fecha inicio");
        grid.addColumn(AtiendeDTO::fechaFinal).setHeader("Fecha final");
        grid.addThemeName("compact");
        grid.setHeight("420px");

        // Validate selections; actual entity fields will be set on save
        binder.forField(mascota).asRequired("Mascota requerida").bind(a -> null, (a,m) -> {});
        binder.forField(diagnostico).asRequired("Diagnóstico requerido").bind(a -> null, (a,d) -> {});
        binder.forField(veterinario).asRequired("Veterinario requerido").bind(a -> null, (a,v) -> {});

        save.setEnabled(false);
        binder.addStatusChangeListener(s -> save.setEnabled(binder.isValid()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(save, delete, clear);

        add(mascota, diagnostico, veterinario, fechaInicio, fechaFinal, actions, grid);
        setColspan(actions, 2);
        setColspan(grid, 2);

        grid.asSingleSelect().addValueChangeListener(evt -> editByDto(evt.getValue()));

        save.addClickListener(e -> save());
        delete.addClickListener(e -> confirmDelete());
        clear.addClickListener(e -> edit(null));

        refreshGrid();
        edit(null);
    }

    private void edit(Atiende a) {
        if (a == null) { current = null; binder.readBean(new Atiende()); delete.setEnabled(false); save.setText("Crear"); }
        else { current = a; binder.readBean(current); delete.setEnabled(true); save.setText("Actualizar"); }
    }

    private void editByDto(AtiendeDTO dto) {
        if (dto == null) { edit(null); return; }
        Atiende a = new Atiende();
        AtiendeId id = new AtiendeId();
        // populate id partials
        a.setId(id);
        edit(a);
    }

    private void save() {
        Atiende toSave = current == null ? new Atiende() : current;
        try { binder.writeBean(toSave); } catch (Exception ex) { Notification.show("Corrige los errores del formulario"); return; }

        // set selected entities
        if (diagnostico.getValue() != null) toSave.setDiagnostico(diagnostico.getValue());
        if (mascota.getValue() != null) toSave.setMascota(mascota.getValue());
        if (veterinario.getValue() != null) toSave.setVeterinario(veterinario.getValue());

         // set dates as LocalDateTime around selected day
         if (fechaInicio.getValue() != null) toSave.setFecha_inicio(LocalDateTime.of(fechaInicio.getValue(), LocalTime.NOON));
         if (fechaFinal.getValue() != null) toSave.setFecha_final(LocalDateTime.of(fechaFinal.getValue(), LocalTime.NOON));

         try {
             Response res;
             if (toSave.getId() == null) {
                 res = atencionService.empezar(toSave);
             } else {
                 res = atencionService.actualizar(toSave);
             }
             if (res.getStatus().equals("error")) Notification.show("Error: " + res.getMessage());
             else { Notification.show("Atención guardada"); refreshGrid(); edit(null); }
         } catch (Exception ex) { Notification.show("Error al guardar: " + ex.getMessage()); log.error("Save atencion failed", ex); }
     }

    private void confirmDelete() {
        if (current == null || current.getId() == null) return;
        Dialog d = new Dialog();
        d.add("¿Confirmar eliminación de la atención?");
        Button yes = new Button("Eliminar", ev -> {
            try { atencionService.eliminar(current); Notification.show("Atención eliminada"); refreshGrid(); edit(null); } catch (Exception ex) { Notification.show("Error al eliminar: " + ex.getMessage()); } finally { d.close(); }
        });
        yes.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button no = new Button("Cancelar", ev -> d.close());
        no.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        d.add(new HorizontalLayout(yes, no));
        d.open();
    }

    private void refreshGrid() {
        try { var items = atencionService.obtenerTodas(); if (items != null) grid.setItems(items); } catch (Exception e) { log.error("Failed to refresh atenciones", e); }
    }
}
