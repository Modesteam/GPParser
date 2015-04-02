package models;

import java.lang.annotation.Annotation;
import java.lang.invoke.ConstantCallSite;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import libraries.Column;
import libraries.DatabaseConnection;
import libraries.Entity;
import libraries.Ignore;

public class GenericPersistence extends DatabaseConnection {
	
	public static String FIELDS = "fields";
	public static String PARAMETERS = "parameters";
	
	public GenericPersistence() throws SQLException, ClassNotFoundException {
		super();
	}

	public boolean beanHasElementWithAttribute(Bean bean, String column, String value) throws SQLException {
		int result = 0;

		this.openConnection();
		String sql = "SELECT 1 AS one FROM '"+bean.identifier+"' WHERE '"+bean.identifier+"'.'"+column+"' = ? LIMIT 1";
		this.pst = this.conn.prepareStatement(sql);

		this.pst.setString(1, value);
		ResultSet rs = this.pst.executeQuery();

		if (rs.next()) {
			result = rs.getInt(1);
		}
		this.closeConnection();

		return (result == 1) ? true : false;
	}

	public ArrayList<Bean> selectBeanRelationship(Bean bean, String table)
			throws SQLException {
		this.openConnection();
		ArrayList<Bean> beans = new ArrayList<Bean>();
		String sql = "SELECT c.* FROM " + table + " as c, " + bean.relationship
				+ " as ci " + "WHERE ci.id_" + bean.identifier + "= ? "
				+ "AND ci.id_" + table + " = c.id";
		this.pst = this.conn.prepareStatement(sql);
		this.pst.setString(1, bean.get(bean.fieldsList().get(0)));
		ResultSet rs = this.pst.executeQuery();
		while (rs.next()) {
			Bean object = init(table);
			for (String s : object.fieldsList()) {
				object.set(s, rs.getString(s));
			}
			beans.add(object);
		}
		this.closeConnection();
		return beans;
	}

