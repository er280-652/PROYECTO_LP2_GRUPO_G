package com.Finanzas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "tbl_categoria")
public class Categoria {

	@Id
	@Column(name = "id_categoria")
	private Integer idCategoria;
	
	@Column(name = "descripcion")
	private String descripcion;
	
	@Column(name = "tipo")
	private String tipo;
}
