package http2servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.PushBuilder;

/**
 * 
 * @author nikheel.patel
 *
 */
public class HelloWorldServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2468062941847233750L;
	
	//add base image directory path
	private static String dir = "";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");

		// step 1
		PushBuilder pb = req.newPushBuilder();
		List<BufferedImage> parts = new ArrayList<>();
		List<String> imagesPushed = new ArrayList<>();

		int partX, partY;
		partX = 12;
		partY = 10;
		String img_src = "/images";
		String name = "2years-gophers";
		String ext = "jpg";
		String base = img_src + "/" + name + "." + ext;

		ImageHelper.divide(partX, partY, dir + base, parts);

		String[] arr = new String[7];
		String pathToWrite;
		int ctr = 0;
		arr[0] = img_src;
		arr[1] = "/";
		arr[2] = name;
		arr[3] = "_";
		arr[5] = ".";
		arr[6] = ext;

		for (BufferedImage i : parts) {
			arr[4] = String.valueOf(ctr++);
			pathToWrite = String.join("", arr);

			File f = new File(dir + pathToWrite);
			if (!f.exists()) {
				f.createNewFile();
				ImageIO.write(i, ext, f);
			}
			if (pb != null) {
				pb.path(pathToWrite);
			}
			imagesPushed.add(pathToWrite);
		}
		if (pb != null) {
			pb.push();
		}

		writeIt(req, resp, imagesPushed, pb != null, partY);
	}

	public void writeIt(HttpServletRequest req, HttpServletResponse resp, List<String> imagesPushed, boolean h2,
			int partY) throws IOException {
		PrintWriter pw = resp.getWriter();

		pw.println("<html>");
		pw.println("<body onload='showtimes()'>");
		if (h2) {
			pw.println("<h4>Http2 is working</h4>");
			pw.println("<p>The following image was provided via a push request.</p>");
		}
		int ctr = 0;
		pw.println("<p>");
		for (String i : imagesPushed) {
			if (ctr < partY) {
				ctr++;
			} else if (partY == ctr) {
				ctr = 1;
				pw.println("</br>");
			}
			pw.print("<img src=\"" + req.getContextPath() + "/" + i + "\"/>");
		}

		pw.println("<div id='loadtimes'></div></p>");
		pw.println("<script>");
		pw.println("function showtimes() {");
		pw.println("	var times = 'Times from connection start:<br>'");
		pw.println(
				"	times += 'DOM loaded: ' + (window.performance.timing.domContentLoadedEventEnd - window.performance.timing.connectStart) + 'ms<br>'");
		pw.println(
				"	times += 'DOM complete (images loaded): ' + (window.performance.timing.domComplete - window.performance.timing.connectStart) + 'ms<br>'");
		pw.println("	document.getElementById('loadtimes').innerHTML = times");
		pw.println("}");
		pw.println("</script>");

		pw.println("</body>");
		pw.println("</html>");
	}

}
