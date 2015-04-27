package models;

import helpers.Condition;
import helpers.GenericPersistence;
import helpers.Joiner;
import helpers.Operator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.jws.WebParam.Mode;

import org.apache.commons.lang3.StringEscapeUtils;

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
			List<String> lines;
			System.out.println("Read start! Brand");
			//List<String> lines = Files.readAllLines(Paths.get("jars/files/tbl_veiculo_marca.csv") , Charset.forName("ISO-8859-1"));
			//lines.remove(0);
			lines = Files.readAllLines(Paths.get("jars/files/brand_relation.csv") , Charset.forName("ISO-8859-1"));
			HashMap<String, Brand> brands = new HashMap<String, Brand>();
			for (String string : lines) {
				String[] data = string.split(";");
				brands.put(data[0], Brand.get(Integer.parseInt(data[1])));
			}
			System.out.println("Read done!");
			//HashMap<String, Brand> brands = saveBrands(lines, true);
			
			System.out.println("Read start! Type");
			//lines = Files.readAllLines(Paths.get("jars/files/tbl_veiculo_especie.csv") , Charset.forName("ISO-8859-1"));
			//lines.remove(0);
			lines = Files.readAllLines(Paths.get("jars/files/type_relation.csv") , Charset.forName("ISO-8859-1"));
			HashMap<String, Type> types = new HashMap<String, Type>();
			for (String string : lines) {
				String[] data = string.split(";");
				types.put(data[0], Type.get(Integer.parseInt(data[1])));
			}
			System.out.println("Read done!");
			//HashMap<String, Type> types = saveTypes(lines, true);
			
			System.out.println("ola'asd".replaceAll("'", "\\\\'"));
			
			DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get("jars/files/ticket/"));
			Iterator<Path> iterator = paths.iterator();
			
			while(iterator.hasNext()){
				Path path = iterator.next();
				System.out.println(path.toFile().getName());
				System.out.println("Start: "+Calendar.getInstance().getTime());
				BufferedReader bufferedReader = Files.newBufferedReader(path, Charset.forName("ISO-8859-1"));
				String line = bufferedReader.readLine();
				line = bufferedReader.readLine();
				int skip = 0;
				for (int i = 0; i < skip; i++) {
					line = bufferedReader.readLine();
				}
				GenericPersistence gP = new GenericPersistence();
				gP.openConnection();
				gP.beginTransaction();
				Connection conn = gP.getConnection();
				
				int i = 0+skip;
				while(bufferedReader.ready()){
					i++;
					saveEach(line, true, brands, types, conn, gP);
					line = bufferedReader.readLine();
					if(i % 1000 == 0){
						System.out.println(i);
					}
					if(i % 50000 == 0){
						if(!conn.isClosed()){
							conn.commit();
							conn.close();
						}
						gP.closeConnection();
						gP.openConnection();
						gP.beginTransaction();
						conn = gP.getConnection();
					}
				}
				if(!conn.isClosed()){
					conn.commit();
					conn.close();
				}
				Files.copy(Paths.get("jars/database.sqlite3.db"), Paths.get("jars/database"+path.toFile().getName()));
				System.out.println("End: "+Calendar.getInstance().getTime());
			}
			
			Ticket.last();
			//lines = Files.readAllLines(Paths.get("jars/files/cities.csv"), Charset.forName("ISO-8859-1"));
			//lines.remove(0);
			//System.out.println("Read done!");
			//HashMap<String, City> cities = saveCities(lines, true);
			
