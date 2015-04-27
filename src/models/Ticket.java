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
	@HasOne(entity=HighwayStretch.class, reference ="idHighwayStretch", belongs=true),
	@HasOne(entity=Model.class, reference="idModel", belongs=true)
})
public class Ticket {
	
	@Column(name="_id", nullable=false)
	private int id;
	private Date date;
	@Column(name="is_velocity", nullable=true)
	private boolean isVelocity;
	private Double limit;
	private Double measure;
	@Column(name="id_model", nullable=false)
	private int idModel;
	@Column(name="id_highway_stretch", nullable=false)
	private int idHighwayStretch;
	
	public Ticket() {
		super();
	}

	public Ticket(int id) {
		super();
		this.id = id;
	}
	
	public Ticket(Date date, boolean isVelocity, Double limit, Double measure,
			int idModel, int idHighwayStretch) {
		super();
		this.date = date;
		this.isVelocity = isVelocity;
		this.limit = limit;
		this.measure = measure;
		this.idModel = idModel;
		this.idHighwayStretch = idHighwayStretch;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getIdModel() {
		return idModel;
	}
	public void setIdModel(int idModel) {
		this.idModel = idModel;
	}
	public int getIdHighwayStretch() {
		return idHighwayStretch;
	}
	public void setIdHighwayStretch(int idHighwayStretch) {
		this.idHighwayStretch = idHighwayStretch;
	}
	public boolean isVelocity() {
		return isVelocity;
	}
	public void setVelocity(boolean isVelocity) {
		this.isVelocity = isVelocity;
	}
	public Double getLimit() {
		return limit;
	}
	public void setLimit(Double limit) {
		this.limit = limit;
	}
	public Double getMeasure() {
		return measure;
	}
	public void setMeasure(Double measure) {
		this.measure = measure;
	}

	public int save() throws ClassNotFoundException, SQLException, NotNullableException{
		GenericPersistence gP = new GenericPersistence();
		int result = gP.insertBean(this);
		this.setId(result);
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
		return "Ticket [id=" + id + ", date=" + date + ", isVelocity="
				+ isVelocity + ", limit=" + limit + ", measure=" + measure
				+ ", idModel=" + idModel + ", idHighwayStretch="
				+ idHighwayStretch + "]";
	}

}
