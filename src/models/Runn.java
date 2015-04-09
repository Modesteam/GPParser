package models;

import helpers.Condition;
import helpers.GenericPersistence;
import helpers.Joiner;
import helpers.Operator;

import java.sql.Connection;
import java.sql.SQLException;

import libraries.NotNullableException;

public class Runn {

	public static void main(String[] args) {
		Carro carro = new Carro();
		carro.setModelo("Gol");
		carro.setRodas(1);
		
		Dummy dummy = new Dummy();
		dummy.setName("lol");
		dummy.setTest(34);
		
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
			
			gp.openConnection();
			
			Connection conn = gp.getConnection();
			
			//gp.insertBean(dummy, conn);
			
			dummy = (Dummy) gp.firstOrLastBean(dummy, true, conn);
			
			System.out.println(dummy);
			
			//gp.insertMany(dummy, carro, conn);
			
			Carro carro2 = (Carro) gp.firstOrLastBean(carro, true, conn);
			
			System.out.println(dummy);
			
			//gp.deleteBean(dummy, conn);
			
			dummy = (Dummy) gp.firstOrLastBean(dummy, true, conn);
			
			Condition condition = new Condition(
					new Condition(carro, "modelo" ,Operator.EQUAL, "Gol"),
					Joiner.AND,
					new Condition(dummy, "name", Operator.EQUAL, "aame"));
			
			condition.prepareSQL(carro2);
			
			
			Engine engine = null;
			//try {
				//gp.insertBean(new Engine(3), conn);
				engine = (Engine)gp.firstOrLastBean(new Engine(), true, conn);
				//carro2.setIdEngine(engine.getId());
				//gp.insertBean(carro2,conn);
			//} catch (NotNullableException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
			
			System.out.println(gp.selectOne(carro2, dummy,conn));
			
			//System.out.println(condition.getEntities());
			
			//System.out.println(condition.buildRelationshipChain(carro2, condition.getEntities()));
			
			//System.out.println(condition.getSql());
			
			//System.out.println(dummy);
			for (Object obj : gp.selectWhere(carro2, condition)) {
			//	System.out.println((Carro)obj);
			}
			gp.closeConnection();
			
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
