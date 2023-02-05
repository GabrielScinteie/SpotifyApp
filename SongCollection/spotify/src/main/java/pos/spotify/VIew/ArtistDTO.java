package pos.spotify.VIew;


import org.springframework.hateoas.RepresentationModel;

public class ArtistDTO extends RepresentationModel {
    private String id;
    private String artist_name;
    private Integer active;

    public ArtistDTO(){

    }
    public ArtistDTO(String id, String artist_name, Integer active)
    {
        this.id = id;
        this.artist_name = artist_name;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
