package com.iskyshop.foundation.test;

import java.util.Date;

import com.easyjf.util.MD5;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;

public class TestMd5 {
	public static void main(String[] args) {
		 String url = "";
		 String userid = "A943753";
		 String userpws = "8242e4712c4b4c9a0e9906da9816a7d1";
		 String cardid = "140101";
		 String cardnum = "100";
		 String sporder_id = "0001";
		 String sporder_time = CommUtil.formatTime("yyyyMMddHHmmss", new
		 Date());
		 String game_userid = "15309882892";
		 String md5_str = Md5Encrypt.md5(userid + userpws + cardid + cardnum
		 + sporder_id + sporder_time + game_userid + "OFCARD").toUpperCase();
		 String ret_url = "";
		 String version = "6.0";
		 url = "http://api2.ofpay.com/onlineorder.do?userid=" + userid
		 + "&userpws=" + userpws + "&cardid=" + cardid + "&cardnum="
		 + cardnum + "&sporder_id=" + sporder_id + "&sporder_time="
		 + sporder_time + "&game_userid=" + game_userid + "&md5_str="
		 + md5_str + "&ret_url=" + ret_url + "&version=" + version;
		 System.out.println(url);
	}
}
