import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        double start = System.currentTimeMillis();


        APICommunicator apiCommunicator = new APICommunicator(request.getParameter("topic"));

        System.out.println("Size of image  arrays" + apiCommunicator.getImages().size());

        System.out.println("Start time: " + start);

        CollageBuilder cb = new CollageBuilder(apiCommunicator.getImages());

        cb.createCollageWithImages(800,600);

        double duration = System.currentTimeMillis() - start;
        System.out.println("Duration: " + duration);
    }

}
