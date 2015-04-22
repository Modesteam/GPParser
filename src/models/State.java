package models;

import java.sql.SQLException;
import java.util.ArrayList;

import libraries.NotNullableException;
import helpers.Condition;
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
	
	public State(int id) {
		super();
		this.id = id;
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
	
	public static State get(int id) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (State) gP.selectBean(new State(id));
	}
	
	public static ArrayList<State> getAll() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<State> states = new ArrayList<State>();
		for (Object bean : gP.selectAllBeans(new State())) {
			states.add((State)bean);
		}
		return states;
	}
	
	public static int count() throws ClassNotFoundException, SQLException {
		GenericPersistence gDB = new GenericPersistence();
		return gDB.countBean(new State());
	}
	
	public static State first() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (State) gP.firstOrLastBean(new State() , false);
	}
	
	public static State last() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (State) gP.firstOrLastBean(new State() , true);
	}
	
	public static ArrayList<State> getWhere(Condition condition) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<State> states = new ArrayList<State>();
		for (Object bean : gP.selectWhere(new State(), condition)) {
			states.add((State)bean);
		}
		return states;
	}
	
	public boolean delete() throws ClassNotFoundException, SQLException {
		GenericPersistence gP = new GenericPersistence();
		return gP.deleteBean(this);
	}
	
	public ArrayList<City> getCities() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<City> beans = new ArrayList<City>();
		for (Object bean : gP.selectMany(this, new City())) {
			beans.add((City)bean);
		}
		return beans;
	}

	@Override
	public String toString() {
		return "State [id=" + id + ", name=" + name + "]";
	}
	
	
	
}
