package models;

import helpers.Condition;
import helpers.GenericPersistence;
import helpers.Joiner;
import helpers.Operator;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import libraries.NotNullableException;

public class Runn {

	public static void main(String[] args) {
		try {
			List<String> lines;
			System.out.println("Read start! Brand");
			
				lines = Files.readAllLines(Paths.get("jars/files/brand_relation.csv") , Charset.forName("ISO-8859-1"));
			
			final HashMap<String, Brand> brands = new HashMap<String, Brand>();
			for (String string : lines) {
				String[] data = string.split(";");
				brands.put(data[0], Brand.get(Integer.parseInt(data[1])));
			}
			System.out.println("Read done!");
			
			System.out.println("Read start! Type");
			lines = Files.readAllLines(Paths.get("jars/files/type_relation.csv") , Charset.forName("ISO-8859-1"));
			final HashMap<String, Type> types = new HashMap<String, Type>();
			for (String string : lines) {
				String[] data = string.split(";");
				types.put(data[0], Type.get(Integer.parseInt(data[1])));
			}
			System.out.println("Read done!");
			
			
			DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get("jars/files/ticket/"));
			Iterator<Path> iterator = paths.iterator();
			
			//while(iterator.hasNext()){
				
				Path path = iterator.next();
				System.out.println(path.toFile().getName());
				System.out.println("Start: "+Calendar.getInstance().getTime());
				final BufferedReader bufferedReader = Files.newBufferedReader(path, Charset.forName("ISO-8859-1"));
				bufferedReader.readLine();
				
				final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
				
				
				final ThreadGroup readGroup = new ThreadGroup("Read");
				Thread[] readThreads = new Thread[]{};
				
				for (int i = 0; i < 6; i++) {
					Thread thread = new Thread(readGroup, new Runnable() {
						
						@Override
						public void run() {
							try {
								while(bufferedReader.ready()){
									queue.add(bufferedReader.readLine());
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					thread.start();
				}
				
				
				final ThreadGroup processGroup = new ThreadGroup("Process");
				Thread[] processThreads = new Thread[]{};
				for (int i = 0; i < 4; i++) {
					Thread thread = new Thread(processGroup, new Runnable() {
						
						@Override
						public void run() {
							try {
								GenericPersistence gP= new GenericPersistence();
								gP.openConnection();
								gP.beginTransaction();
								Connection conn = gP.getConnection();
								int i = 0;
								while(!queue.isEmpty()){
									String line = queue.poll();
									if(line != null){
										i++;
										Demo.saveEach(line, true, brands, types, conn, gP);
									}
									if(i % 1000 == 0){
										System.out.println(i);
									}
									if(i % 10000 == 0){
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
							
							} catch (ClassNotFoundException | SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NotNullableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					thread.start();
				}
				
				
				readGroup.enumerate(readThreads);
				processGroup.enumerate(processThreads);
				
				try {
					for (int i = 0; i < processThreads.length; i++) {
						processThreads[i].join();
					}
					for (int i = 0; i < readThreads.length; i++) {
						readThreads[i].join();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			//}
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
