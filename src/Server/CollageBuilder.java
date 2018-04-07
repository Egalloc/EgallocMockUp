package Server;

import javax.imageio.ImageIO;

import data.Constants;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CollageBuilder {
    private List<BufferedImage> images;

    CollageBuilder(List<BufferedImage> images) {
        this.images = images;
    }

    public BufferedImage createCollageWithImages(int width, int height) {
        BufferedImage collageSpace = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = collageSpace.createGraphics();

        PriorityQueue<Double> angles = generateAngles();

        g.setColor(Color.white);
        int numPixels = (int) (width * height * Constants.TOTAL_AREA_RATIO + 1);

        for (int i = 0; i < Constants.CB_IMAGE_NUMBER; i++) {

            int newWidth = (int) Math.sqrt(numPixels/(Constants.CB_IMAGE_NUMBER-i));
            int newHeight = (int) Math.sqrt(numPixels/(Constants.CB_IMAGE_NUMBER-i));
            double angle = Math.toRadians(angles.poll());

            // Draw the first image and make it as a background
            if(i == 0) {
                drawFirstImage(images.get(i), angle, g, width, height);
                int targetWidth = (int)(width * Math.abs(Math.cos(angle)) +  height * Math.abs(Math.sin(angle)) + 1);
                int targetHeight = (int)(width * Math.abs(Math.sin(angle)) + height * Math.abs(Math.cos(angle)) + 1);
                numPixels = numPixels - targetWidth * targetHeight;
            }

            // Draw the first row of images
            else if (i  < Constants.SECOND_ROW_INDEX) {
                AffineTransform original = g.getTransform();
                int xCoordinate = Constants.PADDING_X + (i - 1) * (width - Constants.PADDING_X * 2) / Constants.IMAGE_PER_ROW;
                int yCoordinate = Constants.PADDING_Y;
                g.rotate(angle, xCoordinate , yCoordinate);
                BufferedImage filler = resize(images.get(i), newWidth, newHeight);
                filler = addBorder(filler);
                
                g.drawImage(filler,xCoordinate,yCoordinate,null); 	
                numPixels = numPixels - newWidth*newHeight;
                g.setTransform(original);
            }

            else if (i < Constants.THIRD_ROW_INDEX) {
                AffineTransform original = g.getTransform();
                int xCoordinate = Constants.PADDING_X + (i - Constants.SECOND_ROW_INDEX) * (width - Constants.PADDING_X * 2) / Constants.IMAGE_PER_ROW;
                int yCoordinate = Constants.PADDING_Y + (height - Constants.PADDING_Y * 2) / Constants.NUMBER_OF_ROW;
                g.rotate(angle, xCoordinate, yCoordinate);
                BufferedImage filler = resize(images.get(i),newWidth, newHeight);
                filler = addBorder(filler);

                g.drawImage(filler,xCoordinate,yCoordinate,null);
                numPixels = numPixels - newWidth * newHeight;
                g.setTransform(original);
            } else {
                AffineTransform original = g.getTransform();
                int xCoordinate = Constants.PADDING_X + (i - Constants.THIRD_ROW_INDEX) * (width - Constants.PADDING_X * 2) / Constants.IMAGE_PER_ROW;
                int yCoordinate = Constants.PADDING_Y + (height - Constants.PADDING_Y * 2) * 2 / Constants.NUMBER_OF_ROW;
                g.rotate(angle, xCoordinate, yCoordinate);
                BufferedImage filler = resize(images.get(i),newWidth, newHeight);
                filler = addBorder(filler);

                g.drawImage(filler, xCoordinate, yCoordinate,null);
                numPixels = numPixels - newWidth * newHeight;
                g.setTransform(original);
            }

        }
        
        return collageSpace;
    }

    // This method adds border to the image
    private BufferedImage addBorder(BufferedImage image){
        //create a new image
        BufferedImage borderedImage = new BufferedImage(image.getWidth() + Constants.BORDER_PIXEL * 2,image.getHeight() + Constants.BORDER_PIXEL * 2,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = borderedImage.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0, borderedImage.getWidth(), borderedImage.getHeight());

        //draw the old one on the new one
        g.drawImage(image,Constants.BORDER_PIXEL,Constants.BORDER_PIXEL,null);
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
        image = addBorder(image);
        g.drawImage(image,  collageWidth / 2 - image.getWidth() / 2,  collageHeight / 2 - image.getHeight() / 2, null);
        g.setTransform(original);
    }

    // This method generates a min heap of random angles. It is compared by absolute values
    private PriorityQueue<Double> generateAngles()
    {
        PriorityQueue<Double> angles = new PriorityQueue<>(Constants.CB_IMAGE_NUMBER, Comparator.comparingDouble(Math::abs));

        Random rand = new Random();

        while(true)
        {
            angles.clear();
            for(int i = 0; i < Constants.CB_IMAGE_NUMBER; i++)
            {
                double randomAngle = Constants.MINIMUM_ANGLE + (Constants.MAXIMUM_ANGLE - Constants.MINIMUM_ANGLE) * rand.nextDouble();
                angles.add(randomAngle);
            }

            if (Math.abs(angles.peek()) <= Constants.MINIMUM_ANGLE_FOR_FIRST_IMAGE) break;
        }
        return angles;
    }


}
