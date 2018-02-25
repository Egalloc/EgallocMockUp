import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class APICommunicator {
    private String keyword;
    private List<BufferedImage> images = new ArrayList<>();


    public List<BufferedImage> getImages() {
        return images;
    }
    // Getter
    public final String getKeyword() {
        return keyword;
    }

    public APICommunicator(String keyword) throws IOException {
        this.keyword = keyword;

        // Request 4 times to get response
        for (int i = 0; i < 4; i++) {
            sendRequestForKeyWord(i * 10 + 1);
        }
    }

    // This method send request for keyword to the Google Custom Search API and retrieve 10 images back.
    private void sendRequestForKeyWord(int startIndex) throws IOException{
        // Create Query
        String url = "https://www.googleapis.com/customsearch/v1";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", keyword);
        parameters.put("key", "AIzaSyDaJ74IGt2X5miRWhriFOImLkBSo1G_dNw");
        parameters.put("cx", "003668417098658282383:2ym3vezfm44");
        parameters.put("start", Integer.toString(startIndex));
        parameters.put("searchType", "image");
        String query = ParameterStringBuilder.getParamsString(parameters);

        HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        InputStream responses = connection.getInputStream();

        parseResponseForImages(responses);

    }

    // This method extract image from response and add to the array
    private void parseResponseForImages(InputStream response) throws IOException {
        // Check NPE
        if (response != null) {
            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();

                // parse the JSON
                Gson gson = new Gson();
                APIResponse apiResponse = gson.fromJson(responseBody, APIResponse.class);
                if (apiResponse.getItems() != null) {
                    for (Item item : apiResponse.getItems()) {
                        if (images.size() == 30) {
                            break;
                        }
                        System.out.println("Url is " + item.getLink());


                        final URL url = new URL(item.getLink());

                        final HttpURLConnection connection = (HttpURLConnection) url
                                .openConnection();
                        connection.setRequestProperty(
                                "User-Agent",
                                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

                        BufferedImage image = null;
                        try {
                            image = ImageIO.read(url.openStream());
                        } catch (IOException e) {
                            continue;
                        }

                        if (image != null) {
                            images.add(image);
                        }
                    }
                }
            }
        }
    }


}
