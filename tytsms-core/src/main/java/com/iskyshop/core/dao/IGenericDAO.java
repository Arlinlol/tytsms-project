package com.iskyshop.core.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>
 * Title: IGenericDAO.java
 * </p>
 * 
 * <p>
 * Description:系统泛型DAO基础接口，所有POJO的DAO均需继承该接口，POJO接口无需编写任何方法，通过接口编程完成POJO的注入
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c 1.0
 */
public interface IGenericDAO<T> {

	/**
	 * 根据Id查找一个类型为T的对象。
	 * 
	 * @param id
	 *            传入的ID的值
	 * @return 一个类型为T的对象
	 */
	T get(Serializable id);

	/**
	 * 持久化一个对象，该对象类型为T。
	 * 
	 * @param newInstance
	 *            需要持久化的对象，使用JPA标注。
	 */
	void save(T newInstance);

	/**
	 * 根据对象id删除一个对象，该对象类型为T
	 * 
	 * @param id
	 *            需要删除的对象的id。
	 */
	void remove(Serializable id);

	/**
	 * 更新一个对象，主要用于更新一个在persistenceContext之外的一个对象。
	 * 
	 * @param transientObject
	 *            需要更新的对象，该对象不需要在persistenceContext中。
	 */
	void update(T transientObject);

	/**
	 * 根据对象的一个属性名和该属性名对应的值来查找一个对象。
	 * 
	 * @param propertyName
	 *            属性名
	 * @param value
	 *            属性名对应的值
	 * @return 一个对象，如果在该属性名和值的条件下找到多个对象，则抛出一个IllegalStateException异常
	 */
	T getBy(String propertyName, Object value);

	/**
	 * 根据一个查询条件及其参数，还有开始查找的位置和查找的个数来查找任意类型的对象。
	 * 
	 * @param queryName
	 *            命名查询的名字
	 * @param params
	 *            查询条件中的参数的值。使用Object数组，要求顺序和查询条件中的参数位置一致。
	 * @param begin
	 *            开始查询的位置
	 * @param max
	 *            需要查询的对象的个数
	 * @return 一个任意对象的List对象，如果没有查到任何数据，返回一个空的List对象。
	 */
	List executeNamedQuery(final String queryName, final Object[] params,
			final int begin, final int max);

	/**
	 * 根据查询条件查出对应的数据
	 * 
	 * @param query
	 *            查询的条件，使用位置参数，对象名统一为obj，查询条件从where后开始。 obj.id=:id and
	 *            obj.userRole=:role
	 * @param params
	 *            查询语句中的参数，使用Map传递，结合查询语句中的参数命名来确定 Map map=new HashMap();
	 *            map.put("id",id); map.put("role",role); 该方法的使用方法为: query(
	 *            "select obj from User obj where obj.id=:id and obj.userRole=:role order by obj.addTime desc"
	 *            ,map,1,20);
	 * @param begin
	 *            查询数据的起始位置
	 * @param max
	 *            查询数据的最大值
	 * @return 数据列表
	 */
	List<T> find(String query, Map params, int begin, int max);
	
  /**
   * 根据查询条件查出对应的数据
   * 
   * @param construct
   *            查询构造函数，如果不存在则默认使用obj查询所有字段，可以使用new
   *            Goods(id,goodsName)来查询指定字段，提高系统性能，此时需要在Goods中增加对应的构造函数
   * @param query
   *            查询的条件，使用位置参数，对象名统一为obj，查询条件从where后开始。 obj.id=:id and
   *            obj.userRole=:role
   * @param params
   *            查询语句中的参数，使用Map传递，结合查询语句中的参数命名来确定 Map map=new HashMap();
   *            map.put("id",id); map.put("role",role); 该方法的使用方法为: query(
   *            "select obj from User obj where obj.id=:id and obj.userRole=:role order by obj.addTime desc"
   *            ,map,1,20);
   * @param begin
   *            查询数据的起始位置
   * @param max
   *            查询数据的最大值
   * @return 数据列表
   */
  List<T> find(String construct, String query, Map params, int begin, int max);

	/**
	 * 根据一个查询条件及其参数，还有开始查找的位置和查找的个数来查找任意类型的对象。
	 * 
	 * @param query
	 *            完整的查询语句，使用命名参数。比如：select user from User user where user.name =
	 *            :name and user.properties = :properties
	 * @param params
	 *            查询条件中的参数的值。使用Map
	 * @param begin
	 *            开始查询的位置
	 * @param max
	 *            需要查询的对象的个数
	 * @return 一个任意对象的List对象，如果没有查到任何数据，返回一个空的List对象。
	 */
	List query(String query, Map params, int begin, int max);

	/**
	 * 根据jpql语句执行批量数据更新等操作
	 * 
	 * @param jpql
	 *            需要执行jpql语句
	 * @param params
	 *            语句中附带的参数
	 * @return
	 */
	int batchUpdate(String jpql, Object[] params);

	/**
	 * 执行SQL语句查询
	 * 
	 * @param nnq
	 * @return
	 */
	public List executeNativeNamedQuery(String nnq);

	/**
	 * 
	 * @param nnq
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List executeNativeQuery(final String nnq, final Object[] params,
			final int begin, final int max);

	/**
	 * 执行SQL语句
	 * 
	 * @param nnq
	 * @return
	 */
	public int executeNativeSQL(final String nnq);

	/**
	 * 清除dao
	 */
	public void flush();

}