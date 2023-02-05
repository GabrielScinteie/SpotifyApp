package pos.spotify.Model.Song;

import com.fasterxml.jackson.annotation.JsonBackReference;
import net.minidev.json.annotate.JsonIgnore;
import pos.spotify.Model.Artist.Artist;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "songs")
public class Song {
    @Id
    @Column(name="song_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String song_name;

    @Enumerated(EnumType.STRING)
    private MusicGenreEnum music_genre;

    @Enumerated(EnumType.STRING)
    private MusicElemType elem_type;

    private Integer release_year;

    @ManyToOne
    @JoinColumn(name="album_id")
    private Song album;

    @ManyToMany(mappedBy = "songs")
    @JsonBackReference
    public List<Artist> artists = new ArrayList<>();

    public Song(){

    }

    public Song(String song_name, MusicGenreEnum music_genre, MusicElemType elem_type, Song album, Integer release_year){
        this.song_name = song_name;
        this.music_genre = music_genre;
        this.elem_type = elem_type;
        this.album = album;
        this.release_year = release_year;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Song getAlbum() {
        return album;
    }

    public void setAlbum(Song album) {
        this.album = album;
    }
}
