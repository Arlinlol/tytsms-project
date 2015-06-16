package com.iskyshop.foundation.test;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.iskyshop.core.tools.CommUtil;

public class ImageIOTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String source = "F://fea37b8c-6813-40c6-ad8b-95c47b1ba680.jpg";
		String target = "F://fea37b8c-6813-40c6-ad8b-95c47b1ba680.jpg_small.jpg";
		String target1 = "F://fea37b8c-6813-40c6-ad8b-95c47b1ba680.jpg_small1.jpg";
		CommUtil.createSmall(source, target, 160, 160);

	}
}
