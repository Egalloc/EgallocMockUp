import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class APICommunicator {
    private String keyword;
    private List<String> imageFilePaths = new ArrayList<>();

    // Getter
    public final String getKeyword() {
        return keyword;
    }
    // Getter
    public final List<String> getImageFilePaths() {
        return imageFilePaths;
    }

    public APICommunicator(String keyword) throws IOException {
        this.keyword = keyword;

        // Request 3 times to get response
        for (int i = 0; i < 3; i++) {
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
        parameters.put("imgSize", "medium");
        parameters.put("searchType", "image");
        parameters.put("fileType", "png");
        String query = ParameterStringBuilder.getParamsString(parameters);

        HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        InputStream responses = connection.getInputStream();

        parseResponseForImages(responses);

    }

    // This method extract image from response and add to the array
    private void parseResponseForImages(InputStream response) {
        try (Scanner scanner = new Scanner(response)) {
            String responseBody = scanner.useDelimiter("\\A").next();

            //System.out.println(responseBody);
            Gson gson = new Gson();
            APIResponse apiResponse = gson.fromJson(responseBody, APIResponse.class);
            for (Item item : apiResponse.getItems()) {
                imageFilePaths.add(item.getLink());
            }
        }
    }


}
