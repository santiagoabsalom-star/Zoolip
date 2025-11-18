package com.surrogate.Zoolip.webTest;

import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Publicacion;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.auth.UsuarioService;
import com.surrogate.Zoolip.services.bussiness.PublicacionService;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route("publicaciones")
@UIScope
@PageTitle("Publicaciones")
public class PublicacionView extends FormLayout {
    private final PublicacionService publicacionService;
    private final UsuarioService usuarioService;
    private final long id=10;
    private final Binder<Publicacion> binder = new Binder<>(Publicacion.class);
    private Publicacion current;

    private final TextField topico = new TextField("Tópico");
    private final TextArea contenido = new TextArea("Contenido");
    private final ComboBox<UsuarioDto> usuario = new ComboBox<>("Usuario");

    private final Button save = new Button("Guardar", VaadinIcon.CHECK.create());
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    private final Button clear = new Button("Limpiar", VaadinIcon.ERASER.create());

    private final Grid<PublicacionDTO> grid = new Grid<>(PublicacionDTO.class, false);

    @Autowired
    public PublicacionView(PublicacionService publicacionService,
                           UsuarioService usuarioService) {
        this.publicacionService = publicacionService;
        this.usuarioService = usuarioService;

        add(new H3("Gestión de Publicaciones"));

        topico.setWidth("400px");
        contenido.setWidthFull();
        contenido.setMaxLength(2000);

        usuario.setItemLabelGenerator(u -> u == null ? "" : u.nombre());
        usuario.setItems(usuarioService.findAvailableUsersInitialized());

        grid.addColumn(PublicacionDTO::idPublicacion).setHeader("ID");
        grid.addColumn(PublicacionDTO::topico).setHeader("Tópico");
        grid.addColumn(PublicacionDTO::contenido).setHeader("Contenido");
        grid.addColumn(PublicacionDTO::nombreUsuario).setHeader("Usuario");
        grid.addColumn(PublicacionDTO::likes).setHeader("Likes");
        grid.setHeight("420px");
        grid.addThemeName("compact");

        binder.forField(topico)
                .asRequired("Tópico requerido")
                .bind(Publicacion::getTopico, Publicacion::setTopico);

        binder.forField(contenido)
                .asRequired("Contenido requerido")
                .withValidator(v -> v != null && !v.trim().isEmpty(), "Contenido requerido")
                .bind(Publicacion::getContenido, Publicacion::setContenido);

        binder.forField(usuario)
                .asRequired("Usuario requerido")
                .bind(p -> p.getId_usuario() == null ? null : new UsuarioDto(p.getId_usuario().getId(), p.getId_usuario().getNombre(), p.getId_usuario().getRol(), "santiagoabsalom@gmail.com","ERI","ERO"),
                        (p, u) -> {
                        });

        save.setEnabled(false);
        binder.addStatusChangeListener(s -> save.setEnabled(binder.isValid()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(save, delete, clear);

        add(topico, contenido, usuario, actions, grid);
        setColspan(contenido, 2);
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

    private void edit(Publicacion p) {
        if (p == null) {
            current = null;
            binder.readBean(new Publicacion());
            delete.setEnabled(false);
            save.setText("Crear");
            usuario.clear();
        } else {
            current = p;
            binder.readBean(current);
            delete.setEnabled(true);
            save.setText("Actualizar");
        }
    }

    private void editByDto(PublicacionDTO dto) {
        if (dto == null) {
            edit(null);
            return;
        }
        Publicacion p = new Publicacion();
        p.setId_publicacion(dto.idPublicacion());
        p.setContenido(dto.contenido());
        p.setTopico(dto.topico());
        edit(p);
    }

    private void save() {
        Publicacion toSave = current == null ? new Publicacion() : current;
        try {
            binder.writeBean(toSave);
        } catch (Exception ex) {
            Notification.show("Corrige los errores del formulario");
            return;
        }

        if (usuario.getValue() != null) {
            usuarioService.findById(usuario.getValue().id()).ifPresent(toSave::setId_usuario);
        }

        try {
            Response res;
            if (toSave.getId_publicacion() == null) {
                res = publicacionService.crear(toSave);
            } else {
                res = publicacionService.actualizar(toSave);
            }
            if (res.getStatus().equals("error")) {
                Notification.show("Error: " + res.getMessage());
            } else {
                Notification.show("Publicación guardada");
                refreshGrid();
                edit(null);
            }
        } catch (Exception ex) {
            Notification.show("Error al guardar: " + ex.getMessage());
            log.error("Save publicacion failed", ex);
        }
    }

    private void confirmDelete() {
        if (current == null || current.getId_publicacion() == null) return;
        Dialog d = new Dialog();
        d.add("¿Confirmar eliminación de la publicación?");
        Button yes = new Button("Eliminar", ev -> {
            try {
                publicacionService.eliminar(current.getId_publicacion());
                Notification.show("Publicación eliminada");
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
            var items = publicacionService.obtenerTodasPaginacion(id);
            if (items != null) grid.setItems(items);
        } catch (Exception e) {
            log.error("Failed to refresh publicaciones", e);
        }
        try {
            usuario.setItems(usuarioService.findAvailableUsersInitialized());
        } catch (Exception ignored) {
        }
    }
}
