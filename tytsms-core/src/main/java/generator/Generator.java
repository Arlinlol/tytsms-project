package generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.iskyshop.core.annotation.Title;

public class Generator {

	protected String templateDir = "\\templates\\";
	protected String resultDir = "\\result\\";
	private String beanName;
	private String packageName;
	private Map<String, String> javafiles;
	private Map<String, String> htmlfiles;

	public Generator(String[] args) {
		String bean = args[0];
		String packageName = args[1];
		this.beanName = bean;
		this.packageName = packageName;
	}

	public void init() {
		String bea = beanName.substring(beanName.lastIndexOf(".") + 1);
		addJavaFiles(bea);
		addHtmlFiles(bea);
	}

	private void addJavaFiles(String bea) {
		Map<String, String> javaMapping = new HashMap<String, String>();
		javaMapping.put("actionTemplate.java", "manage/action/" + bea
				+ "ManageAction.java");
		javaMapping.put("daotemplate.java", "dao/" + bea + "DAO.java");
		javaMapping.put("IserviceTemplate.java", "service/" + "I" + bea
				+ "Service.java");
		javaMapping.put("queryObjectTemplate.java", "domain/query/" + bea
				+ "QueryObject.java");
		javaMapping.put("serviceImplTemplate.java", "service/impl/" + bea
				+ "ServiceImpl.java");
		this.javafiles = javaMapping;
	}

	private void addHtmlFiles(String bealower) {
		Map<String, String> htmlMapping = new HashMap<String, String>();
		htmlMapping.put("tag_add.html", bealower + "_add.html");
		htmlMapping.put("tag_list.html", bealower + "_list.html");
		this.htmlfiles = htmlMapping;
	}

	private void mergeJava(String sourcePath, String targetPath)
			throws Exception {
		for (String templateFile : this.javafiles.keySet()) {
			String targetFile = this.javafiles.get(templateFile);
			Properties pro = new Properties();
			pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, sourcePath);
			VelocityEngine ve = new VelocityEngine(pro);
			org.apache.velocity.Template t = ve.getTemplate(templateFile,
					"UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("domainName", this.beanName.substring(beanName
					.lastIndexOf(".") + 1));
			context.put("domain", (this.beanName.substring(beanName
					.lastIndexOf(".") + 1).toLowerCase()));
			context.put("packageName", this.packageName);
			File file = new File(targetPath, targetFile);
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (!file.exists())
				file.createNewFile();

			FileOutputStream outStream = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(outStream,
					"UTF-8");
			BufferedWriter sw = new BufferedWriter(writer);
			t.merge(context, sw);
			sw.flush();
			sw.close();
			outStream.close();
			System.out.println("成功生成Java文件:"
					+ (targetPath + targetFile).replaceAll("/", "\\\\"));
		}
	}

	private void mergeHTML(String sourcePath, String targetPath)
			throws Exception {
		for (String templateFile : this.htmlfiles.keySet()) {
			String targetFile = this.htmlfiles.get(templateFile);
			Properties pro = new Properties();
			pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, sourcePath);
			VelocityEngine ve = new VelocityEngine(pro);
			org.apache.velocity.Template t = ve.getTemplate(templateFile,
					"UTF-8");
			VelocityContext context = new VelocityContext();
			Class clz = Class.forName(beanName);
			Field[] fields = clz.getDeclaredFields();
			List<UserField> ufs = new ArrayList<UserField>();
			for (Field field : fields) {
				if (field.getType().isPrimitive()
						|| field.getType().toString().indexOf("java.lang.") >= 0
						|| field.getType().toString().indexOf("java.util.") >= 0) {
					UserField uf = new UserField();
					uf.setName(field.getName());
					uf.setType(field.getType().getName());
					if (field.isAnnotationPresent(Title.class)) {
						Title title = field.getAnnotation(Title.class);
						uf.setTitle(title.value());
					} else
						uf.setTitle(field.getName());
					ufs.add(uf);
				}
			}
			context.put("fields", ufs);
			context.put("domainName", this.beanName.substring(beanName
					.lastIndexOf(".") + 1));
			FileOutputStream outStream = new FileOutputStream(new File(
					targetPath, targetFile));
			OutputStreamWriter writer = new OutputStreamWriter(outStream,
					"UTF-8");
			BufferedWriter sw = new BufferedWriter(writer);
			t.merge(context, sw);
			sw.flush();
			sw.close();
			outStream.close();
			System.out.println("成功生成页面文件:" + targetPath + targetFile);
		}
	}

	public void generate() {
		this.init();
		try {
			String sourcePath = System.getProperty("user.dir")
					+ this.templateDir;
			String html_targetPath = System.getProperty("user.dir")
					+ this.resultDir;
			String java_targetPath = System.getProperty("user.dir")
					+ this.resultDir + "\\"
					+ this.packageName.replace(".", "\\");
			System.out.println("开始生成页面文件......");
			//this.mergeHTML(sourcePath, html_targetPath);
			this.mergeJava(sourcePath, java_targetPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("生成HTML页面模板失败，失败原因如下:");
			e.printStackTrace();
		}
	}
}
