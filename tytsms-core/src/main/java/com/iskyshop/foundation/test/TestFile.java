package com.iskyshop.foundation.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.database.AppendMessage;

public class TestFile {
	public static void main(String[] args) {
		int count=1;
		String name="book";
		float psize=30;
		File file = new File("D:\\book" + File.separator + name + "_" + count
				+ ".txt");
		PrintWriter pwrite;
		try {
			pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), "UTF-8"), true);
			for(int i=1;i<100000;i++){
				double fsize = CommUtil.div(file.length(), 1024);
				if (fsize > psize) {
					pwrite.flush();
					//pwrite.close();
					count++;
					file = new File("D:\\book" + File.separator + name + "_"
							+ count + ".txt");
					pwrite = new PrintWriter(new OutputStreamWriter(
							new FileOutputStream(file, true), "UTF-8"),
							true);
  				}
				pwrite.println("THIS LINE IS:"+i+",THIS IS A BOOK THIS IS A BOOK THIS IS A BOOK");
			}
			pwrite.flush();
			pwrite.close();
			System.out.println("文件输出完毕");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
