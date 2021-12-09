import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.process.StackProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchDao {
    public Img[] search(SearchInfo searchInfo){
        Connection conn=null;
        PreparedStatement psmt;
        String sql;
        ResultSet rs;
        List<Img> img_l = new ArrayList<Img>();

        try {
            conn = DBDao.getConnection();

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
                //sql = psmt.toString();
                rs = psmt.executeQuery();
            }

            while (rs.next()) {
                Img img=new Img();
                img.setId(rs.getInt("Id"));
                img.setModality(rs.getString("Modality"));
                img.setRegion(rs.getString("Region"));
                img.setPatient_name(rs.getString("Patient_name"));
                img.setFile_name(rs.getString("File_name"));
                //img.setThumbnail(create_t(img.getFile_name()));
                img_l.add(img);
            }

            rs.close();
            psmt.close();
            conn.close();
        } catch (SQLException e) {
            Img img2=new Img();
            img2.setFile_name(e.toString());
            img_l.add(img2);
        }finally{
            DBDao.closeConnection(conn);
        }

        Img[] img_a = img_l.toArray(new Img[0]);
        return img_a;
    }

    public InputStream create_thumbnail(String filename) {
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

            return is;
        } catch (Exception e) {
            return null;
        }
    }
}
