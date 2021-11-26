import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Objects;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/search"},loadOnStartup = 1)
public class Selvet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (Objects.equals(path, "/search")){ //cannot compare use '=='
            resp.setContentType("text/html");
            resp.getWriter().write("<h1>Hello, world!<h1>");
        }
        else resp.getWriter().write(path);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqBody=req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Gson gson = new Gson();
        Img img=gson.fromJson(reqBody,Img.class);

        String dbUrl = System.getenv("JDBC_DATABASE_URL");

        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (Exception e) {
        }

        Img img2=new Img();
        try {
            //img2.setUrl("1111");
            Connection con = DriverManager.getConnection(dbUrl);
            String sql = "SELECT * FROM imgs WHERE modality=?";
            PreparedStatement psmt = con.prepareStatement(sql);
            psmt.setString(1, img.getModality());
            ResultSet rs=psmt.executeQuery();
            while(rs.next()){
                img2.setId(rs.getInt("id"));
                img2.setModality(rs.getString("modality"));
                img2.setRegion(rs.getString("region"));
                img2.setUrl(rs.getString("url"));
            }
            rs.close();
            psmt.close();
            con.close();
        } catch (SQLException throwables) {
        }

        resp.setContentType("application/json");
        Gson gson2 = new Gson();
        String jsonString = gson2.toJson(img2);
        resp.getWriter().write(jsonString);
    }
}
