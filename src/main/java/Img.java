import java.io.InputStream;
import java.io.Serializable;

public class Img implements Serializable {
    private int id=0;
    private String modality="";
    private String region="";
    private String patient_name="";
    private String file_name="";
    private InputStream thumbnail=null;

//    public Img(String id, String region, String modality, String file_name){
//        this.id=id;
//        this.region=region;
//        this.modality=modality;
//        this.file_name=file_name;
//    }

    public int getId() {
        return id;
    }

    public String getModality() {
        return modality;
    }

    public String getRegion() {
        return region;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public InputStream getThumbnail() {
        return thumbnail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setThumbnail(InputStream thumbnail) {
        this.thumbnail = thumbnail;
    }
}
