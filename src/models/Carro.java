package models;

import libraries.BelongsTo;
import libraries.Column;
import libraries.Entity;
import libraries.HasOne;

@Entity(table="carro", primaryKey="id")
@BelongsTo(entity=Dummy.class, reference="idDummy")
@HasOne(entity=Engine.class, reference="idEngine")
public class Carro {

	int id;
	int rodas;
	@Column(name="id_dummy", nullable=false)
	int idDummy;
	@Column(name="id_engine", nullable=true)
	int idEngine;

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
	public int getIdDummy() {
		return idDummy;
	}
	public void setIdDummy(int idDummy) {
		this.idDummy = idDummy;
	}
	public int getIdEngine() {
		return idEngine;
	}
	public void setIdEngine(int idEngine) {
		this.idEngine = idEngine;
	}

	@Override
	public String toString() {
		return "Carro [id=" + id + ", rodas=" + rodas + ", idDummy=" + idDummy
				+ ", idEngine=" + idEngine + ", modelo=" + modelo + "]";
	}
	
}
