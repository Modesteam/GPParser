package helpers;

import java.util.ArrayList;
import java.util.HashMap;
import annotations.Entity;
import annotations.HasMany;
import annotations.HasOne;
import annotations.ManyRelations;
import annotations.OneRelations;



public class Condition {
	
	static HashMap<Operator, String> operators = new HashMap<Operator,String>(){
		private static final long serialVersionUID = 1L;
	{
		put(Operator.DIFERENT, " != ");
		put(Operator.EQUAL, " = ");
		put(Operator.GREATER, " > ");
		put(Operator.LESSER, " < ");
		put(Operator.LIKE, " LIKE ");
	}};
	static HashMap<Joiner, String> joiners = new HashMap<Joiner,String>(){
		private static final long serialVersionUID = 1L;
	{
		put(Joiner.AND, " AND ");
		put(Joiner.OR, " OR ");
	}};
	
	private String sql;
	private String field;
	private Object bean;
	private Operator operator;
	private Object value;
	private Condition first;
	private Joiner joiner;
	private Condition second;
	private boolean multiple;
	
	public Condition(Object bean, String field, Operator operator, Object value){
		this.bean = bean;
		this.field = field;
		this.operator = operator;
		this.value = value;
		this.multiple = false;
	}

	public Condition(Condition first, Joiner joiner, Condition second){
		this.first = first;
		this.joiner = joiner;
		this.second = second;
		this.multiple = true;
	}
	
	public String buildConditionSQL(Object mainBean){		
		String sql = " ";
		if(this.multiple){
			sql += this.first.buildConditionSQL(mainBean);
			sql += getJoiner(this.joiner);
			sql += this.second.buildConditionSQL(mainBean);
		} else {
			String result = "";
			if(this.value.getClass() == String.class){
				result = "'"+this.value.toString()+"'";
			}else{
				result = this.value.toString();
			}
			Entity entity = this.bean.getClass().getAnnotation(Entity.class);
			sql = entity.table()+"."+GenericPersistence.databaseColumn(GenericPersistence.getField(this.bean, this.field))+
					getOperator(this.operator)+result;
		}
		return sql;
	}
	
	public void prepareSQL(Object mainBean){
		ArrayList<Entity> entities = this.getEntities();
		Entity mainEntity = mainBean.getClass().getAnnotation(Entity.class);
		entities.remove(mainEntity);
		String sql = "SELECT "+mainEntity.table()+".* FROM "+mainEntity.table();
		for (Entity entity : entities) {
			sql+=", "+entity.table();
		}
		sql+=" WHERE "+buildRelationshipChain(mainBean, entities)+buildConditionSQL(mainBean);
		this.sql=sql;
	}
	
	public String buildRelationshipChain(Object mainBean, ArrayList<Entity> entities){
		String sql = " ";
		ManyRelations manyRelations = mainBean.getClass().getAnnotation(ManyRelations.class);
		OneRelations oneRelations = mainBean.getClass().getAnnotation(OneRelations.class);
		try {
			if(manyRelations != null){
				for (int i = 0; i < manyRelations.value().length; i++) {
					HasMany hasMany = manyRelations.value()[i];
					Object nextBean;
					nextBean = hasMany.entity().newInstance();
					Entity mainEntity = mainBean.getClass().getAnnotation(Entity.class);
					Entity entity = hasMany.entity().getAnnotation(Entity.class);
					if(entities.contains(entity)){
						sql += entity.table()+"."+
								GenericPersistence.databaseColumn(
										GenericPersistence.getField(nextBean, hasMany.foreignKey()))+
										" = "+mainEntity.table()+"."+GenericPersistence.primaryColumn(
												GenericPersistence.primaryField(mainBean)) + " AND ";
						entities.remove(entity);
						sql += this.buildRelationshipChain(nextBean, entities);
					}
				}
			}
			if(oneRelations != null){
				for (int i = 0; i < oneRelations.value().length; i++) {
					HasOne hasOne = oneRelations.value()[i];
					Object nextBean = hasOne.entity().newInstance();
					Entity mainEntity = mainBean.getClass().getAnnotation(Entity.class);
					Entity entity = hasOne.entity().getAnnotation(Entity.class);
					if(entities.contains(entity)){
						sql += entity.table()+"."+GenericPersistence.primaryColumn(
								GenericPersistence.primaryField(nextBean))+" = "+
								mainEntity.table()+"."+GenericPersistence.databaseColumn(
										GenericPersistence.getField(mainBean, hasOne.reference()))+ " AND ";
						entities.remove(entity);
						sql += this.buildRelationshipChain(nextBean, entities);
					}
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sql;
	}
	
	public ArrayList<Entity> getEntities(){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		ArrayList<Entity> localEntities = new ArrayList<Entity>();
		if(this.multiple){
			localEntities = this.first.getEntities();
			for (Entity entity : localEntities) {
				if(!entities.contains(entity))
					entities.add(entity);
			}
			localEntities = this.second.getEntities();
			for (Entity entity : localEntities) {
				if(!entities.contains(entity))
					entities.add(entity);
			}
		}else{
			Entity entity = this.bean.getClass().getAnnotation(Entity.class);
			entities.add(entity);
		}		
		return entities;
	}
	
	public String getOperator(Operator operator){
		return operators.get(operator);
	}
	
	public String getJoiner(Joiner joiner){
		return joiners.get(joiner);
	}
	
	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public Condition getFirst() {
		return first;
	}

	public void setFirst(Condition first) {
		this.first = first;
	}

	public Condition getSecond() {
		return second;
	}

	public void setSecond(Condition second) {
		this.second = second;
	}
	
	public String getSql() {
		return this.sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
