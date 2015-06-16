package com.iskyshop.view.web.tools;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.ISysConfigService;

@Component
public class ImageViewTools {
	@Autowired
	private ISysConfigService configService;

	public String random_login_img() {
		String img = "";
		SysConfig config = this.configService.getSysConfig();
		if (config.getLogin_imgs().size() > 0) {
			Random random = new Random();
			Accessory acc = config.getLogin_imgs().get(
					random.nextInt(config.getLogin_imgs().size()));
			img = acc.getPath() + "/" + acc.getName();
		} else {
			img = "resources/style/common/images/login_img.jpg";
		}
		return img;
	}
}
