package models;

import helpers.Condition;
import helpers.GenericPersistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import libraries.NotNullableException;
import annotations.Column;
import annotations.Entity;
import annotations.HasOne;
import annotations.OneRelations;

@Entity(table="ticket", primaryKey="id")
@OneRelations({
	@HasOne(entity=Infraction.class, reference="idInfraction", belongs=true),
	@HasOne(entity=HighwayStretch.class, reference ="idHighwayStretch", belongs=true),
	@HasOne(entity=Vehicle.class, reference="idVehicle", belongs=true)
})
public class Ticket {
	
	@Column(name="_id", nullable=false)
	private int id;
	@Column(name="paid_value", nullable=true)
	private Double paidValue;
	private Date date;
	@Column(name="id_vehicle", nullable=false)
	private int idVehicle;
	@Column(name="id_infraction", nullable=false)
	private int idInfraction;
	@Column(name="id_highway_stretch", nullable=false)
	private int idHighwayStretch;
	
	public Ticket() {
		super();
	}

	public Ticket(int id) {
		super();
		this.id = id;
	}

	public Ticket(Double paidValue, Date date, int idVehicle, int idInfraction,
			int idHighwayStretch) {
		super();
		this.paidValue = paidValue;
		this.date = date;
		this.idVehicle = idVehicle;
		this.idInfraction = idInfraction;
		this.idHighwayStretch = idHighwayStretch;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Double getPaidValue() {
		return paidValue;
	}
	public void setPaidValue(Double paidValue) {
		this.paidValue = paidValue;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getIdVehicle() {
		return idVehicle;
	}
	public void setIdVehicle(int idVehicle) {
		this.idVehicle = idVehicle;
	}
	public int getIdInfraction() {
		return idInfraction;
	}
	public void setIdInfraction(int idInfraction) {
		this.idInfraction = idInfraction;
	}
	public int getIdHighwayStretch() {
		return idHighwayStretch;
	}
	public void setIdHighwayStretch(int idHighwayStretch) {
		this.idHighwayStretch = idHighwayStretch;
	}
	
	public boolean save() throws ClassNotFoundException, SQLException, NotNullableException{
		GenericPersistence gP = new GenericPersistence();
		boolean result = gP.insertBean(this);
		this.setId(Ticket.last().getId());
		return result;
	}
	
	public static Ticket get(int id) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Ticket) gP.selectBean(new Ticket(id));
	}
	
	public static ArrayList<Ticket> getAll() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		for (Object bean : gP.selectAllBeans(new Ticket())) {
			tickets.add((Ticket)bean);
		}
		return tickets;
	}
	
	public static int count() throws ClassNotFoundException, SQLException {
		GenericPersistence gDB = new GenericPersistence();
		return gDB.countBean(new Ticket());
	}
	
	public static Ticket first() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Ticket) gP.firstOrLastBean(new Ticket() , false);
	}
	
	public static Ticket last() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Ticket) gP.firstOrLastBean(new Ticket() , true);
	}
	
	public static ArrayList<Ticket> getWhere(Condition condition) throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		for (Object bean : gP.selectWhere(new Ticket(), condition)) {
			tickets.add((Ticket)bean);
		}
		return tickets;
	}

	public boolean delete() throws ClassNotFoundException, SQLException {
		GenericPersistence gP = new GenericPersistence();
		return gP.deleteBean(this);
	}

	public Infraction getInfraction() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (Infraction) gP.selectOne(this, new Infraction());
	}
	
	public HighwayStretch getHighwayStretch() throws ClassNotFoundException, SQLException{
		GenericPersistence gP = new GenericPersistence();
		return (HighwayStretch) gP.selectOne(this, new HighwayStretch());
	}

	@Override
	public String toString() {
		return "Ticket [id=" + id + ", paidValue=" + paidValue + ", date="
				+ date + ", idVehicle=" + idVehicle + ", idInfraction="
				+ idInfraction + ", idHighwayStretch=" + idHighwayStretch + "]";
	}

}
