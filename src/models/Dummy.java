package models;

import java.util.ArrayList;

import libraries.Column;
import libraries.Entity;
import libraries.HasMany;
import libraries.Ignore;

@Entity(table="dummy", primaryKey="id")
@HasMany(entity=Dummy.class, foreignKey="id_dummy")
public class Dummy{

	@Column(name="_id")
	private int id;

	@Column(name="name")
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
