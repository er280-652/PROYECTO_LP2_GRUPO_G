
package com.Finanzas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "tbl_usuario")
public class Usuario {

	@Column(name = "id_usuario")
	private Integer usuario;
	
	@Column(name = "nombres")
	private String nombres;
	
	@Column(name = "apellidos")
	private String apellidos;
	
	
	@Column(name = "correo")
	private String correo;
	
	@Column(name = "clave")
	private String clave;
	
	@Column(name = "telefono")
	private String telefono;
	
	@Column(name = "fecha_registro")
	private LocalDateTime fechaRegistro;
	
	@Column(name = "activo")
	private Boolean activo;
	
	@ManyToOne
	@JoinColumn(name = "id_tipo")
	private Integer idTipo;
}
