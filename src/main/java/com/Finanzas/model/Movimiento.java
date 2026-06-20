
package com.Finanzas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "tbl_movimiento")
@AllArgsConstructor
@NoArgsConstructor
public class Movimiento {

	@Id
	@Column(name = "id_movimiento")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idMovimiento;
	
	@Column(name = "fecha")
	private LocalDate fecha;
	
	@Column(name = "descripcion")
	private String descripcion;
	
	@Column(name = "monto")
	private BigDecimal monto;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria")
	private Categoria categoria;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
}
