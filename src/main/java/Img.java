import java.io.Serializable;

public class Img implements Serializable {
    private int id=0;
    private String modality="";
    private String region="";
    private String patient_name="";
    private String filename="";

//    public Img(String id, String region, String modality, String filename){
//        this.id=id;
//        this.region=region;
//        this.modality=modality;
//        this.filename=filename;
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

    public String getFilename() {
        return filename;
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

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
