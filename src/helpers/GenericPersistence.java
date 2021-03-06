package helpers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.sun.org.glassfish.external.statistics.annotations.Reset;

import annotations.Column;
import annotations.Entity;
import annotations.HasMany;
import annotations.HasOne;
import annotations.Ignore;
import annotations.ManyRelations;
import annotations.OneRelations;
import annotations.OrderBy;
import libraries.DatabaseConnection;
import libraries.NotNullableException;

public class GenericPersistence extends DatabaseConnection {
	
	public static String FIELDS = "fields";
	public static String PARAMETERS = "parameters";
	
	public GenericPersistence() throws SQLException, ClassNotFoundException {
		super();
	}

	public Connection getConnection(){
		return this.conn;
	}
	
	public void beginTransaction() throws SQLException{
		this.conn.setAutoCommit(false);
	}
	
	public void endTransaction() throws SQLException{
		this.conn.commit();
		this.conn.setAutoCommit(true);
	}
	
	public int insertBean(Object bean) throws SQLException, NotNullableException {
		this.openConnection();
		int result = insertBean(bean, this.conn);
		this.closeConnection();
		return result;
	}
	
	public int insertBean(Object bean, Connection conn) throws SQLException, NotNullableException {
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		ArrayList<Field> beanFields = getFields(bean);
		
		OneRelations oneRelations = bean.getClass().getAnnotation(OneRelations.class);
		if(oneRelations != null){
			for (int i = 0; i < oneRelations.value().length; i++) {
				HasOne hasOne =  oneRelations.value()[i];
				Field relationField = getField(bean, hasOne.reference());
				if (isNullRelation(bean, relationField)){
					 if (relationField.getAnnotation(Column.class).nullable()){
						 beanFields.remove(relationField);
					 } else {
						 throw new NotNullableException();
					 }
				 }
			}
		}
		
		Field primaryField = primaryField(bean);
		
		beanFields.remove(primaryField);
		
		HashMap<String, String> sqlSets = buildStrings(beanFields);
				
		String sql = "INSERT INTO " + entity.table() + " (" + sqlSets.get(FIELDS) + ")";
		sql += " VALUES(" + sqlSets.get(PARAMETERS)+")";
		this.pst = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		
		prepare(pst, bean, beanFields);
		
		this.pst.executeUpdate();
		ResultSet rs = this.pst.getGeneratedKeys();
		int key = 0;
		if(rs.next()){
			key = rs.getInt(1);
		}
		return key;
	}
	
	public boolean insertAll(ArrayList<Object> beans, Connection conn) throws SQLException, NotNullableException {
		Object bean = beans.get(0);
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		ArrayList<Field> beanFields = getFields(bean);
		
		Field primaryField = primaryField(bean);
		
		beanFields.remove(primaryField);
		
		HashMap<String, String> sqlSets = buildStrings(beanFields);
				
		String sql = "INSERT INTO " + entity.table() + " (" + sqlSets.get(FIELDS) + ")";
		sql += " VALUES(" + sqlSets.get(PARAMETERS)+")";
		this.pst = conn.prepareStatement(sql);
		
		for (Object object : beans) {
			prepare(pst, object, beanFields);
			this.pst.executeUpdate();
		}
		
		int result = 1;
		return (result == 1) ? true : false;
	}
	
	public boolean insertOne(Object bean, Object one) throws SQLException{
		
		int result = this.pst.executeUpdate();
		return (result == 1) ? true : false;
	}
	
	public int insertMany(Object mainBean, Object linkedBean) throws SQLException{
		this.openConnection();
		int result = insertMany(mainBean, linkedBean, this.conn);
		this.closeConnection();
		return result;
	}
	
