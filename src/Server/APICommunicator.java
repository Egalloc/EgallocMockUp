package Server;

import com.google.gson.Gson;

import data.Constants;

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
	// This is the keyword that we are searching for.
    private String keyword;

    // The images vector's add method are overridden, so that it won't exceed IMAGE_NUMBER images.
    // And it needs to be thread-safe. So we use vector.
    private List<BufferedImage> images = new Vector<BufferedImage>() {
        @Override
        public boolean add(BufferedImage image) {
            return size() < Constants.API_IMAGE_NUMBER && super.add(image);
        }
    };
    // This is the blockingDeque which holds all the URLs
    private BlockingDeque<URL> urls = new LinkedBlockingDeque<>();

    /*
     * This is the constructor of the APICommunicator, it takes in one keyword and request Google Custom Search API
     * for result. It will add the result images to the image vector. Then the Servlet will call get images to get the
     * BufferedImages.
     */
    public APICommunicator(String keyword) {
        this.keyword = keyword;

        System.out.println("Searching for" + keyword);
        // Create an ExecutorService for the THREAD_NUBMER threads
        ExecutorService executor = Executors.newFixedThreadPool(Constants.THREAD_NUMBER);

        // Assign API_REQUEST_THREAD_NUBMER threads to make API request
        for (int i = 0; i < Constants.API_REQUEST_THREAD_NUMBER; i++) {
            executor.submit(makeRequestRunnable(i));
        }

        // Assign URL_PROCESS_THREAD_NUMBER threads to download the urls into Bufferimages.
        for (int i = 0; i < Constants.URL_PROCESS_THREAD_NUMBER; i++) {
            executor.submit(makeUrlProcessorRunnable());
        }

        /*
         * Let the main thread count the number of image, if the number of image is not 
         * enough, make the thread sleep for SLEEP_TIME. If the number of image reaches
         * IMAGE_NUMBER, break out the loop and shut down all the thread.
         * Also set a SLEEP_TIME_OUT limit, so that if an error occur, it will not sleep 
         * forever.
         */
        int sleepTime = 0;
        while (images.size() < Constants.API_IMAGE_NUMBER && sleepTime < Constants.SLEEP_TIME_OUT) {
            try {
                Thread.sleep(Constants.SLEEP_TIME);
            } catch (InterruptedException e) {
            }
            sleepTime++;
        }
        // Shut down all the threads.
        executor.shutdownNow();

    }

    // This change the processor to a runnable for multi-threading purpose
    private Runnable makeUrlProcessorRunnable() {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    processUrlForImage(urls.takeFirst());
                } catch (InterruptedException e) {
                    return;
                }
            }
        };
    }

    // This change the request to a runnable for multi-threading purpose
    private Runnable makeRequestRunnable(int startIndex) {
        return () -> sendRequestForKeyWord(startIndex * Constants.ELEMENT_PER_REQUEST + 1);
    }

    // This method send request for keyword to the Google Custom Search API and retrieve 10 images back.
    private void sendRequestForKeyWord(int startIndex) {

        // Create Query
        String url = Constants.REQUEST_URL;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", keyword);
        parameters.put("key", Constants.API_KEY);
        parameters.put("cx", Constants.CX);
        parameters.put("start", Integer.toString(startIndex));
        parameters.put("searchType",Constants.SEARCH_TYPE);

        // Request the API using the query
        try {
            String query = ParameterStringBuilder.getParamsString(parameters);

            HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
            connection.setRequestProperty(Constants.API_REQUEST_PROPERTY_KEY, Constants.API_REQUEST_PROPERTY_VALUE);

            connection.setConnectTimeout(Constants.URL_TIMEOUT_LIMIT);

            InputStream response = connection.getInputStream();

            // No response.
            if (response == null) {
                System.out.println("NO response");
                return;
            }

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

        // Add URLs to the blockingdeque
        for (Item item : apiResponse.getItems()) {
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
            connection.setRequestProperty(Constants.URL_REQUEST_PROPERTY_KEY, Constants.URL_REQUEST_PROPERTY_VALUE);

            connection.setConnectTimeout(Constants.URL_TIMEOUT_LIMIT);
            BufferedImage image = ImageIO.read(url.openStream());

            if (image != null) {
                images.add(image);
            }

        } catch (IOException e) {
        }

    }

    // The getter to get the images
    public List<BufferedImage> getImages() {
        return images;
    }
}
