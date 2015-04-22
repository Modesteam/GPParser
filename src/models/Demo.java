package models;

import helpers.GenericPersistence;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import libraries.NotNullableException;

public class Demo {

	public static void main(String[] args) {
		
		try {
			/*
			State df = new State("DF");
			
			df.save();
			
			City gama = new City("001", "Gama", df.getId());
			
			gama.save();
			
			HighwayStretch highwayStretch = new HighwayStretch("040", 10, gama.getId());
			
			highwayStretch.save();
			
			Infraction infraction = new Infraction("Some infraction", "11", 2000.0, 7);
			
			infraction.save();
			
			Type type = new Type("Utilitary", "Util");
			
			type.save();
			
			Brand brand = new Brand("Fiat");
			
			brand.save();
			
			Model model = new Model("Uno", type.getId(), brand.getId());
			
			model.save();
			
			Vehicle vehicle = new Vehicle(true, model.getId());
			
			vehicle.save();
			
			Ticket ticket = new Ticket(1900.0, Calendar.getInstance().getTime(), vehicle.getId(), infraction.getId(), highwayStretch.getId());
			
			ticket.save();
			
			System.out.println(State.last());
			System.out.println(City.last());
			System.out.println(HighwayStretch.last());
			System.out.println(Infraction.last());
			System.out.println(Type.last());
			System.out.println(Brand.last());
			System.out.println(Model.last());
			System.out.println(Vehicle.last());
			System.out.println(Ticket.last());
			*/
			
			List<String> lines = Files.readAllLines(Paths.get("jars/files/tbl_veiculo_marca.csv") , Charset.forName("ISO-8859-1"));
			lines.remove(0);
			HashMap<String, Brand> brands = saveBrands(lines, true);
			
			lines = Files.readAllLines(Paths.get("jars/files/tbl_veiculo_especie.csv") , Charset.forName("ISO-8859-1"));
			lines.remove(0);
			HashMap<String, Type> types = saveTypes(lines, true);
			
			//lines = Files.readAllLines(Paths.get("jars/files/tbl_infracao.csv") , Charset.forName("ISO-8859-1"));
			//lines.remove(0);
			//HashMap<String, Infraction> infractions = saveInfractions(lines, true);
			
			lines = Files.readAllLines(Paths.get("jars/files/cities.csv"), Charset.forName("ISO-8859-1"));
			lines.remove(0);
			HashMap<String, City> cities = saveCities(lines, true);
			
			
		} catch (ClassNotFoundException | SQLException | NotNullableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	//TODO finish
	/*public static HashMap<String, Infraction> saveInfractions(List<String> lines, boolean commit) throws ClassNotFoundException, SQLException, NotNullableException{
		HashMap<String, Infraction> types = new HashMap<String, Infraction>();
		for (String line : lines) {
			String key = line.split(":")[0];
			String  = line.split(";")[1];
			Infraction infraction = new Infraction();
			if(commit){
				infraction.save();
			}
			types.put(key, infraction);
		}
		return types;
	}*/
	
	public static HashMap<String, City> saveCities(List<String> lines, boolean commit) throws ClassNotFoundException, SQLException, NotNullableException{
		HashMap<String, City> cities = new HashMap<String, City>();
		State lastState = new State();
		GenericPersistence gP = new GenericPersistence();
		gP.openConnection();
		Connection conn = gP.getConnection();
		for (String line : lines){
			String state = line.split(";")[0];
			String name = line.split(";")[1];
			String key = line.split(";")[2].substring(0, line.split(";")[2].length());
			
			if(!state.equalsIgnoreCase(lastState.getName())){
				lastState = new State(state);
				gP.insertBean(lastState, conn);
				lastState = (State)gP.firstOrLastBean(lastState, true, conn);
			}
			
			City city = new City(key, name, lastState.getId());
			
			gP.insertBean(city, conn);
			city = (City) gP.firstOrLastBean(city, true, conn);
			
			cities.put(key, city);
		}
		gP.closeConnection();
		return cities;
	}
	
	public static HashMap<String, Brand> saveBrands(List<String> lines, boolean commit) throws ClassNotFoundException, SQLException, NotNullableException{
		HashMap<String, Brand> brands = new HashMap<String, Brand>();
		for (String line : lines) {
			String key = line.split(":")[0];
			String data = line.split(";")[1];
			Brand brand = new Brand(data);
			if(commit){
				brand.save();
			}
			brands.put(key, brand);
		}
		return brands;
	}
	
	public static HashMap<String, Type> saveTypes(List<String> lines, boolean commit) throws ClassNotFoundException, SQLException, NotNullableException{
		HashMap<String, Type> types = new HashMap<String, Type>();
		for (String line : lines) {
			String key = line.split(":")[0];
			String name = line.split(";")[1];
			Type type = new Type("Description", name);
			if(commit){
				type.save();
			}
			types.put(key, type);
		}
		return types;
	}

}
