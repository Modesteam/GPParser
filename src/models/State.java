package models;

import java.sql.SQLException;
import libraries.NotNullableException;
import helpers.GenericPersistence;
import annotations.Column;
import annotations.Entity;
import annotations.HasMany;
import annotations.ManyRelations;

@Entity(table="state", primaryKey="id")
@ManyRelations({
	@HasMany(entity=City.class, foreignKey="idState")
})
public class State {

	@Column(name="_id", nullable=false)
	private int id;
	private String name;
	
	public State() {
		super();
	}
	
	public State(String name) {
		super();
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean save() throws ClassNotFoundException, SQLException, NotNullableException{
		GenericPersistence gP = new GenericPersistence();
		boolean result = gP.insertBean(this);
		this.setId(State.last().getId());
		return result;
	}
	
	public static State last() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (State) gP.firstOrLastBean(new State() , true);
	}

	@Override
	public String toString() {
		return "State [id=" + id + ", name=" + name + "]";
	}
	
	
	
}
