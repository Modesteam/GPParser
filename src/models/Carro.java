package models;

import libraries.Entity;

@Entity(table="carro", primaryKey="id")
public class Carro {

	int id;
	int rodas;
	String modelo;
	
	public Carro() {
		super();
	}
	
	public Carro(int id) {
		super();
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRodas() {
		return rodas;
	}
	public void setRodas(int rodas) {
		this.rodas = rodas;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@Override
	public String toString() {
		return "Carro [id=" + id + ", rodas=" + rodas + ", modelo=" + modelo
				+ "]";
	}
	
}