	public boolean verifyIfAlreadyExistsBeanRelationship (Bean first, Bean second) throws SQLException {
		int result = 0;

		this.openConnection();
		String sql = "SELECT 1 AS one FROM 'courses_institutions' "+
				"WHERE 'courses_institutions'.'id_"+first.identifier+"' = ? "+
				"AND 'courses_institutions'.'id_"+second.identifier+"' = ? "+
				"LIMIT 1";

		this.pst = this.conn.prepareStatement(sql);
		this.pst.setString(1, first.get(first.fieldsList().get(0)));
		this.pst.setString(2, second.get(second.fieldsList().get(0)));

		ResultSet rs = this.pst.executeQuery();

		if (rs.next()) {
			result = rs.getInt(1);
		}
		this.closeConnection();

		return (result == 1) ? true : false;
	}
	
	
	public boolean insertBean(Object bean) throws SQLException {
		this.openConnection();	
		
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		ArrayList<Field> beanFields = new ArrayList<Field>(Arrays.asList(bean.getClass().getDeclaredFields()));
		Field primaryField = primaryField(bean);
		
		beanFields.remove(primaryField);
		
		HashMap<String, String> sqlSets = buildStrings(beanFields);
				
		String sql = "INSERT INTO " + entity.table() + " (" + sqlSets.get(FIELDS) + ")";
		sql += " VALUES(" + sqlSets.get(PARAMETERS)+")";
		this.pst = this.conn.prepareStatement(sql);
		
		prepare(pst, bean, beanFields);
		
		int result = this.pst.executeUpdate();
		this.closeConnection();
		return (result == 1) ? true : false;
	}
	
	
	public static Field primaryField(Object bean){
		Field primaryField = null;
		
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		
		try {
			primaryField = bean.getClass().getDeclaredField(entity.primaryKey());
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return primaryField;
	}
	
	public static String primaryColumn(Field primaryField){
		Column column = primaryField.getAnnotation(Column.class);
		if (column == null){
			return primaryField.getName();
		} else {
			return column.name();
		}
	}
	
	public static HashMap<String, String> buildStrings(ArrayList<Field> fields){
		HashMap<String, String> result = new HashMap<String,String>();
		ArrayList<String> columns = databaseColumns(fields);
		String fieldString = "";
		String parameterString = "";
		String joiner = ",";
		
		for (int i = 0; i < columns.size(); i++) {
			fieldString += columns.get(i);
			parameterString += "?";
			if (i < columns.size() - 1) {
				fieldString += joiner;
				parameterString += joiner;
			}
		}
		
		result.put(FIELDS, fieldString);
		result.put(PARAMETERS, parameterString);
		
		return result;
	}
	
	public static String join(ArrayList<String> strings, String joiner) {
		String resultString = null;
		for (int i = 0; i < strings.size(); i++) {
			resultString += strings.get(i);
			if (i < strings.size() - 1) {
				resultString += joiner;
			}
		}
		return resultString;
	}
	
	public static ArrayList<Method> getGetters(Object bean, ArrayList<Field> fields) {
		ArrayList<Method> beanGetters = new ArrayList<Method>();
		
		for (Field field : fields) {
			if (field.getAnnotation(Ignore.class) == null){
				try {
					char[] fieldName = field.getName().trim().toCharArray();
			        fieldName[0] = Character.toUpperCase(fieldName[0]);
					beanGetters.add(bean.getClass().getDeclaredMethod("get"+new String(fieldName)));
				} catch (NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return beanGetters;
	}
	
	public static ArrayList<Method> getSetters(Object bean, ArrayList<Field> fields) {
		ArrayList<Method> beanSetters = new ArrayList<Method>();
		
		for (Field field : fields) {
			if (field.getAnnotation(Ignore.class) == null){
				try {
					char[] fieldName = field.getName().trim().toCharArray();
			        fieldName[0] = Character.toUpperCase(fieldName[0]);
					beanSetters.add(bean.getClass().getDeclaredMethod("set"+new String(fieldName), field.getType()));
				} catch (NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return beanSetters;
	}
	
	public static ArrayList<String> databaseColumns(ArrayList<Field> fields) {
		ArrayList<String> databaseColumns = new ArrayList<String>();
		for (Field field : fields) {
			Column columnAnnotation = field.getAnnotation(Column.class);
			if(field.getAnnotation(Ignore.class) == null){
				if (columnAnnotation != null){
					databaseColumns.add(columnAnnotation.name());
				} else {
					databaseColumns.add(field.getName());
				}
			}
		}
		return databaseColumns;
	}
	
	public static void prepare(PreparedStatement pst, Object bean, ArrayList<Field> fields) throws SQLException{
		ArrayList<Method> getters = getGetters(bean, fields);
		for (int i = 0; i < getters.size(); i++) {
			dataSet(pst, i+1, bean, getters.get(i));
		}
	}
	
	public static void prepare(PreparedStatement pst, Object bean, int index, Field field) throws SQLException {
		Method getter = null;
		if (field.getAnnotation(Ignore.class) == null){
			try {
				char[] fieldName = field.getName().trim().toCharArray();
		        fieldName[0] = Character.toUpperCase(fieldName[0]);
				getter = bean.getClass().getDeclaredMethod("get"+new String(fieldName));
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dataSet(pst, index, bean, getter);
	}
	
	public static Object result(ResultSet rs, Object bean, ArrayList<Field> fields) throws SQLException {
		Object result = null;
		try {
			result = bean.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Method> setters = getSetters(bean, fields);
		for (int i = 0; i < setters.size(); i++) {
			result = dataGet(rs, i+1, result, setters.get(i));
		}
		return result;
	}
	
	public static Object result(ResultSet rs, Object bean, int index, Field field) throws SQLException {
		Method setter = null;
		if (field.getAnnotation(Ignore.class) == null){
			try {
				char[] fieldName = field.getName().trim().toCharArray();
		        fieldName[0] = Character.toUpperCase(fieldName[0]);
				setter = bean.getClass().getDeclaredMethod("set"+new String(fieldName), field.getType());
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dataGet(rs, index, bean, setter);
	}
	
	
	public static void dataSet(PreparedStatement pst, int index, Object bean, Method getter) throws SQLException {
		try {
			Object test = getter.invoke(bean);
			
			if (test instanceof String) {
				pst.setString(index, (String)test);
			} else if (test instanceof Integer) {
				pst.setInt(index, (Integer)test);
			} else if (test instanceof Double) {
				pst.setDouble(index, (Double)test);
			}
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Object dataGet(ResultSet rs, int index, Object bean, Method setter) throws SQLException {
		try {
			Class<?> test = setter.getParameterTypes()[0];
			
			if (test == String.class) {
				setter.invoke(bean, rs.getString(index));
			} else if (test == Integer.class || test == int.class) {
				setter.invoke(bean, rs.getInt(index));
			} else if (test == Double.class) {
				setter.invoke(bean, rs.getDouble(index));
			}
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bean;
	}

	public boolean addBeanRelationship(Bean parentBean, Bean childBean)
			throws SQLException {
		this.openConnection();
		String sql = "INSERT INTO " + parentBean.relationship + "(id_"
				+ parentBean.identifier + ",id_" + childBean.identifier
				+ ") VALUES(?,?)";
		this.pst = this.conn.prepareStatement(sql);
		this.pst.setString(1, parentBean.get(parentBean.fieldsList().get(0)));
		this.pst.setString(2, childBean.get(childBean.fieldsList().get(0)));
		int result = this.pst.executeUpdate();
		this.closeConnection();
		return (result == 1) ? true : false;
	}
	
	public boolean deleteBeanRelationship(Bean parentBean, Bean childBean)
			throws SQLException {
		this.openConnection();
		String sql = "DELETE FROM " + parentBean.relationship + "  WHERE id_"
				+ parentBean.identifier + " = ? AND id_" + childBean.identifier
				+ " = ?";
		this.pst = this.conn.prepareStatement(sql);
		this.pst.setString(1, parentBean.get(parentBean.fieldsList().get(0)));
		this.pst.setString(2, childBean.get(childBean.fieldsList().get(0)));
		int result = this.pst.executeUpdate();
		this.closeConnection();
		return (result == 1) ? true : false;
	}

	public Object selectBean(Object bean) throws SQLException {
		this.openConnection();
		
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		ArrayList<Field> beanFields = new ArrayList<Field>(Arrays.asList(bean.getClass().getDeclaredFields()));
		Field primaryField = primaryField(bean);
		
		
		String sql = "SELECT * FROM " + entity.table() + " WHERE " + primaryColumn(primaryField) + " = ?";
		this.pst = this.conn.prepareStatement(sql);
		prepare(this.pst, bean, 1, primaryField);
		
		ResultSet rs = this.pst.executeQuery();
		
		Object result = null;
		if (rs.next()) {
			result = result(rs, bean, beanFields);
		}
		this.closeConnection();
		return result;
	}

	public ArrayList<Bean> selectAllBeans(Bean type) throws SQLException {
		this.openConnection();
		ArrayList<Bean> beans = new ArrayList<Bean>();
		String sql = "SELECT * FROM " + type.identifier;
		this.pst = this.conn.prepareStatement(sql);

		ResultSet rs = this.pst.executeQuery();
		while (rs.next()) {
			Bean bean = init(type.identifier);
			for (String s : type.fieldsList()) {
				bean.set(s, rs.getString(s));
			}
			beans.add(bean);
		}
		this.closeConnection();
		return beans;
	}

	public Integer countBean(Bean type) throws SQLException {
		Integer count = 0;
		String sql = "SELECT COUNT(*) FROM " + type.identifier;

		this.openConnection();
		this.pst = this.conn.prepareStatement(sql);

		ResultSet rs = this.pst.executeQuery();
		if (rs.next())
			count = rs.getInt(1);

		this.closeConnection();

		return count;
	}

	public Bean firstOrLastBean(Bean type, boolean last) throws SQLException {
		Bean bean = null;
		String sql = "SELECT * FROM " + type.identifier + " ORDER BY "
				+ type.fieldsList().get(0);

		if (!last)
			sql += " LIMIT 1";
		else
			sql += " DESC LIMIT 1";

		this.openConnection();
		this.pst = this.conn.prepareStatement(sql);
		ResultSet rs = this.pst.executeQuery();

		if (rs.next()) {
			bean = init(type.identifier);
			for (String s : type.fieldsList()) {
				bean.set(s, rs.getString(s));
			}
		}
		this.closeConnection();

		return bean;
	}

	public ArrayList<Bean> selectBeanWhere(Bean type, String field,
			String value, boolean use_like) throws SQLException {
		ArrayList<Bean> beans = new ArrayList<Bean>();
		String sql = "SELECT * FROM " + type.identifier + " WHERE ";

		if (!use_like)
			sql += field+" =?";
		else
			sql += field+" LIKE ?";

		this.openConnection();
		this.pst = this.conn.prepareStatement(sql);
		if (use_like)
			this.pst.setString(1, "%" + value + "%");
		else
			this.pst.setString(1, value);

		ResultSet rs = this.pst.executeQuery();
		while (rs.next()) {
			Bean bean = init(type.identifier);
			for (String s : type.fieldsList()) {
				bean.set(s, rs.getString(s));
			}
			beans.add(bean);
		}
		this.closeConnection();

		return beans;
	}
	
	public boolean deleteBean(Bean bean) throws SQLException {
		this.openConnection();
		String sql = "DELETE FROM "+bean.identifier+ " WHERE "+bean.fieldsList().get(0)+" = ?";
		this.pst = this.conn.prepareStatement(sql);
		this.pst.setString(1, bean.get(bean.fieldsList().get(0)));
		int result = this.pst.executeUpdate();
		this.closeConnection();
		return (result == 1) ? true : false;
	}

	public Bean init(String beanIdentifier) {
		Bean object = null;
		if (beanIdentifier.equals("dummy")) {
			//object = new Dummy();
		}		
		return object;
	}

}