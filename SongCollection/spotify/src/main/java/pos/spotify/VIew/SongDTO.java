package pos.spotify.VIew;

import org.springframework.hateoas.RepresentationModel;
import pos.spotify.Model.Song.MusicElemType;
import pos.spotify.Model.Song.MusicGenreEnum;

public class SongDTO extends RepresentationModel {
    private Integer id;
    private String song_name;

    private MusicGenreEnum music_genre;

    private MusicElemType elem_type;

    private Integer release_year;

    private Integer album;

    public SongDTO(){

    }

    public SongDTO(Integer id, String song_name, MusicGenreEnum music_genre, MusicElemType elem_type, Integer release_year, Integer album)
    {
        this.id = id;
        this.song_name = song_name;
        this.music_genre = music_genre;
        this.elem_type = elem_type;
        this.release_year = release_year;
        this.album = album;
    }
    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public MusicGenreEnum getMusic_genre() {
        return music_genre;
    }

    public void setMusic_genre(MusicGenreEnum music_genre) {
        this.music_genre = music_genre;
    }

    public MusicElemType getElem_type() {
        return elem_type;
    }

    public void setElem_type(MusicElemType elem_type) {
        this.elem_type = elem_type;
    }

    public Integer getRelease_year() {
        return release_year;
    }

    public void setRelease_year(Integer release_year) {
        this.release_year = release_year;
    }


    public Integer getAlbum() {
        return album;
    }

    public void setAlbum(Integer album) {
        this.album = album;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