//			GenericPersistence gP = new GenericPersistence();
//			gP.openConnection();
//			City city = (City) gP.selectWhere(new City(), new Condition(new City(), "code", Operator.EQUAL, "0011".substring(0, "0011".length()-1)), gP.getConnection()).get(0);
//			gP.closeConnection();
//			System.out.println(city);
		}catch (SQLException s){
			
			s.printStackTrace();
			System.out.println(s.getSQLState() + s.getMessage());
		} catch (ClassNotFoundException | IOException | NotNullableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public static int saveEach(String line, boolean commit, HashMap<String, Brand> brands, HashMap<String, Type> types, Connection conn, GenericPersistence gP) throws ClassNotFoundException, SQLException, NotNullableException{
		String[] data = line.split(";");
		City city;
		ArrayList<Object> cities = gP.selectWhere(new City(), new Condition(new City(), "code", Operator.EQUAL, data[9].substring(0, data[9].length()-1)), conn);
		
		HighwayStretch highwayStretch = new HighwayStretch();
		Condition condition = new Condition( 
				new Condition(highwayStretch, "number", Operator.EQUAL, data[6].trim()), Joiner.AND, 
				new Condition(highwayStretch, "kilometer", Operator.EQUAL, Integer.parseInt(data[8])));
		
		ArrayList<Object> highways = gP.selectWhere(highwayStretch, condition, conn);
		
		if(cities.size() == 0 && highways.size() != 0){
			
			city = (City) gP.selectOne(((HighwayStretch)highways.get(0)), new City());
			
		} else if(cities.size() != 0){
			city = (City) cities.get(0);
		} else {
			System.out.println("bad");
			return 1;
		}
		if(highways.size() == 0){
			highwayStretch = new HighwayStretch(
					data[6], Integer.parseInt(data[8]), city.getId());
			highwayStretch.setId(gP.insertBean(highwayStretch, conn));
		} else {
			highwayStretch = (HighwayStretch)highways.get(0);
		}
		
		Brand brand;
		if(!data[30].trim().equalsIgnoreCase("") || !data[30].trim().equalsIgnoreCase("0")){
			brand = brands.get(data[30]);
		} else {
			brand = brands.get("75");
		}
		if(brand == null){
			brand = brands.get("75");
		}
		Type type;
		if(!data[31].trim().equalsIgnoreCase("") || !data[30].trim().equalsIgnoreCase("0")){
			type = types.get(data[31]);
		} else {
			type = types.get("10");
		}
		if(type == null){
			type = types.get("10");
		}
		boolean national;
		if(data[34].equalsIgnoreCase("N")){
			national = true;
		} else {
			national = false;
		}
		Model model = new Model();
		condition =  new Condition(
				new Condition(model, "name", Operator.EQUAL, data[23].trim().replaceAll("\"", "\"\"")), Joiner.AND, 
				new Condition(model, "idBrand", Operator.EQUAL, brand.getId()));
		ArrayList<Object> objects = gP.selectWhere(model, condition, conn);
		if(objects.size() == 0){
			model = new Model(data[23].trim(), national, brand.getId(), type.getId());
			model.setId(gP.insertBean(model, conn));
		} else {
			model = (Model)objects.get(0);
		}
		boolean isVelocity;
		Double limit;
		Double measure;
		if(data[12].trim().equalsIgnoreCase("VELOCIDADE") &&
				!data[14].equalsIgnoreCase("") &&
				!data[15].equalsIgnoreCase("")){
			isVelocity = true;
			limit = Double.parseDouble(data[14]);
			measure = Double.parseDouble(data[15]);
		}else{
			isVelocity = false;
			limit = 0.0;
			measure = 0.0;
		}
		
		Ticket ticket;
		try {
			ticket = new Ticket(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data[20])
					, isVelocity, limit, measure, model.getId(), highwayStretch.getId());
			ticket.setId(gP.insertBean(ticket, conn));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
//	public static HashMap<String, Infraction> saveInfractions(List<String> lines, boolean commit) throws ClassNotFoundException, SQLException, NotNullableException{
//		HashMap<String, Infraction> infractions = new HashMap<String, Infraction>();
//		GenericPersistence gP = new GenericPersistence();
//		gP.openConnection();
//		Connection conn = gP.getConnection();
//		ArrayList<Object> objects = new ArrayList<Object>();
//		for (String line : lines) {
//			line = line.replaceAll("\"", "");
//			String[] data = line.split(";");
//			String key = data[0];
//			String description = data[1].trim();
//			String framing = data[3];
//			Double value;
//			if(data[4].equalsIgnoreCase("")){
//				value = 0.0;
//			} else {
//				value = Double.parseDouble(data[4]);
//			}
//			int cnh_points = Integer.parseInt(data[5]);
//			Infraction infraction = new Infraction(description,framing,value,cnh_points);
//			objects.add(infraction);
//			infractions.put(key, infraction);
//			System.out.println("a"+key);
//			
//		}
//		if(commit){
//			gP.insertAll(objects, conn);
//		}
//		gP.closeConnection();
//		return infractions;
//	}
	
	public static HashMap<String, City> saveCities(List<String> lines, boolean commit) throws ClassNotFoundException, SQLException, NotNullableException{
		HashMap<String, City> cities = new HashMap<String, City>();
		State lastState = new State();
		GenericPersistence gP = new GenericPersistence();
		gP.openConnection();
		Connection conn = gP.getConnection();
		for (String line : lines){
			String[] data = line.split(";");
			String state = data[0];
			String name = data[1];
			String key = data[2];
			
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
