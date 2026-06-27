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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	public String getCompletadaDescripcion() {
		return Boolean.TRUE.equals(completada) ? "Completada" : "Incompleta";
	}
	
	public Boolean getFechaLimitePasada() {
		if (fechaObjetivo == null) {
			return false;
		}
		
		return LocalDate.now().isAfter(fechaObjetivo);
	}
	
	public String getEstadoDescripcion() {
		return Boolean.TRUE.equals(completada) ? "Completada" : "Incompleta";
	}
	
	public String getBadgeEstado() {
		return Boolean.TRUE.equals(completada) ? "bg-success" : "bg-secondary";
	}
	
	public Double getMontoRestante() {
		Double objetivo = montoObjetivo == null ? 0.0 : montoObjetivo;
		Double actual = montoActual == null ? 0.0 : montoActual;
		Double restante = objetivo - actual;
		
		return restante < 0 ? 0.0 : restante;
	}
	
	public Double getPorcentajeAvance() {
		if (montoObjetivo == null || montoObjetivo <= 0) {
			return 0.0;
		}
		
		Double actual = montoActual == null ? 0.0 : montoActual;
		Double porcentaje = (actual * 100) / montoObjetivo;
		
		return porcentaje > 100 ? 100.0 : porcentaje;
	}
}
