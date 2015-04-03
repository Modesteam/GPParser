package models;

import libraries.BelongsTo;
import libraries.Column;
import libraries.Entity;

@Entity(table="engine", primaryKey="id")
public class Engine {
	
	int id;
	
	@Column(name="horse_power", nullable=true)
	String horsePower;
	
	

}
