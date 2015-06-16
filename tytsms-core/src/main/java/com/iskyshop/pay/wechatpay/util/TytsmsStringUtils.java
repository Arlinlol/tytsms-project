package com.iskyshop.pay.wechatpay.util;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 功能描述：字符处理通用类
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author Frank 新增日期：2015年3月15日 下午11:43:36
 * @author Frank 修改日期：2015年3月15日 下午11:43:36
 *
 */
public  class TytsmsStringUtils {

	
	/**
	 * 生成最终上传图片文件的文件夹路径
	 * @param request
	 * @param config
	 * @return
	 */
	public static String generatorImagesFolderServerPath(HttpServletRequest request){
		String path = (ConfigContants.IS_UPLOAD_SERVER_MODEL ? ConfigContants.UPLOAD_IMAGE_SERVER_PATH
				: request.getSession().getServletContext().getRealPath(""))
				+ File.separator;
		File imageFolder = new File(path);
		if(!imageFolder.exists()){
			imageFolder.mkdirs();
		}
		return path;
	}
	
	/**
	 * 生成最终生成通知模板、首页静态模板文件等文件夹路径
	 * @param request
	 * @param config
	 * @return
	 */
	public static String generatorFilesFolderServerPath(HttpServletRequest request){
		String path = (ConfigContants.IS_UPLOAD_SERVER_MODEL ? ConfigContants.GENERATOR_FILES_SERVER_PATH
				: request.getSession().getServletContext().getRealPath(""))
				+ File.separator ;
		File generatorFolder = new File(path);
		if(!generatorFolder.exists()){
			generatorFolder.mkdirs();
		}
		return path;
	}

}
