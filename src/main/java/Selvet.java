import com.google.gson.Gson;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.*;
import ij.process.ImageProcessor;
import ij.process.StackProcessor;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
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

            List<Img> img_l = new ArrayList<Img>();

            try {
                Class.forName("org.postgresql.Driver");
            } catch (Exception e) {
            }

            try {
                Connection conn = DriverManager.getConnection(dbUrl);
                ResultSet rs;
                PreparedStatement psmt;
                String sql;

                String[] modality_a= searchInfo.getModality_a();
                String[] region_a= searchInfo.getRegion_a();
                String patient_name= searchInfo.getPatient_name();
                String[] modality_all={"MRI","CT","US","Xray"};
                String[] region_all={"Brain","Chest","Angiogram"};
                Array modality_aa;
                Array region_aa;

                if (modality_a.length==0){
                    modality_aa=conn.createArrayOf("varchar", modality_all);
                }
                else {
                    modality_aa = conn.createArrayOf("varchar", modality_a);
                }

                if (region_a.length==0){
                    region_aa=conn.createArrayOf("varchar", region_all);
                }
                else {
                    region_aa=conn.createArrayOf("varchar", region_a);
                }

                if (patient_name.equals("")){
                    sql = "SELECT * FROM imgs WHERE modality = ANY (?) AND region = ANY (?)";
                    psmt = conn.prepareStatement(sql);
                    psmt.setArray(1, modality_aa);
                    psmt.setArray(2, region_aa);
                    rs = psmt.executeQuery();
                }
                else{
                    sql = "SELECT * FROM imgs WHERE Modality = ANY (?) AND Region = ANY (?) AND Patient_name=?";
                    psmt = conn.prepareStatement(sql);
                    psmt.setArray(1, modality_aa);
                    psmt.setArray(2, region_aa);
                    psmt.setString(3, patient_name);
                    sql = psmt.toString();
                    rs = psmt.executeQuery();
                }

                while (rs.next()) {
                    Img img=new Img();
                    img.setId(rs.getInt("Id"));
                    img.setModality(rs.getString("Modality"));
                    img.setRegion(rs.getString("Region"));
                    img.setPatient_name(rs.getString("Patient_name"));
                    img.setFile_name(rs.getString("File_name"));
                    img_l=check_t(img.getFile_name(),img_l);
                    img_l.add(img);
                }

                rs.close();
                psmt.close();
                conn.close();

            } catch (SQLException e) {
                Img img2=new Img();
                img2.setFile_name(e.toString());
                img_l.add(img2);
            }

            Img[] img_a = img_l.toArray(new Img[0]);
            resp.setContentType("application/json");
            Gson gson2 = new Gson();
            String jsonString = gson2.toJson(img_a);
            resp.getWriter().write(jsonString);
        }
        else if (path.equals("/thumbnail")) {
            String reqBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            StringBuffer filePath = new StringBuffer(reqBody);
            filePath.replace(filePath.lastIndexOf("."),
                    filePath.length(), ".jpg");
            String fileName = filePath.toString();
            String FilePath="./img_ts/"+fileName;

            //制定浏览器头
            resp.setHeader("content-disposition", "attachment;fileName="+fileName);

            InputStream reader = null;
            OutputStream out = null;
            byte[] bytes = new byte[1024];
            int len = 0;
            try {
                // 读取文件
                reader = new FileInputStream(FilePath);
                // 写入浏览器的输出流
                out = resp.getOutputStream();

                while ((len = reader.read(bytes)) > 0) {
                    out.write(bytes, 0, len);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    reader.close();
                }
                if (out != null)
                    out.close();
            }
        }
        else if (path.equals("/img")) {
            String reqBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            StringBuffer filePath = new StringBuffer(reqBody);
            filePath.replace(filePath.lastIndexOf("."),
                    filePath.length(), ".jpg");
            String fileName = filePath.toString();
            String FilePath="./imgs/"+fileName;

            //制定浏览器头
            resp.setHeader("content-disposition", "attachment;fileName="+fileName);

            InputStream reader = null;
            OutputStream out = null;
            byte[] bytes = new byte[1024];
            int len = 0;
            try {
                // 读取文件
                reader = new FileInputStream(FilePath);
                // 写入浏览器的输出流
                out = resp.getOutputStream();

                while ((len = reader.read(bytes)) > 0) {
                    out.write(bytes, 0, len);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    reader.close();
                }
                if (out != null)
                    out.close();
            }
        }
    }

    public List<Img> check_t(String filename,List<Img> img_l) {
        if(new File("./img_ts/"+filename).isFile()){
            return img_l;
        }
        else{
            img_l=create_t(filename,img_l);
            return img_l;
        }
    }

    public List<Img> create_t(String filename,List<Img> img_l) {
        String fileAbsolutePath="./imgs/"+filename;
        try {
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

            BufferedImage buffImage = imp.getBufferedImage();
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ImageIO.write(buffImage, "jpg", baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());

            return img_l;
        } catch (Exception e) {
            Img img2=new Img();
            img2.setFile_name(e.toString());
            img_l.add(img2);
            return img_l;
        }
    }
}
