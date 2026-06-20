
package com.Finanzas.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	private Tipo tipo;
}
