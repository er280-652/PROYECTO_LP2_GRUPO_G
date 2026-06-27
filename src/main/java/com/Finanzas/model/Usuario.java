
package com.Finanzas.model;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
	private Integer idUsuario;
	
	@Column(name = "nombres")
	private String nombres;
	
	@Column(name = "apellidos")
	private String apellidos;
	
	
	@Column(name = "cuenta")
	private String cuenta;
	
	@Column(name = "clave")
	private String clave;
	
	
	@Column(name = "fecha_nac")
	private LocalDate fecha_nac;
	
	@Column(name = "estado")
	private Boolean activo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo")
	private Tipo tipo;
	
	public String getFullName() {
		return String.format("%s %s", nombres, apellidos);
	}
	
	public String getActivoDescripcion() {
		return activo ? "Activo" : "Inactivo";
	}
	
}
