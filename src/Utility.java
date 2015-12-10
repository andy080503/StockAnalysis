import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class Utility {
	
	public static void downloadFile(URL url, String path, String fileName) {
		try {
			File dir = new File(path);
			FileUtils.forceMkdir(dir);
			File download = new File(path + fileName);
			FileUtils.copyURLToFile(url, download);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
