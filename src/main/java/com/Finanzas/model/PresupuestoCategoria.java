package com.Finanzas.model;

import java.math.BigDecimal;

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
@Table(name = "tbl_presupuesto_categoria")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PresupuestoCategoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_presupuesto_categoria")
	private Integer idPresupuestoCategoria;
	
	@Column(name = "monto_asignado")
	private BigDecimal montoAsignado;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_presupuesto")
	private Presupuesto presupuesto;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria")
	private Categoria categoria;
}
