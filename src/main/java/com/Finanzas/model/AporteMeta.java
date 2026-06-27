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
@Table(name = "tbl_aporte_meta")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class AporteMeta {

	@Id
	@Column(name = "id_aporte")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idAporte;
	
	@Column(name = "fecha")
	private LocalDate fecha;
	
	@Column(name = "monto_aporte")
	private Double montoAporte;
	
	@Column(name = "observacion")
	private String observacion;
	
	@Column(name = "activo")
	private Boolean activo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_meta")
	private Meta meta;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_movimiento")
	private Movimiento movimiento;
	
	public String getActivoDescripcion() {
		return Boolean.TRUE.equals(activo) ? "Activo" : "Inactivo";
	}
}
