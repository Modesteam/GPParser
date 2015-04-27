package models;

import helpers.Condition;
import helpers.GenericPersistence;

import java.sql.SQLException;
import java.util.ArrayList;

import libraries.NotNullableException;
import annotations.Column;
import annotations.Entity;
import annotations.HasMany;
import annotations.HasOne;
import annotations.ManyRelations;
import annotations.OneRelations;

@Entity(table="highway_stretch", primaryKey="id")
@OneRelations({
	@HasOne(entity=City.class, reference="idCity", belongs=true)
})
@ManyRelations({
	@HasMany(entity=Ticket.class, foreignKey="idHighwayStretch")
})
public class HighwayStretch {

	@Column(name="_id", nullable=false)
	private int id;
	
	private String number;
	
	private int kilometer;
	
	@Column(name="id_city", nullable = false)
	private int idCity;

	public int getId() {
		return id;
	}

	public HighwayStretch() {
		super();
	}
	
	public HighwayStretch(int id) {
		super();
		this.id = id;
	}
	
	public HighwayStretch(String number, int kilometer, int idCity) {
		super();
		this.number = number;
		this.kilometer = kilometer;
		this.idCity = idCity;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getKilometer() {
		return kilometer;
	}

	public void setKilometer(int kilometer) {
		this.kilometer = kilometer;
	}

	public int getIdCity() {
		return idCity;
	}

	public void setIdCity(int idCity) {
		this.idCity = idCity;
	}
	
	public int save() throws ClassNotFoundException, SQLException, NotNullableException{
		GenericPersistence gP = new GenericPersistence();
		int result = gP.insertBean(this);
		this.setId(result);
		return result;
	}
	
	public static HighwayStretch get(int id) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (HighwayStretch) gP.selectBean(new HighwayStretch(id));
	}
	
	public static ArrayList<HighwayStretch> getAll() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<HighwayStretch> stretches = new ArrayList<HighwayStretch>();
		for (Object bean : gP.selectAllBeans(new HighwayStretch())) {
			stretches.add((HighwayStretch)bean);
		}
		return stretches;
	}
	
	public static int count() throws ClassNotFoundException, SQLException {
		GenericPersistence gDB = new GenericPersistence();
		return gDB.countBean(new HighwayStretch());
	}
	
	public static HighwayStretch first() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (HighwayStretch) gP.firstOrLastBean(new HighwayStretch() , false);
	}
	
	public static HighwayStretch last() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (HighwayStretch) gP.firstOrLastBean(new HighwayStretch() , true);
	}
	
	public static ArrayList<HighwayStretch> getWhere(Condition condition) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<HighwayStretch> stretches = new ArrayList<HighwayStretch>();
		for (Object bean : gP.selectWhere(new HighwayStretch(), condition)) {
			stretches.add((HighwayStretch)bean);
		}
		return stretches;
	}

	public boolean delete() throws ClassNotFoundException, SQLException {
		GenericPersistence gP = new GenericPersistence();
		return gP.deleteBean(this);
	}

	public City getCity() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (City) gP.selectOne(this, new City());
	}
	
	public ArrayList<Ticket> getTickets() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<Ticket> beans = new ArrayList<Ticket>();
		for (Object bean : gP.selectMany(this, new Ticket())) {
			beans.add((Ticket)bean);
		}
		return beans;
	}

	@Override
	public String toString() {
		return "HighwayStretch [id=" + id + ", number=" + number
				+ ", kilometer=" + kilometer + ", idCity=" + idCity + "]";
	}
}
