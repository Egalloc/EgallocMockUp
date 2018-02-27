package Server;

import com.google.gson.Gson;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class APICommunicator {
    private String keyword;
    private List<BufferedImage> images = new Vector<BufferedImage>() {
        @Override
        public boolean add(BufferedImage image) {
            return size() < IMAGE_NUMBER && super.add(image);
        }
    }; // needs to be thread-safe
    private BlockingDeque<URL> urls = new LinkedBlockingDeque<>();

    private static final int IMAGE_NUMBER = 30;
    private static final int API_REQUEST_THREAD_NUMBER = 4;
    private static final int URL_PROCESS_THREAD_NUMBER = 36;
    private static final int URL_TIMEOUT_LIMIT = 5000;
    private static final int THREAD_NUMBER = 40;
    private static final int SLEEP_TIME = 1000;
    private static final int ELEMENT_PER_REQUEST = 10;
    private static final int SLEEP_TIME_OUT = 10;
    private static final String URL_REQUEST_PROPERTY = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) " +
            "AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31";


    public APICommunicator(String keyword) {
        this.keyword = keyword;

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

        for (int i = 0; i < API_REQUEST_THREAD_NUMBER; i++) {
            executor.submit(makeRequestRunnable(i));
        	//sendRequestForKeyWord(i * ELEMENT_PER_REQUEST + 1);
        }

        for (int i = 0; i < URL_PROCESS_THREAD_NUMBER; i++) {
            executor.submit(makeUrlProcessorRunnable());
        	//processUrlForImage(urls.poll());
        }

        int sleepTime = 0;
        
        while (images.size() < IMAGE_NUMBER && sleepTime < SLEEP_TIME_OUT) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {

            }
            System.out.println("sleeping");
            sleepTime++;
        }
        
        System.out.println("shutdown");
        System.out.println("Image size is " + images.size() );
        executor.shutdownNow();

    }

    // This change the processor to a runnable
    private Runnable makeUrlProcessorRunnable() {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    processUrlForImage(urls.takeFirst());
                } catch (InterruptedException e) {
                    System.out.println("Current Thread is" + Thread.currentThread().getName());
                    System.out.println("Current Thread is doing" + Thread.currentThread().getName());
                    return;
                }
            }
        };
    }

    // This change the request to a runnable
    private Runnable makeRequestRunnable(int startIndex) {
        return () -> sendRequestForKeyWord(startIndex * ELEMENT_PER_REQUEST + 1);
    }

    // This method send request for keyword to the Google Custom Search API and retrieve 10 images back.
    private void sendRequestForKeyWord(int startIndex) {
        // Create Query
        String url = "https://www.googleapis.com/customsearch/v1";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", keyword);
        parameters.put("key", "AIzaSyBjefw6KjolHODgNI2IZhvYh7jzoSzG44A");
        parameters.put("cx", "010274188002515510907:oecl9salb6i");
        parameters.put("start", Integer.toString(startIndex));
        parameters.put("searchType", "image");

        try {
            String query = ParameterStringBuilder.getParamsString(parameters);

            HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setConnectTimeout(URL_TIMEOUT_LIMIT);

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

    // This method extract image from response and add to the blocking dequeue
    private void processAPIResponse(APIResponse apiResponse) {
        // Check NPE
        if (apiResponse.getItems() == null) return;

        for (Item item : apiResponse.getItems()) {

            System.out.println("Url is " + item.getLink());

            try {
                final URL url = new URL(item.getLink());
                urls.add(url);
            } catch (MalformedURLException e) {
            }
        }
    }

    // Process url and generate image and add to the vector
    private void processUrlForImage(URL url) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty(
                    "User-Agent", URL_REQUEST_PROPERTY
                    );

            connection.setConnectTimeout(URL_TIMEOUT_LIMIT);
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
}
