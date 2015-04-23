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

import javax.jws.WebParam.Mode;

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

			System.out.println("Read start! Brand");
			List<String> lines = Files.readAllLines(Paths.get("jars/files/tbl_veiculo_marca.csv") , Charset.forName("ISO-8859-1"));
			lines.remove(0);
			System.out.println("Read done!");
			HashMap<String, Brand> brands = saveBrands(lines, true);
			
			System.out.println("Read start! Type");
			lines = Files.readAllLines(Paths.get("jars/files/tbl_veiculo_especie.csv") , Charset.forName("ISO-8859-1"));
			lines.remove(0);
			System.out.println("Read done!");
			HashMap<String, Type> types = saveTypes(lines, true);
			
			System.out.println("Read start! Infraction");
			lines = Files.readAllLines(Paths.get("jars/files/tbl_infracao.csv") , Charset.forName("ISO-8859-1"));
			lines.remove(0);
			System.out.println("Read done!");
			HashMap<String, Infraction> infractions = saveInfractions(lines, true);
			
			System.out.println("Read start! City");
			lines = Files.readAllLines(Paths.get("jars/files/cities.csv"), Charset.forName("ISO-8859-1"));
			lines.remove(0);
			System.out.println("Read done!");
			//HashMap<String, City> cities = saveCities(lines, true);
			
			
		} catch (ClassNotFoundException | SQLException | NotNullableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public static void saveEach(String line, boolean commit, HashMap<String, City> cities, HashMap<String, Brand> brands, HashMap<String, Type> types, Connection conn) throws ClassNotFoundException, SQLException, NotNullableException{
		String[] data = line.split(";");
		City city = (City)cities.get(data[9].substring(0, data[9].length()-1));
		HighwayStretch highwayStretch = new HighwayStretch(
				data[6], Integer.parseInt(data[8]), city.getId());
		highwayStretch.save();
		Brand brand;
		if(data[30].trim().equalsIgnoreCase("")){
			brand = brands.get("?");
		} else {
			brand = brands.get(data[30]);
		}
		Type type;
		if(data[31].trim().equalsIgnoreCase("")){
			type = types.get("?");
		} else {
			type = types.get(data[31]);
		}
		String modelName;
		if(data[23].trim().equalsIgnoreCase("")){
			modelName = "Não Informado";
		} else {
			modelName = data[23].trim();
		}
		Model model = new Model(modelName, brand.getId(), type.getId());
		model.save();
		boolean national;
		if(data[34].equalsIgnoreCase("N")){
			national = true;
		} else {
			national = false;
		}
		Vehicle vehicle = new Vehicle(national, model.getId());
		vehicle.save();
		
		
	}
	
	public static HashMap<String, Infraction> saveInfractions(List<String> lines, boolean commit) throws ClassNotFoundException, SQLException, NotNullableException{
		HashMap<String, Infraction> infractions = new HashMap<String, Infraction>();
		GenericPersistence gP = new GenericPersistence();
		gP.openConnection();
		Connection conn = gP.getConnection();
		ArrayList<Object> objects = new ArrayList<Object>();
		for (String line : lines) {
			line = line.replaceAll("\"", "");
			String[] data = line.split(";");
			String key = data[0];
			String description = data[1].trim();
			String framing = data[3];
			Double value;
			if(data[4].equalsIgnoreCase("")){
				value = 0.0;
			} else {
				value = Double.parseDouble(data[4]);
			}
			int cnh_points = Integer.parseInt(data[5]);
			Infraction infraction = new Infraction(description,framing,value,cnh_points);
			objects.add(infraction);
			infractions.put(key, infraction);
			System.out.println("a"+key);
			
		}
		if(commit){
			gP.insertAll(objects, conn);
		}
		gP.closeConnection();
		return infractions;
	}
	
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
			System.out.println(key);
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
			String key = line.split(";")[0];
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
			String key = line.split(";")[0];
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
