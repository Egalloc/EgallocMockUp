import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CollageBuilder {
    private List<BufferedImage> images = new ArrayList<>();

    CollageBuilder(List<BufferedImage> images) throws IOException {
        for (BufferedImage image : images) {
            BufferedImage borderedVersion = addBorder(image);
            this.images.add(borderedVersion);
        }
    }

    public void createCollageWithImages(int width, int height) {
        BufferedImage collageSpace = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = collageSpace.createGraphics();
        ArrayList<Double> angles = GenerateAngles();
        g.setColor(Color.white);
        int numPixels = (int) (width*height*1.5+1);

        for (int i = 0; i < 30; i++) {
            int newWidth = (int) Math.sqrt(numPixels/(30-i));
            int newHeight = (int) Math.sqrt(numPixels/(30-i));
            System.out.println("New width for this image " + i + "is " + newWidth);
            System.out.println("New height for this image " + i + "is " + newHeight);
            System.out.println("Remaining pixels: " + numPixels);
            if(i == 0) {
                DrawFirstImage(images.get(i), angles.get(29), g, width, height);
                System.out.println("The min angle:" + angles.get(29));
                double angle = Math.toRadians(angles.get(29));
                int targetWidth = (int)(width * Math.cos(angle) + Math.abs(height * Math.sin(angle) + 1));
                int targetHeight = (int)(width * Math.abs(Math.sin(angle)) + height * Math.cos(angle) + 1);
                System.out.println("Target widith is " + targetWidth);
                System.out.println("Target height is " + targetHeight);
                numPixels = numPixels - targetWidth*targetHeight;
            }
            else if (i  < 11) {
                System.out.println("Angle for this image" + (i-1) + " is" + angles.get(i-1));
                double angle = Math.toRadians(angles.get(i-1));
                AffineTransform original = g.getTransform();
                g.rotate(angle, width/2, height/2);
                BufferedImage filler = resize(images.get(i),newWidth, newHeight);
                g.drawImage(filler,100 + (i - 1) * (width - 200) / 10, 50, null);
                numPixels = numPixels - newWidth*newHeight;
                g.setTransform(original);
            }

            else if (i < 21) {
                AffineTransform original = g.getTransform();
                g.rotate(Math.toRadians(angles.get(i-1)), width/2, height/2);
                BufferedImage filler = resize(images.get(i),newWidth, newHeight);
                g.drawImage(filler, 100 + (i - 11) * (width - 200) / 10, 50 + (height - 100) / 3 , null);
                numPixels = numPixels - newWidth*newHeight;
                g.setTransform(original);
            } else {
                AffineTransform original = g.getTransform();
                g.rotate(Math.toRadians(angles.get(i-1)), width/2, height/2);
                BufferedImage filler = resize(images.get(i),newWidth, newHeight);
                g.drawImage(filler, 100 + (i - 21) * (width - 200) / 10, 50 + (height - 100) * 2 / 3 , null);
                numPixels = numPixels - newWidth*newHeight;
                g.setTransform(original);
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


    public void DrawFirstImage(BufferedImage image, double angle, Graphics2D g, int collageWidth,
                               int collageHeight){
        double angleInRadians = Math.toRadians(angle);
        AffineTransform original = g.getTransform();
        g.rotate(angleInRadians, collageWidth/2, collageHeight/2);
        int targetWidth = (int)(collageWidth * Math.cos(angleInRadians) + Math.abs(collageHeight * Math.sin(angleInRadians)) + 1);
        int targetHeight = (int)(collageWidth * Math.abs(Math.sin(angleInRadians)) + collageHeight * Math.cos(angleInRadians) + 1);
        image = resize(image, targetWidth, targetHeight);
        g.drawImage(image,  collageWidth/2 - image.getWidth()/2,  collageHeight/2 - image.getHeight()/2, null);
        g.setTransform(original);
    }
    public static ArrayList<Double> GenerateAngles()
    {
        ArrayList<Double> angles = new ArrayList<Double>();

        double minRange = -45;
        double maxRange = 45;
        Boolean validAngles = false;
        double angleForFirstImage = 0;

        Random rand = new Random();

        while(!validAngles)
        {
            angles.clear();
            for(int i=0; i<30; i++)
            {
                double randomAngle = minRange + (maxRange - minRange) * rand.nextDouble();
                angles.add(randomAngle);
            }

            double minAngle = maxRange;
            int minAngleIdx = -1;
            for(int i=0; i<30; i++)
            {
                if(Math.abs(angles.get(i)) < minAngle)
                {
                    minAngle = Math.abs(angles.get(i));
                    minAngleIdx = i;
                }
            }


            if(Math.abs(angles.get(minAngleIdx)) <= 5)
            {
                angleForFirstImage = angles.get(minAngleIdx);
                angles.remove(minAngleIdx);
                angles.add(angleForFirstImage);
                validAngles = true;
            }
        }
        return angles;
    }
}
