import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CollageBuilder {
    private List<String> imageFilePaths;
    private List<BufferedImage> images = new ArrayList<>();

    CollageBuilder(List<String> imageFilePaths) throws IOException {
        this.imageFilePaths = imageFilePaths;

        for (String urlString: imageFilePaths) {
            System.out.println(urlString);

            final URL url = new URL(urlString);
            final HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
            BufferedImage image = ImageIO.read(connection.getInputStream());

            BufferedImage borderedVersion = addBorder(image);
            images.add(borderedVersion);
        }
    }

    public void createCollageWithImages() {

    }

    private BufferedImage addBorder(BufferedImage image){
        BufferedImage borderedImage = new BufferedImage(image.getWidth() + 6,image.getHeight() + 6,
                BufferedImage.TYPE_INT_RGB);
        System.out.println("Adding borders...");
        Graphics g = borderedImage.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0,borderedImage.getWidth(),borderedImage.getHeight());

        g.drawImage(image,3,3,null);
        return borderedImage;
    }

}
