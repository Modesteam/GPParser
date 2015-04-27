package models;

import helpers.Condition;
import helpers.GenericPersistence;

import java.sql.SQLException;
import java.util.ArrayList;

import libraries.NotNullableException;
import annotations.Column;
import annotations.Entity;
import annotations.HasMany;
import annotations.ManyRelations;

@Entity(table="infraction", primaryKey="id")
@ManyRelations({@HasMany(entity=Ticket.class, foreignKey="idInfraction")})
public class Infraction {
	
	@Column(name="_id", nullable=false)
	private int id;
	private String description;
	private String framing;
	private Double value;
	@Column(name="cnh_points", nullable=true)
	private int cnhPoints;
	
	
	public Infraction(){
		super();
	}
	
	public Infraction(int id){
		super();
		this.id = id;
	}
	
	public Infraction(String description, String framing, Double value,
			int cnhPoints) {
		super();
		this.description = description;
		this.framing = framing;
		this.value = value;
		this.cnhPoints = cnhPoints;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFraming() {
		return framing;
	}
	public void setFraming(String framing) {
		this.framing = framing;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public int getCnhPoints() {
		return cnhPoints;
	}
	public void setCnhPoints(int cnhPoints) {
		this.cnhPoints = cnhPoints;
	}
	
	public int save() throws ClassNotFoundException, SQLException, NotNullableException{
		GenericPersistence gP = new GenericPersistence();
		int result = gP.insertBean(this);
		this.setId(result);
		return result;
	}
	
	public static Infraction get(int id) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Infraction) gP.selectBean(new Infraction(id));
	}
	
	public static ArrayList<Infraction> getAll() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<Infraction> infractions = new ArrayList<Infraction>();
		for (Object bean : gP.selectAllBeans(new Infraction())) {
			infractions.add((Infraction)bean);
		}
		return infractions;
	}
	
	public static int count() throws ClassNotFoundException, SQLException {
		GenericPersistence gDB = new GenericPersistence();
		return gDB.countBean(new Infraction());
	}
	
	public static Infraction first() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Infraction) gP.firstOrLastBean(new Infraction() , false);
	}
	
	public static Infraction last() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Infraction) gP.firstOrLastBean(new Infraction() , true);
	}
	
	public static ArrayList<Infraction> getWhere(Condition condition) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<Infraction> infractions = new ArrayList<Infraction>();
		for (Object bean : gP.selectWhere(new Infraction(), condition)) {
			infractions.add((Infraction)bean);
		}
		return infractions;
	}
	
	public boolean delete() throws ClassNotFoundException, SQLException {
		GenericPersistence gP = new GenericPersistence();
		return gP.deleteBean(this);
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
		return "Infraction [id=" + id + ", description=" + description
				+ ", framing=" + framing + ", value=" + value + ", cnhPoints="
				+ cnhPoints + "]";
	}
}
