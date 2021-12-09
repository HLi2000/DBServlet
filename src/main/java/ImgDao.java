import ij.IJ;
import ij.ImagePlus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImgDao {
    public InputStream open_img(String filename) {
        String fileAbsolutePath="./imgs/"+filename;
        try {
            ImagePlus imp = IJ.openImage(fileAbsolutePath);

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