	public int insertMany(Object mainBean, Object linkedBean, Connection conn) throws SQLException{
		ManyRelations hasMultiple = mainBean.getClass().getAnnotation(ManyRelations.class);
		
		if (hasMultiple != null){
			HasMany hasMany = null;
			for (int i = 0; i < hasMultiple.value().length; i++) {
				if(hasMultiple.value()[i].entity().equals(linkedBean.getClass())){
					hasMany = hasMultiple.value()[i];
				}
			}
			if (hasMany != null){
				Field foreignField = getField(linkedBean, hasMany.foreignKey());
				setValue(linkedBean, foreignField, getValue(mainBean, primaryField(mainBean)));
				
				try {
					return this.insertBean(linkedBean, conn);
				} catch (NotNullableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	
	public Object selectBean(Object bean) throws SQLException {
		this.openConnection();
		Object result = selectBean(bean, this.conn);
		this.closeConnection();
		return result;
	}
	
	public Object selectBean(Object bean, Connection conn) throws SQLException {
		
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		ArrayList<Field> beanFields = getFields(bean);
		Field primaryField = primaryField(bean);
		
		
		String sql = "SELECT * FROM " + entity.table() + " WHERE " + primaryColumn(primaryField) + " = ?";
		this.pst = conn.prepareStatement(sql);
		prepare(this.pst, bean, 1, primaryField);
		
		ResultSet rs = this.pst.executeQuery();
		
		Object result = null;
		if (rs.next()) {
			result = result(rs, bean, beanFields);
		}
		return result;
	}
	
	public Object selectOne(Object bean, Object one) throws SQLException {
		this.openConnection();
		Object result = selectOne(bean, one, this.conn);
		this.closeConnection();
		return result;
	}
	
	public Object selectOne(Object bean, Object one, Connection conn) throws SQLException{
		Object result = null;
		Entity entity = one.getClass().getAnnotation(Entity.class);
		ArrayList<Field> beanFields = getFields(one);
		Field primaryField = primaryField(one);
		OneRelations oneRelations = bean.getClass().getAnnotation(OneRelations.class);
		if(oneRelations != null){
			HasOne hasOne = null;
			for (int i = 0; i < oneRelations.value().length; i++) {
				if(oneRelations.value()[i].entity() == one.getClass()){
					hasOne = oneRelations.value()[i];
				}
			}
			
			String sql = "SELECT * FROM " + entity.table() + " WHERE " + 
					primaryColumn(primaryField) + " = ?";
			this.pst = conn.prepareStatement(sql);
			prepare(this.pst, bean, 1, getField(bean, hasOne.reference()));
			
			ResultSet rs = this.pst.executeQuery();
			
			if (rs.next()) {
				result = result(rs, one, beanFields);
			}
			
			
		}
		return result;
	}

	public ArrayList<Object> selectAllBeans(Object bean) throws SQLException {
		return selectAllBeans(bean, getOrderField(bean));
	}
	public ArrayList<Object> selectAllBeans(Object bean, Field orderBy) throws SQLException {
		this.openConnection();
		ArrayList<Object> beans = selectAllBeans(bean, orderBy, this.conn);
		this.closeConnection();
		return beans;
	}
	public ArrayList<Object> selectAllBeans(Object bean, Connection conn) throws SQLException {
		return selectAllBeans(bean, getOrderField(bean), conn);
	}	
	public ArrayList<Object> selectAllBeans(Object bean, Field orderBy, Connection conn) throws SQLException {
		ArrayList<Object> beans = new ArrayList<Object>();
		
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		ArrayList<Field> beanFields = getFields(bean);
		
		String sql = "SELECT * FROM " + entity.table();
		
		System.out.println(orderBy);
		
		if(orderBy != null){
			sql+= " ORDER BY "+ databaseColumn(orderBy);
		}
		
		this.pst = conn.prepareStatement(sql);

		ResultSet rs = this.pst.executeQuery();
		while (rs.next()) {
			beans.add(result(rs, bean, beanFields));
		}
		return beans;
	}
	
	public ArrayList<Object> selectMany(Object bean, Object target) throws SQLException{
		return selectMany(bean, target, getOrderField(target));
	}
	public ArrayList<Object> selectMany(Object bean, Object target, Field orderBy) throws SQLException{
		this.openConnection();
		ArrayList<Object> beans = selectMany(bean, target, orderBy, this.conn);
		this.closeConnection();
		return beans;
	}
	public ArrayList<Object> selectMany(Object bean, Object target, Connection conn) throws SQLException{
		return selectMany(bean, target, getOrderField(target), conn);
	}
	public ArrayList<Object> selectMany(Object bean, Object target, Field orderBy, Connection conn) throws SQLException{
		ArrayList<Object> beans = new ArrayList<Object>();
		ManyRelations hasMultiple = bean.getClass().getAnnotation(ManyRelations.class);
		
		if (hasMultiple != null){
			HasMany hasMany = null;
			for (int i = 0; i < hasMultiple.value().length; i++) {
				if(hasMultiple.value()[i].entity().equals(target.getClass())){
					hasMany = hasMultiple.value()[i];
				}
			}
			if (hasMany != null){
				Entity entity = target.getClass().getAnnotation(Entity.class);
				String sql = "SELECT * FROM " + entity.table() +" WHERE "
					+ databaseColumn(getField(target, hasMany.foreignKey())) + " = ?";
				
				if(orderBy != null){
					sql+= " ORDER BY "+ databaseColumn(orderBy);
				}
				
				this.pst = conn.prepareStatement(sql);
				
				prepare(pst, bean, 1, primaryField(bean));
				
				ArrayList<Field> targetFields = getFields(target);

				ResultSet rs = this.pst.executeQuery();
				while (rs.next()) {
					beans.add(result(rs, target, targetFields));
				}
			}
		}
		return beans;
	}

	public Integer countBean(Object bean) throws SQLException {
		this.openConnection();
		Integer count = countBean(bean, this.conn);
		this.closeConnection();
		return count;
	}
	
	public Integer countBean(Object bean, Connection conn) throws SQLException {
		Integer count = 0;
		
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		String sql = "SELECT COUNT(*) FROM " + entity.table();

		
		this.pst = conn.prepareStatement(sql);

		ResultSet rs = this.pst.executeQuery();
		if (rs.next())
			count = rs.getInt(1);

		return count;
	}

	public Object firstOrLastBean(Object bean, boolean last) throws SQLException {
		this.openConnection();
		Object result = firstOrLastBean(bean, last, this.conn);
		this.closeConnection();
		return result;
	}
	
	
	public Object firstOrLastBean(Object bean, boolean last, Connection conn) throws SQLException {
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		ArrayList<Field> beanFields = new ArrayList<Field>(
				Arrays.asList(bean.getClass().getDeclaredFields()));
		Object result = null;
		String sql = "SELECT * FROM " + entity.table() + " ORDER BY "
				+ primaryColumn(primaryField(bean));

		if(last)
			sql += " DESC";
		
		sql+=" LIMIT 1";

		
		this.pst = conn.prepareStatement(sql);
		ResultSet rs = this.pst.executeQuery();

		if (rs.next()) {
			result = result(rs, bean, beanFields);
		}

		return result;
	}
	
	public ArrayList<Object> selectWhere(Object bean,Condition condition) throws SQLException{
		return selectWhere(bean, condition, getOrderField(bean));
	}
	public ArrayList<Object> selectWhere(Object bean, Condition condition, Field orderBy) throws SQLException{
		this.openConnection();
		ArrayList<Object> beans = selectWhere(bean, condition, orderBy, this.conn);
		this.closeConnection();
		return beans;
	}
	public ArrayList<Object> selectWhere(Object bean, Condition condition, Connection conn) throws SQLException{
		return selectWhere(bean, condition, getOrderField(bean), conn);
	}
	
	public ArrayList<Object> selectWhere(Object bean, Condition condition, Field orderBy, Connection conn) throws SQLException{
		ArrayList<Object> beans = new ArrayList<Object>();
		ArrayList<Field> beanFields = getFields(bean);
		
		condition.prepareSQL(bean);
		
		String sql = condition.getSql();
		
		if(orderBy != null){
			sql+= " ORDER BY "+ databaseColumn(orderBy);
		}
		this.pst = conn.prepareStatement(sql);

		ResultSet rs = this.pst.executeQuery();
		while (rs.next()) {
			beans.add(result(rs, bean, beanFields));
		}
		return beans;
	}
	
	public boolean deleteBean(Object bean) throws SQLException {
		this.openConnection();
		boolean result = deleteBean(bean, this.conn);
		this.closeConnection();
		return result;
	}
	
	public boolean deleteMany(Object bean, Connection conn) throws SQLException{
		ManyRelations hasMultiple = bean.getClass().getAnnotation(ManyRelations.class);
		
		boolean result = true;
		
		if(hasMultiple != null){
			try {
				for (int i = 0; i < hasMultiple.value().length; i++) {
					HasMany hasMany = hasMultiple.value()[i];
					Class<?> child = hasMany.entity();
					Object childInstance = child.newInstance();
					ArrayList<Object> results = selectMany(bean, childInstance, getOrderField(bean) , conn);
					for (Object object : results) {
						boolean status = deleteBean(object, conn);
						if (!status){
							result = false;
						}
					}
				}
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public boolean deleteBean(Object bean, Connection conn) throws SQLException {
		Entity entity = bean.getClass().getAnnotation(Entity.class);
		ManyRelations hasMultiple = bean.getClass().getAnnotation(ManyRelations.class);
		Field primaryField = primaryField(bean);
		boolean response = true;
		if(hasMultiple != null){
			response = deleteMany(bean, conn);
		}
		if(response){
			String sql = "DELETE FROM "+ entity.table() + " WHERE "+ primaryColumn(primaryField) +" = ?";
			this.pst = conn.prepareStatement(sql);
			prepare(this.pst, bean, 1, primaryField);
			int result = this.pst.executeUpdate();
			return (result == 1) ? true : false;
		}
		return response;
	}
	
	
	//Static Helpers *****************************
	
	public static Field getOrderField(Object bean){
		OrderBy orderBy = bean.getClass().getAnnotation(OrderBy.class);
		if(orderBy != null){
			return getField(bean, orderBy.field());
		}else{
			return null;
		}
	}
	
	public static boolean isNullRelation(Object bean, Field field){
		if (field.getType() == int.class){
			 if ((int)getValue(bean, field) == 0){
				 return true;
			 }
		 } else {
			 if (getValue(bean, field) == null){
				 return true;
			 }
		 }
		return false;
	}
	
	public static Object getValue(Object bean, Field field){
		try {
			return getGetter(field).invoke(bean);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setValue(Object bean, Field field, Object value){
		try {
			getSetter(field).invoke(bean, value);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	public static Field getField(Object bean, String fieldName){
		try {
			return bean.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<Field> getFields(Object bean){
		return new ArrayList<Field>(Arrays.asList(bean.getClass().getDeclaredFields()));
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
			fieldString += "`"+columns.get(i)+"`";
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
				beanGetters.add(getGetter(field));
			}
		}
		return beanGetters;
	}
	
	public static Method getGetter(Field field){
		Method result = null;
		try {
			char[] fieldName = field.getName().trim().toCharArray();
	        String methodName = "";
	        if ((field.getType() == Boolean.class ||
	        		field.getType() == boolean.class) &&
	        		new String(fieldName).contains("is")) {
	        	methodName = new String(fieldName);
	        } else {
	        	fieldName[0] = Character.toUpperCase(fieldName[0]);
	        	methodName = "get" + new String(fieldName);
	        }
	        result = field.getDeclaringClass().getDeclaredMethod(methodName);
	    } catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static ArrayList<Method> getSetters(Object bean, ArrayList<Field> fields) {
		ArrayList<Method> beanSetters = new ArrayList<Method>();
		
		for (Field field : fields) {
			if (field.getAnnotation(Ignore.class) == null){
				beanSetters.add(getSetter(field));
			}
		}
		return beanSetters;
	}
	
	public static Method getSetter(Field field) {
		try {
			char[] fieldName = field.getName().trim().toCharArray();
			String methodName = "";
	        if ((field.getType() == Boolean.class ||
	        		field.getType() == boolean.class) &&
	        		new String(fieldName).contains("is")) {
	        	methodName = new String(fieldName);
	        	methodName = methodName.substring(2);
	        	methodName = "set" + methodName;
	        } else {
	        	fieldName[0] = Character.toUpperCase(fieldName[0]);
	        	methodName = "set" + new String(fieldName);
	        }
	        return field.getDeclaringClass().getDeclaredMethod(methodName, field.getType());
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> databaseColumns(ArrayList<Field> fields) {
		ArrayList<String> databaseColumns = new ArrayList<String>();
		for (Field field : fields) {
			if(field.getAnnotation(Ignore.class) == null){
				databaseColumns.add(databaseColumn(field));
			}
		}
		return databaseColumns;
	}
	
	public static String databaseColumn(Field field){
		Column columnAnnotation = field.getAnnotation(Column.class);
		if (columnAnnotation != null){
			return columnAnnotation.name();
		} else {
			return field.getName();
		}
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
			result = dataGet(rs, i+1, result, setters.get(i),fields.get(i));
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
			return dataGet(rs, index, bean, setter, field);
		}
		return null;
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
			} else if (test instanceof Date) {
				pst.setTimestamp(index, new Timestamp(((Date)test).getTime()));
			} else if (test instanceof Boolean) {
				pst.setBoolean(index, (Boolean)test);
			}
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Object dataGet(ResultSet rs, int index, Object bean, Method setter, Field field) throws SQLException {
		try {
			Class<?> test = setter.getParameterTypes()[0];
			
			if (test == String.class) {
				setter.invoke(bean, rs.getString(databaseColumn(field)));
			} else if (test == Integer.class || test == int.class) {
				setter.invoke(bean, rs.getInt(databaseColumn(field)));
			} else if (test == Double.class) {
				setter.invoke(bean, rs.getDouble(databaseColumn(field)));
			} else if (test == Date.class) {
				setter.invoke(bean, rs.getTimestamp(databaseColumn(field)));
			} else if (test == Boolean.class || test == boolean.class) {
				setter.invoke(bean, rs.getBoolean(databaseColumn(field)));
			}
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bean;
	}

}