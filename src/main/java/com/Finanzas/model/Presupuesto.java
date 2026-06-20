package com.Finanzas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_presupuesto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Presupuesto {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_presupuesto")
	private Integer idPresupuesto;
	
	@Column(name = "mes")
	private String mes;
	
	@Column(name = "anio")
	private Integer anio;
	
	@Column(name = "monto_total")
	private Double montoTotal;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
}
