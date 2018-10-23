package ar.edu.iua.ingweb3.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
//@Table(name="productos")
public class Producto {

	public Producto() {
	}

	public Producto(int id, String descripcion, double precio, boolean enStock, Date vencimiento) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.precio = precio;
		this.enStock = enStock;
		this.vencimiento = vencimiento;
	}

	@Id
	@GeneratedValue
	private int id;

	//@Column(name="la_descripcion")
	private String descripcion;
	private double precio;
	private boolean enStock;
	private Date vencimiento;
	
	@ManyToOne
    @JoinColumn(name="id_rubro", nullable=true)
	//@JsonIgnoreProperties("productos")
	private Rubro rubro;

	public Rubro getRubro() {
		return rubro;
	}

	public void setRubro(Rubro rubro) {
		this.rubro = rubro;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public boolean isEnStock() {
		return enStock;
	}

	public void setEnStock(boolean enStock) {
		this.enStock = enStock;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

}
