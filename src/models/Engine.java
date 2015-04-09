package models;

import annotations.Column;
import annotations.Entity;

@Entity(table="engine", primaryKey="id")
public class Engine {
	
	int id;
	
	@Column(name="horse_power", nullable=true)
	int horsePower;

	
	public Engine() {
		super();
	}
	
	public Engine(int horsePower) {
		super();
		this.horsePower = horsePower;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHorsePower() {
		return horsePower;
	}

	public void setHorsePower(int horsePower) {
		this.horsePower = horsePower;
	}

	@Override
	public String toString() {
		return "Engine [id=" + id + ", horsePower=" + horsePower + "]";
	}
	
	

}
