import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "WeatherServlet", value = "/weather")
public class WeatherServlet extends HttpServlet {
    private static final String API_KEY = "35b06fd54665ba499868e6987a064686";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String city = request.getParameter("city");
        if (city == null || city.isEmpty()) {
            response.getWriter().println("You need to input a city!");
            return;
        }

//        String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", city, API_KEY);
        String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s", city, API_KEY);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet req = new HttpGet(url);
            String jsonResponse = client.execute(req, httpResponse -> EntityUtils.toString(httpResponse.getEntity()));
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonResponse);

            JSONObject main = (JSONObject) json.get("main");
            JSONArray weatherArr = (JSONArray) json.get("weather");
            JSONObject weather = (JSONObject) weatherArr.get(0);
            Double temp = (Double) main.get("temp");
            String description = (String) weather.get("description");

            request.setAttribute("temp", temp);
            request.setAttribute("description", description);
            request.getRequestDispatcher("weather.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Failed to retrieve weather information.");
        }
    }
}