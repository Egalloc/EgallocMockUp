import com.google.gson.Gson;
import sun.util.resources.cldr.fr.CalendarData_fr_BL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class APICommunicator {
    private String keyword;
    private List<BufferedImage> images = new Vector<>(); // needs to be thread-safe
    private BlockingDeque<URL> urls = new LinkedBlockingDeque<>();

    private static final int IMAGE_NUMBER = 30;
    private static final int API_REQUEST_THREAD_NUMBER = 4;
    private static final int URL_PROCESS_THREAD_NUMBER = 27;

    public APICommunicator(String keyword) {
        this.keyword = keyword;

        ExecutorService executor = Executors.newFixedThreadPool(32);

        for (int i = 0; i < API_REQUEST_THREAD_NUMBER; i++) {
            executor.submit(makeRequestRunnable(i));
        }

        for (int i = 0; i < URL_PROCESS_THREAD_NUMBER ; i++) {
            executor.submit(makeUrlProcessorRunnable());
        }

        while (images.size() < IMAGE_NUMBER) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        executor.shutdownNow();

        while (images.size() > 30) {
            images.remove(images.size() - 1);
        }
    }

    private Runnable makeUrlProcessorRunnable() {
        return () -> {
            while (true) {
                try {
                    processUrlForImage(urls.takeFirst());
                } catch (InterruptedException e) {
                }
            }
        };
    }

    private Runnable makeRequestRunnable(int startIndex) {
        return () -> sendRequestForKeyWord(startIndex * 10 + 1);
    }

    // This method send request for keyword to the Google Custom Search API and retrieve 10 images back.
    private void sendRequestForKeyWord(int startIndex) {
        // Create Query
        String url = "https://www.googleapis.com/customsearch/v1";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", keyword);
        parameters.put("key", "AIzaSyDaJ74IGt2X5miRWhriFOImLkBSo1G_dNw");
        parameters.put("cx", "003668417098658282383:2ym3vezfm44");
        parameters.put("start", Integer.toString(startIndex));
        parameters.put("searchType", "image");

        try {
            String query = ParameterStringBuilder.getParamsString(parameters);

            HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            InputStream response = connection.getInputStream();


            if (response == null) return;

            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();

                // parse the JSON
                Gson gson = new Gson();
                processAPIResponse(gson.fromJson(responseBody, APIResponse.class));
            }
        } catch (IOException e) {
        }
    }

    // This method extract image from response and add to the array
    private void processAPIResponse(APIResponse apiResponse) throws IOException {
        // Check NPE
        if (apiResponse.getItems() == null) return;

        for (Item item : apiResponse.getItems()) {

            System.out.println("Url is " + item.getLink());


            final URL url = new URL(item.getLink());

            urls.add(url);
        }
    }

    private void processUrlForImage(URL url) {

        try {
            final HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

            BufferedImage image = ImageIO.read(url.openStream());

            if (image != null) {
                images.add(image);
            }

        } catch (IOException e) {
        }

    }


    public List<BufferedImage> getImages() {
        return images;
    }

    // Getter
    public final String getKeyword() {
        return keyword;
    }
}
