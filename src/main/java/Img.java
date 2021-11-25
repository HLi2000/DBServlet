import java.io.Serializable;

public class Img implements Serializable {
    private String id="";
    private String region="";
    private String modality="";
    private String url="";

//    public Img(String id, String region, String modality, String url){
//        this.id=id;
//        this.region=region;
//        this.modality=modality;
//        this.url=url;
//    }

    public String getId() {
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

    public void setId(String id) {
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
