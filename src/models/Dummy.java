package models;

import annotations.Column;
import annotations.Entity;
import annotations.HasMany;
import annotations.Ignore;
import annotations.ManyRelations;
import annotations.OrderBy;

@Entity(table="dummy", primaryKey="id")
@ManyRelations({
	@HasMany(entity=Carro.class, foreignKey="idDummy")
	})
@OrderBy(field="name")
public class Dummy{

	@Column(name="_id", nullable=false)
	private int id;

	@Ignore
	@Column(name="name", nullable=true)
	private String name;

	int test;

	public Dummy() {
		super();
	}
	public Dummy(int id) {
		super();
		this.id = id;
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
	public int getTest() {
		return test;
	}
	public void setTest(int test) {
		this.test = test;
	}
	@Override
	public String toString() {
		return "Dummy [id=" + id + ", name=" + name + ", test=" + test + "]";
	}

}
