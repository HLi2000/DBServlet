import java.io.Serializable;

public class Img implements Serializable {
    private int id=0;
    private String modality="";
    private String region="";
    private String url="";

//    public Img(String id, String region, String modality, String url){
//        this.id=id;
//        this.region=region;
//        this.modality=modality;
//        this.url=url;
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

    public String getUrl() {
        return url;
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

    public void setUrl(String url) {
        this.url = url;
    }
}
