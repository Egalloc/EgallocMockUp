import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
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
        BufferedImage collageSpace = new BufferedImage(1600,1200,BufferedImage.TYPE_INT_RGB);
        Graphics g = collageSpace.getGraphics();
        g.setColor(Color.white);

        for (int i = 0; i < 30; i++) {
            if (i < 10) {
                g.drawImage(images.get(i), i * 160, 0, null);
            }
            else if (i < 20) {
                g.drawImage(images.get(i), (i - 10) * 160, 400, null);
            } else {
                g.drawImage(images.get(i), (i - 20) * 160, 800, null);
            }
        }

        try {
            File file = new File("/Users/zifanshi/Desktop/Collage.png");
            ImageIO.write(collageSpace, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage addBorder(BufferedImage image){
        //create a new image
        BufferedImage borderedImage = new BufferedImage(image.getWidth() + 6,image.getHeight() + 6,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = borderedImage.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0,borderedImage.getWidth(),borderedImage.getHeight());

        //draw the old one on the new one
        g.drawImage(image,3,3,null);
        return borderedImage;
    }


    public BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public BufferedImage rotate(BufferedImage im) {
        AffineTransform tx = new AffineTransform();
        tx.rotate(0.5, im.getWidth() / 2, im.getHeight() / 2);

        AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
        return op.filter(im, null);
    }


    public void DrawFirstImage(BufferedImage image, double angleInRadians, Graphics2D g, int collageWidth,
                                      int collageHeight){
        g.rotate(angleInRadians, collageWidth/2, collageHeight/2);
        int targetWidth = (int)(collageWidth * Math.cos(angleInRadians) + Math.abs(collageHeight * Math.sin(angleInRadians)) + 1);
        int targetHeight = (int)(collageWidth * Math.abs(Math.sin(angleInRadians)) + collageHeight * Math.cos(angleInRadians) + 1);
        image = resize(image, targetWidth, targetHeight);
        g.drawImage(image,  collageWidth/2 - image.getWidth()/2,  collageHeight/2 - image.getHeight()/2, null);
    }

}

