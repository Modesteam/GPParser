package models;

import helpers.GenericPersistence;

import java.sql.SQLException;

import libraries.NotNullableException;
import annotations.Column;
import annotations.Entity;
import annotations.HasOne;
import annotations.OneRelations;

@Entity(table="city", primaryKey="id")
@OneRelations({
	@HasOne(entity=State.class, reference="idState", belongs=true)
})
public class City {

	@Column(name="_id", nullable=false)
	private int id;
	
	private String code;
	private String name;
	
	@Column(name="id_state", nullable=false)
	private int idState;
	
	public City(){
		super();
	}
	
	public City(String code, String name, int idState) {
		super();
		this.code = code;
		this.name = name;
		this.idState = idState;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdState() {
		return idState;
	}

	public void setIdState(int idState) {
		this.idState = idState;
	}
	
	public boolean save() throws ClassNotFoundException, SQLException, NotNullableException{
		GenericPersistence gP = new GenericPersistence();
		boolean result = gP.insertBean(this);
		this.setId(City.last().getId());
		return result;
	}
	
	public static City last() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (City) gP.firstOrLastBean(new City() , true);
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", code=" + code + ", name=" + name
				+ ", idState=" + idState + "]";
	}
	
}
