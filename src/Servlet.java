import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        for (int i = 0; i < 3; i++) {
            String url = "https://www.googleapis.com/customsearch/v1";

            Map<String, String> parameters = new HashMap<>();
            parameters.put("q", request.getParameter("topic"));
            parameters.put("key", "AIzaSyDaJ74IGt2X5miRWhriFOImLkBSo1G_dNw");
            parameters.put("cx", "003668417098658282383:2ym3vezfm44");
            parameters.put("start", Integer.toString(i * 10 + 1));
            parameters.put("imgSize", "medium");
            parameters.put("searchType", "image");
            parameters.put("fileType", "png");
            String query = ParameterStringBuilder.getParamsString(parameters);

            HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            InputStream responses = connection.getInputStream();

            try (Scanner scanner = new Scanner(responses)) {
                String responseBody = scanner.useDelimiter("\\A").next();

                //System.out.println(responseBody);
                Gson gson = new Gson();
                APIResponse apiResponse = gson.fromJson(responseBody, APIResponse.class);
                for (Item item : apiResponse.getItems()) {
                    System.out.println("Link: " + item.getLink());
                    System.out.println("Height: " + item.getImage().getHeight());
                    System.out.println("Width: " + item.getImage().getWidth());
                    System.out.println("Thumbnail: " + item.getImage().getThumbnailLink());
                    System.out.println("ThumbnailHeight: " + item.getImage().getThumbnailHeight());
                    System.out.println("ThumbnailWidth: " + item.getImage().getThumbnailWidth());
                    System.out.println();
                }
            }
        }


    }

}
