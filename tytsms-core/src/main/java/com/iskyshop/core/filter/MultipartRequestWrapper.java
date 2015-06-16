package com.iskyshop.core.filter;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.util.HtmlUtils;


    /** 
     * 包装HttpServletRequest，针对multipart requests 重新实现getParameter，以方便获得普通表单域参数(文本)。 
     * 文件参数和 
     */  
    public class MultipartRequestWrapper extends DefaultMultipartHttpServletRequest {  
        /** 
         * 用于存放multipart request的参数 
         */  
        protected Map parameters;  
        
        public  MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<String, MultipartFile>();
        
        public Map<String, String[]> multipartParameters = new HashMap<String, String[]>();
       
      
        /** 
         * 持有被装饰者（HttpServletRequest对象）引用，初始化用于存放multipart request的参数的Map 
         */  
        public MultipartRequestWrapper(HttpServletRequest request) {  
            super(request);  
            String partEncoding  =  request.getCharacterEncoding();
            this.parameters = new HashMap();  
			  //为该请求创建一个DiskFileItemFactory对象，通过它来解析请求
	        FileItemFactory factory = new DiskFileItemFactory();
	        ServletFileUpload upload = new ServletFileUpload(factory);
	         //将所有的表单项目都保存到List中
	        try {
	        	List<FileItem> items = upload.parseRequest(request);
		        Iterator  itr = items.iterator();
		         //循环list，取得表单项
		         while (itr.hasNext()) {
		              FileItem item = (FileItem) itr.next();
		               //检查当前项目是普通表单项目还是文件。
		              String fieldName = item.getFieldName();
		               if (item.isFormField()) {//如果是普通表单项目，显示表单内容。
		            	   String value ;
		            	   if (partEncoding != null) {
			   					try {
			   						value = item.getString(partEncoding);
			   					}catch (UnsupportedEncodingException ex) {
			   						value = item.getString();
			   					}
			   				}else {
			   					value = item.getString();
			   				}
		            	   //无需转码的，添加到此处，不进行转码
		            	    if(!"share_code".equals(fieldName)&&!"codeStat".equals(fieldName)){
		            		    value = HtmlUtils.htmlEscape(value);
		            	    }
		            	   
		            	  //  System. out .println("key: "+ fieldName + " value:" + value + "item.getString() : " +item.getString() + " Encoding:" + partEncoding  ); //显示表单内容
		                    parameters.put(fieldName, value);
		                    
		                    String[] curParam = multipartParameters.get(item.getFieldName());
		    				if (curParam == null) {
		    					// simple form field
		    					multipartParameters.put(item.getFieldName(), new String[] {value});
		    				}
		    				else {
		    					// array of simple form fields
		    					String[] newParam = StringUtils.addStringToArray(curParam, value);
		    					multipartParameters.put(item.getFieldName(), newParam);
		    				}
		                    
		              }else { //如果上传文件的file的name为" filecer"
		            	  CommonsMultipartFile file = new CommonsMultipartFile(item);
		            	  multipartFiles.add(fieldName, file);
		              }
		               
		        }
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	setMultipartFiles(multipartFiles);
			setMultipartParameters(multipartParameters);
        }  
      
      
      
      
        /** 
         * 通过包装器存放参数的Map对象，如果参数有多个值返回第一个值。 
         */  
        public String getParameter(String name) {  
        	
            String value = (String) parameters.get(name); 
//            System.out.println("name:"+ name +" \nvalue:" + value);
            return value;  
        }  
      
        /** 
         *  通过包装器存放参数的Map对象获得参数，如果不为null ，以String[]形式返回。 
         */  
        public String[] getParameterValues(String name) {  
        	String[] value = new String[1];
            value[0] =  (String) parameters.get(name);  
            return value;  
        }  
          
       
        /** 
         * 通过包装器存放参数的Map对象 获得参数名称枚举 
         */  
        public Enumeration getParameterNames() {  
            Vector list = new Vector();  
            Collection multipartParams = parameters.keySet();  
            Iterator iterator = multipartParams.iterator();  
            while (iterator.hasNext()) {  
                list.add(iterator.next());  
            }  
            return Collections.enumeration(list);  
        }  
      
        /** 
         *  返回存放参数的Map对象 
         */  
        public Map getParameterMap() {  
            return parameters;  
        }  
    }  