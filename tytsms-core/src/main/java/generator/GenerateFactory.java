package generator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author erikzhang www.iskyshop.com
 * @info 运行改方法生成系统模板文件
 * 
 */
public class GenerateFactory {
	/**
	 * @since1.0
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		// TODO Auto-generated method stub
		generSimple();
	}

	private static void generSimple() throws IOException, FileNotFoundException {
		String path = "package.properties";
		Properties properties = new Properties();
		properties.load(GenerateFactory.class
				.getResourceAsStream("package.properties"));
		List<String[]> argsList = new ArrayList<String[]>();
		for (Iterator it = properties.keySet().iterator(); it.hasNext();) {
			String domainName = (String) it.next();
			String packageName = properties.getProperty(domainName);
			String[] pair = new String[] { domainName, packageName };
			argsList.add(pair);
		}
		for (String[] arg : argsList) {
			// 根据类路径和生成的包前缀合成模板
			Generator gen=new Generator(arg);
			gen.generate();
		}
	}

	
}
