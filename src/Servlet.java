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
        APICommunicator apiCommunicator = new APICommunicator(request.getParameter("topic"));

        System.out.println("Size of image url arrays" + apiCommunicator.getImageFilePaths().size());

        CollageBuilder cb = new CollageBuilder(apiCommunicator.getImageFilePaths());

        cb.createCollageWithImages();
    }

}
