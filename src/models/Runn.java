package models;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import libraries.Column;

public class Runn {
	
	private static final String  fmt = "%24s: %s%n";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Carro carro = new Carro();
		carro.setModelo("Gol");
		carro.setRodas(3);
		
//		ArrayList<Field> beanFields = new ArrayList<Field>(Arrays.asList(bean.getClass().getDeclaredFields()));
//		//System.out.println(beanFields.size());
//		ArrayList<Method> methods = GenericPersistence.getGetters(bean, beanFields);
//		//System.out.println(methods.size());
//		for (Method method : methods) {
//		//	System.out.println(method.getName());
//		}
//		for (String field : GenericPersistence.databaseColumns(beanFields)) {
//		//	System.out.println(field);
//		}
		
		
		//System.out.println(beanFields.get(1).getAnnotation(Column.class).name());
		
		
		GenericPersistence gp;
		try {
			gp = new GenericPersistence();
			
			gp.insertBean(carro);
			
			Carro carro2 = new Carro(1);
			
			System.out.println(gp.selectBean(carro2));
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Method m = null;
//		try {
//			m = bean.getClass().getDeclaredMethod("setTest", 
//					new Class<?>[]{ bean.getClass().getDeclaredField("test").getType()});
//			Class<?> classs =  bean.getClass().getDeclaredField("test").getType();
//			System.out.println(classs == Integer.class);
//		} catch (NoSuchMethodException | SecurityException
//				| NoSuchFieldException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		
//		
//		Class<?>[] pType  = m.getParameterTypes();
//		Type[] gpType = m.getGenericParameterTypes();
//		for (int i = 0; i < pType.length; i++) {
//		    System.out.format(fmt,"ParameterType", pType[i]);
//		    System.out.format(fmt,"GenericParameterType", gpType[i]);
//		}
		
		
		
		
	}

}
