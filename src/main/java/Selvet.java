import com.google.gson.Gson;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.*;
import ij.process.ImageProcessor;
import ij.process.StackProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/search","/thumbnail","/img"},loadOnStartup = 1)
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
        String path = req.getServletPath();
        if (path.equals("/search")) {
            String reqBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Gson gson = new Gson();
            SearchInfo searchInfo = gson.fromJson(reqBody, SearchInfo.class);
            String dbUrl = System.getenv("JDBC_DATABASE_URL");

            String[] modality_a= searchInfo.getModality_a();
            String[] region_a= searchInfo.getRegion_a();
            String patient_name= searchInfo.getPatient_name();

            try {
                Class.forName("org.postgresql.Driver");
            } catch (Exception e) {
            }

            List<Img> img_l = new ArrayList<Img>();
            try {
                Connection con = DriverManager.getConnection(dbUrl);
                ResultSet rs;

                String modality_s;
                String region_s;

                if (modality_a.length==0){
                    modality_s="'MRI','CT','US','Xray'";
                }
                else {
                    modality_s = "'"+String.join("','", modality_a)+"'";
                }

                if (region_a.length==0){
                    region_s="'Brain','Chest','Angiogram";
                }
                else {
                    region_s = "'"+String.join("','", region_a)+"'";
                }

                if (patient_name.equals("")){
                    String sql = "SELECT * FROM imgs WHERE modality IN (?) AND region IN (?)";
                    PreparedStatement psmt = con.prepareStatement(sql);
                    psmt.setString(1, modality_s);
                    psmt.setString(2, region_s);
                    rs = psmt.executeQuery();
                    psmt.close();
                }
                else{
                    String sql = "SELECT * FROM imgs WHERE modality IN (?) AND region IN (?) AND patiet_name=?";
                    PreparedStatement psmt = con.prepareStatement(sql);
                    //psmt.setString(1, modality_s);
                    psmt.setString(1, "'MRI','CT','US','Xray'");
                    //psmt.setString(2, region_s);
                    psmt.setString(2, "'Brain','Chest','Angiogram");
                    psmt.setString(3, patient_name);
                    rs = psmt.executeQuery();
                    psmt.close();
                }

                while (rs.next()) {
                    Img img=new Img();
                    img.setId(rs.getInt("id"));
                    img.setModality(rs.getString("modality"));
                    img.setRegion(rs.getString("region"));
                    img.setPatient_name(rs.getString("patient_name"));
                    img.setFile_name(rs.getString("file_name"));
                    check_t(img.getFile_name());
                    //img_l.add(img);
                    Img img2=new Img();
                    img2.setFile_name("file_name");
                    img_l.add(img2);
                }
                rs.close();
                con.close();
            } catch (SQLException throwables) {
            }

            Img[] img_a = img_l.toArray(new Img[0]);
            resp.setContentType("application/json");
            Gson gson2 = new Gson();
            String jsonString = gson2.toJson(img_a);
            resp.getWriter().write(jsonString);
        }
        else if (path.equals("/thumbnail")) {

        }
        else if (path.equals("/img")) {

        }
    }

    public void check_t(String filename) {
        if(new File("./img_ts/"+filename).isFile()){
        }
        else{
            create_t(filename);
        }
    }

    public void create_t(String filename) {
        String fileAbsolutePath="./imgs/"+filename;
        try {
            Opener opener = new Opener();
            ImagePlus imp = IJ.openImage(fileAbsolutePath);
            ImageProcessor ip = imp.getProcessor();
            StackProcessor sp = new StackProcessor(imp.getStack(), ip);

            int width = imp.getWidth();
            int height = imp.getHeight();

            int cropWidth = 0;
            int cropHeight = 0;

            if(width > height) {
                cropWidth = height;
                cropHeight = height;
            } else {
                cropWidth = width;
                cropHeight = width;
            }

            int x = -1;
            int y = -1;

            if(width == height) {
                x = 0;
                y = 0;
            } else if(width > height) {
                x = (width - height) / 2;
                y=0;
            } else if (width < height) {
                x = 0;
                y = (height - width) / 2;
            }

            ImageStack croppedStack = sp.crop(x, y, cropWidth, cropHeight);

            imp.setStack(null, croppedStack);

            sp = new StackProcessor(imp.getStack(), imp.getProcessor());

            ImageStack resizedStack = sp.resize(100, 100, true);
            imp.setStack(null, resizedStack);

            StringBuffer filePath = new StringBuffer(filename);
            filePath.replace(filePath.lastIndexOf("."),
                    filePath.length(), ".jpg");
            String filename2 = filePath.toString();

            String saveAsFilePath="./img_ts/"+filename2;
            IJ.save(imp, saveAsFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
