package com.surrogate.Zoolip.models.bussiness;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "usuarios", schema = "zoolip", uniqueConstraints = {
        @UniqueConstraint(name = "usuario", columnNames = {"nombre"})
})


public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Lob
    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;






}

