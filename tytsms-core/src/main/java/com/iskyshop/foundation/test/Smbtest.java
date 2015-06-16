package com.iskyshop.foundation.test;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

public class Smbtest {
	/**
	 * 从局域网中共享文件中得到文件并保存在本地磁盘上
	 * 
	 * @param remoteUrl
	 *            共享电脑路径 如：smb//administrator:123456@172.16.10.136/smb/1221.zip ,
	 *            smb为共享文件 注：如果一直出现连接不上，有提示报错，并且错误信息是 用户名活密码错误 则修改共享机器的文件夹选项 查看
	 *            去掉共享简单文件夹的对勾即可。
	 * @param localDir
	 *            本地路径 如：D:/
	 */
	public static void smbGet(String remoteUrl,String localDir){
		InputStream in = null;
		OutputStream out = null;
		try {
			SmbFile smbFile = new SmbFile(remoteUrl);
			String fileName = smbFile.getName();
			File localFile = new File(localDir+File.separator+fileName);
			in = new BufferedInputStream(new SmbFileInputStream(smbFile));
			out = new BufferedOutputStream(new FileOutputStream(localFile));
			byte []buffer = new byte[1024];
			while((in.read(buffer)) != -1){
				out.write(buffer);
				buffer = new byte[1024];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 把本地磁盘中的文件上传到局域网共享文件下
	 * 
	 * @param remoteUrl
	 *            共享电脑路径 如：smb//administrator:123456@172.16.10.136/smb
	 * @param localFilePath
	 *            本地路径 如：D:/
	 */
	public static void smbPut(String remoteUrl,String localFilePath){
		InputStream in = null;
		OutputStream out = null;
		try {
			File localFile = new File(localFilePath);
			String fileName = localFile.getName();
			SmbFile remoteFile = new SmbFile(remoteUrl+"/"+fileName);
			in = new BufferedInputStream(new FileInputStream(localFile));
			out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
			byte []buffer = new byte[1024];
			while((in.read(buffer)) != -1){
				out.write(buffer);
				buffer = new byte[1024];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		smbPut("smb://administrator:123456@192.168.1.102/smb", "E:/公司资料/Com.zip");
		// smbGet("smb://administrator:123456@192.168.1.102/smb/1221.zip",
		// "D:/");

	}

}

