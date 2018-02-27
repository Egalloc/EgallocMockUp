import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CollageBuilder {
    private List<BufferedImage> images = new ArrayList<>();
    private static final int IMAGE_NUMBER = 30;
    private static final double TOTAL_AREA_RATIO = 1.5;
    private static final int BORDER_PIXEL = 3;
    private static final int PADDING_X = 100;
    private static final int PADDING_Y = 50;
    private static final int MAXIMUM_ANGLE = 45;
    private static final int MINIMUM_ANGLE = -45;
    private static final int MINIMUM_ANGLE_FOR_FIRST_IMAGE = 5;
    private static final int IMAGE_PER_ROW = 10;
    private static final int SECOND_ROW_INDEX = IMAGE_PER_ROW + 1;
    private static final int THIRD_ROW_INDEX = IMAGE_PER_ROW * 2 + 1;

    CollageBuilder(List<BufferedImage> imagesWithoutBorder) {
        for (BufferedImage image : imagesWithoutBorder) {
            BufferedImage borderedVersion = addBorder(image);
            images.add(borderedVersion);
        }
    }

    public void createCollageWithImages(int width, int height) {
        BufferedImage collageSpace = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = collageSpace.createGraphics();

        PriorityQueue<Double> angles = generateAngles();

        g.setColor(Color.white);
        int numPixels = (int) (width * height * TOTAL_AREA_RATIO + 1);

        for (int i = 0; i < IMAGE_NUMBER; i++) {

            int newWidth = (int) Math.sqrt(numPixels/(IMAGE_NUMBER-i));
            int newHeight = (int) Math.sqrt(numPixels/(IMAGE_NUMBER-i));
            double angle = Math.toRadians(angles.poll());

            System.out.println("New width for this image " + i + "is " + newWidth);
            System.out.println("New height for this image " + i + "is " + newHeight);
            System.out.println("Remaining pixels: " + numPixels);


            // Draw the first image
            if(i == 0) {
                drawFirstImage(images.get(i), angle, g, width, height);
                System.out.println("The min angle:" + angle);
                int targetWidth = (int)(width * Math.abs(Math.cos(angle)) +  height * Math.abs(Math.sin(angle)) + 1);
                int targetHeight = (int)(width * Math.abs(Math.sin(angle)) + height * Math.abs(Math.cos(angle)) + 1);
                System.out.println("Target widith is " + targetWidth);
                System.out.println("Target height is " + targetHeight);
                numPixels = numPixels - targetWidth * targetHeight;
            }

            else if (i  < SECOND_ROW_INDEX) {
                AffineTransform original = g.getTransform();
                g.rotate(angle, PADDING_X + (i - 1) * (width - PADDING_X * 2) / IMAGE_PER_ROW, PADDING_Y);
                BufferedImage filler = resize(images.get(i), newWidth, newHeight);

                drawImageWithCoordinate(PADDING_X + (i - 1) * (width - PADDING_X * 2) / IMAGE_PER_ROW, PADDING_Y, g, filler);

                numPixels = numPixels - newWidth*newHeight;
                g.setTransform(original);
            }

            else if (i < THIRD_ROW_INDEX) {
                AffineTransform original = g.getTransform();
                g.rotate(angle, PADDING_X + (i - SECOND_ROW_INDEX) * (width - PADDING_X * 2) / IMAGE_PER_ROW, PADDING_Y + (height - PADDING_Y * 2) / 3);
                BufferedImage filler = resize(images.get(i),newWidth, newHeight);

                drawImageWithCoordinate(PADDING_X + (i - SECOND_ROW_INDEX) * (width - PADDING_X * 2) / IMAGE_PER_ROW,PADDING_Y + (height - PADDING_Y * 2) / 3, g, filler);


                numPixels = numPixels - newWidth * newHeight;
                g.setTransform(original);
            } else {
                AffineTransform original = g.getTransform();
                g.rotate(angle, PADDING_X + (i - THIRD_ROW_INDEX) * (width - PADDING_X * 2) / 10, PADDING_Y + (height - PADDING_Y * 2) * 2 / 3);
                BufferedImage filler = resize(images.get(i),newWidth, newHeight);

                drawImageWithCoordinate(PADDING_X + (i - THIRD_ROW_INDEX) * (width - PADDING_X * 2) / 10,PADDING_Y + (height - PADDING_Y * 2) * 2 / 3, g, filler);

                numPixels = numPixels - newWidth * newHeight;
                g.setTransform(original);
            }

        }

        // TODO: delete this later when everything is checked perfectly
        try {
            File file = new File("/Users/zifanshi/Desktop/Collage.png");
            ImageIO.write(collageSpace, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawImageWithCoordinate(int x, int y, Graphics2D g, BufferedImage bi) {
        g.drawImage(bi, x, y,null);
    }

    // This method adds border to the image
    private BufferedImage addBorder(BufferedImage image){
        //create a new image
        BufferedImage borderedImage = new BufferedImage(image.getWidth() + BORDER_PIXEL * 2,image.getHeight() + BORDER_PIXEL * 2,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = borderedImage.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0, borderedImage.getWidth(), borderedImage.getHeight());

        //draw the old one on the new one
        g.drawImage(image,BORDER_PIXEL,BORDER_PIXEL,null);
        return borderedImage;
    }

    // This resize an image
    private BufferedImage resize(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return image;
    }

    // This method draws the first image and fill up the whole space
    private void drawFirstImage(BufferedImage image, double angle, Graphics2D g, int collageWidth,
                               int collageHeight){
        AffineTransform original = g.getTransform();
        g.rotate(angle, collageWidth / 2, collageHeight / 2);
        int targetWidth = (int)(collageWidth * Math.cos(angle) + Math.abs(collageHeight * Math.sin(angle)) + 1);
        int targetHeight = (int)(collageWidth * Math.abs(Math.sin(angle)) + collageHeight * Math.cos(angle) + 1);
        image = resize(image, targetWidth, targetHeight);
        g.drawImage(image,  collageWidth / 2 - image.getWidth() / 2,  collageHeight / 2 - image.getHeight() / 2, null);
        g.setTransform(original);
    }

    // This method generates a min heap of random angles. It is compared by absolute values
    private PriorityQueue<Double> generateAngles()
    {
        PriorityQueue<Double> angles = new PriorityQueue<>(IMAGE_NUMBER, Comparator.comparingDouble(Math::abs));

        Random rand = new Random();

        while(true)
        {
            angles.clear();
            for(int i = 0; i < IMAGE_NUMBER; i++)
            {
                double randomAngle = MINIMUM_ANGLE + (MAXIMUM_ANGLE - MINIMUM_ANGLE) * rand.nextDouble();
                angles.add(randomAngle);
            }

            if (Math.abs(angles.peek()) <= MINIMUM_ANGLE_FOR_FIRST_IMAGE) break;
        }
        return angles;
    }


}
