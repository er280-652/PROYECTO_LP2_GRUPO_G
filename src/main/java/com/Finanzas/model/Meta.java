
package com.Finanzas.model;

import java.time.LocalDate;

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
@Table(name = "tbl_meta")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class Meta {

	@Id
	@Column(name = "id_meta")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idMeta;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "descripcion")
	private String descripcion;
	
	@Column(name = "monto_objetivo")
	private Double montoObjetivo;
	
	@Column(name = "monto_actual")
	private Double montoActual;
	
	@Column(name = "fecha_objetivo")
	private LocalDate fechaObjetivo;
	
	@Column(name = "completada")
	private Boolean completada;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
}
