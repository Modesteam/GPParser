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


@Entity(table="vehicle", primaryKey="id")
@OneRelations({@HasOne(entity=Model.class, reference="idModel", belongs=true)})
@ManyRelations({@HasMany(entity=Ticket.class, foreignKey="idVehicle")})
public class Vehicle {
	
	@Column(name="_id", nullable=false)
	private int id;
	@Column(name="is_national", nullable=true)
	private boolean isNational;
	@Column(name="id_model", nullable=false)
	private int idModel;
	
	public Vehicle() {
		super();
	}

	public Vehicle(int id) {
		super();
		this.id = id;
	}

	public Vehicle(boolean isNational, int idModel) {
		super();
		this.isNational = isNational;
		this.idModel = idModel;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isNational() {
		return isNational;
	}
	public void setNational(boolean isNational) {
		this.isNational = isNational;
	}
	public int getIdModel() {
		return idModel;
	}
	public void setIdModel(int idModel) {
		this.idModel = idModel;
	}
	
	public boolean save() throws ClassNotFoundException, SQLException, NotNullableException{
		GenericPersistence gP = new GenericPersistence();
		boolean result = gP.insertBean(this);
		this.setId(Vehicle.last().getId());
		return result;
	}
	
	public static Vehicle get(int id) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Vehicle) gP.selectBean(new Vehicle(id));
	}
	
	public static ArrayList<Vehicle> getAll() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
		for (Object bean : gP.selectAllBeans(new Vehicle())) {
			vehicles.add((Vehicle)bean);
		}
		return vehicles;
	}
	
	public static int count() throws ClassNotFoundException, SQLException {
		GenericPersistence gDB = new GenericPersistence();
		return gDB.countBean(new Vehicle());
	}
	
	public static Vehicle first() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Vehicle) gP.firstOrLastBean(new Vehicle() , false);
	}
	
	public static Vehicle last() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Vehicle) gP.firstOrLastBean(new Vehicle() , true);
	}
	
	public static ArrayList<Vehicle> getWhere(Condition condition) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
		for (Object bean : gP.selectWhere(new Vehicle(), condition)) {
			vehicles.add((Vehicle)bean);
		}
		return vehicles;
	}
	
	public boolean delete() throws ClassNotFoundException, SQLException {
		GenericPersistence gP = new GenericPersistence();
		return gP.deleteBean(this);
	}
	
	public Model getModel() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Model) gP.selectOne(this, new Model());
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
		return "Vehicle [id=" + id + ", isNational=" + isNational
				+ ", idModel=" + idModel + "]";
	}
}
