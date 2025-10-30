package com.surrogate.Zoolip.webTest;

import com.surrogate.Zoolip.models.DTO.ComentarioDTO;
import com.surrogate.Zoolip.models.DTO.PublicacionDTO;
import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Comentario;
import com.surrogate.Zoolip.models.bussiness.Publicacion.Publicacion;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.services.auth.UsuarioService;
import com.surrogate.Zoolip.services.bussiness.ComentarioService;
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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route("comentarios")
@UIScope
@PageTitle("Comentarios")
public class ComentarioView extends FormLayout {
    private final ComentarioService comentarioService;
    private final PublicacionService publicacionService;
    private final UsuarioService usuarioService;

    private final Binder<Comentario> binder = new Binder<>(Comentario.class);
    private Comentario current;

    private final ComboBox<PublicacionDTO> publicacion = new ComboBox<>("Publicación");
    private final TextArea contenido = new TextArea("Contenido");
    private final ComboBox<UsuarioDto> usuario = new ComboBox<>("Usuario");

    private final Button save = new Button("Guardar", VaadinIcon.CHECK.create());
    private final Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());
    private final Button clear = new Button("Limpiar", VaadinIcon.ERASER.create());

    private final Grid<ComentarioDTO> grid = new Grid<>(ComentarioDTO.class, false);

    @Autowired
    public ComentarioView(ComentarioService comentarioService,
                          PublicacionService publicacionService,
                          UsuarioService usuarioService) {
        this.comentarioService = comentarioService;
        this.publicacionService = publicacionService;
        this.usuarioService = usuarioService;

        add(new H3("Gestión de Comentarios"));

        contenido.setWidthFull();
        contenido.setMaxLength(1000);

        publicacion.setItems(publicacionService.obtenerTodas());
        publicacion.setItemLabelGenerator(p -> p == null ? "" : (p.idPublicacion() + " - " + (p.topico() == null ? "" : p.topico())));

        usuario.setItemLabelGenerator(u -> u == null ? "" : u.nombre());
        usuario.setItems(usuarioService.findAvailableUsersInitialized());

        grid.addColumn(ComentarioDTO::idComentario).setHeader("ID");
        grid.addColumn(ComentarioDTO::contenido).setHeader("Contenido");
        grid.addColumn(ComentarioDTO::nombreUsuario).setHeader("Usuario");
        grid.addColumn(ComentarioDTO::idPublicacion).setHeader("Publicación ID");
        grid.setHeight("420px");
        grid.addThemeName("compact");

        binder.forField(contenido)
                .asRequired("Contenido requerido")
                .bind(Comentario::getContenido, Comentario::setContenido);

        // Keep selection validated but set actual Publicacion entity manually on save
        binder.forField(publicacion)
                .asRequired("Publicación requerida")
                .bind(c -> null, (c, dto) -> {
                });

        binder.forField(usuario)
                .asRequired("Usuario requerido")
                .bind(c -> c.getId_usuario() == null ? null : new UsuarioDto(c.getId_usuario().getId(), c.getId_usuario().getNombre(), c.getId_usuario().getRol(), "santiagoabsalom@gmail.com"),
                        (c, u) -> {
                        });

        save.setEnabled(false);
        binder.addStatusChangeListener(s -> save.setEnabled(binder.isValid()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(save, delete, clear);

        add(publicacion, contenido, usuario, actions, grid);
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

    private void edit(Comentario c) {
        if (c == null) {
            current = null;
            binder.readBean(new Comentario());
            delete.setEnabled(false);
            save.setText("Crear");
            usuario.clear();
        } else {
            current = c;
            binder.readBean(current);
            delete.setEnabled(true);
            save.setText("Actualizar");
        }
    }

    private void editByDto(ComentarioDTO dto) {
        if (dto == null) {
            edit(null);
            return;
        }
        Comentario c = new Comentario();
        c.setId_comentario(dto.idComentario());
        c.setContenido(dto.contenido());
        // user/publicacion selection left for UI
        edit(c);
    }

    private void save() {
        Comentario toSave = current == null ? new Comentario() : current;
        try {
            binder.writeBean(toSave);
        } catch (Exception ex) {
            Notification.show("Corrige los errores del formulario");
            return;
        }

        if (usuario.getValue() != null) {
            usuarioService.findById(usuario.getValue().id()).ifPresent(toSave::setId_usuario);
        }
        if (publicacion.getValue() != null) {
            // create a lightweight Publicacion entity with only id set
            Publicacion p = new Publicacion();
            p.setId_publicacion(publicacion.getValue().idPublicacion());
            toSave.setId_publicacion(p);
        }

        try {
            Response res;
            if (toSave.getId_comentario() == null) {
                res = comentarioService.comentar(toSave);
            } else {
                res = comentarioService.actualizar(toSave);
            }
            if (res.getStatus().equals("error")) {
                Notification.show("Error: " + res.getMessage());
            } else {
                Notification.show("Comentario guardado");
                refreshGrid();
                edit(null);
            }
        } catch (Exception ex) {
            Notification.show("Error al guardar: " + ex.getMessage());
            log.error("Save comentario failed", ex);
        }
    }

    private void confirmDelete() {
        if (current == null || current.getId_comentario() == null) return;
        Dialog d = new Dialog();
        d.add("¿Confirmar eliminación del comentario?");
        Button yes = new Button("Eliminar", ev -> {
            try {
                comentarioService.eliminar(current);
                Notification.show("Comentario eliminado");
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
            var items = comentarioService.buscarComentarios();
            if (items != null) grid.setItems(items);
        } catch (Exception e) {
            log.error("Failed to refresh comentarios", e);
        }
        try {
            publicacion.setItems(publicacionService.obtenerTodas());
        } catch (Exception ignored) {
        }
        try {
            usuario.setItems(usuarioService.findAvailableUsersInitialized());
        } catch (Exception ignored) {
        }
    }
}
