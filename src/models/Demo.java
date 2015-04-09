package models;

import java.sql.SQLException;

import libraries.NotNullableException;

public class Demo {

	public static void main(String[] args) {
		
		State df = new State("DF");
		
		try {
			
			df.save();
			
			City gama = new City("001", "Gama", df.getId());
			
			gama.save();
			
			System.out.println(State.last());
			
			System.out.println(City.last());
			
		} catch (ClassNotFoundException | SQLException | NotNullableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
