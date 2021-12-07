import java.io.Serializable;
import java.util.List;

public class SearchInfo implements Serializable {
    private String[] modality_a;
    private String[] region_a;
    private String patient_name;

    public SearchInfo(String[] modality_a, String[] region_a, String patient_name){
        this.modality_a =modality_a;
        this.region_a =region_a;
        this.patient_name =patient_name;
    }

    public String[] getModality_a() {
        return modality_a;
    }

    public String[] getRegion_a() {
        return region_a;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setModality(String[] modality_a) {
        this.modality_a = modality_a;
    }

    public void setRegion(String[] region_a) {
        this.region_a = region_a;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }
}