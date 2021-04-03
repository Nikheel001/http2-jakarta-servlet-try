package http2servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * 
 * @author nikheel.patel
 *
 */
public class ImageHelper {

	public static void divide(int x, int y, String image, List<BufferedImage> parts) {
		try {
			BufferedImage base = ImageIO.read(new File(image));
			int h, w;
			h = base.getHeight();
			w = base.getWidth();

			int newH = h / x, newW = w / y;

			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					parts.add(base.getSubimage(j * newW, i * newH, newW, newH));
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}