package com.iskyshop.core.exception;
/**
 * 
* <p>Title: CanotRemoveObjectException.java</p>

* <p>Description:删除对象异常，继承在RuntimeException，后续系统将会自定义更多异常，方便程序员调试程序 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class CanotRemoveObjectException extends RuntimeException {

	@Override
	public void printStackTrace() {
		// TODO Auto-generated method stub
		System.out.println("删除对象错误!");
		super.printStackTrace();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
