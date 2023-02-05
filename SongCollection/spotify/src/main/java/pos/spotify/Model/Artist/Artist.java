package pos.spotify.Model.Artist;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import net.minidev.json.annotate.JsonIgnore;
import pos.spotify.Model.Song.Song;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @Column(name="artist_uuid")
    private String id;

    private String artist_name;
    private Integer active;

    @ManyToMany
    @JoinTable(
            name="songs_artists_join",
            joinColumns = @JoinColumn(name = "artist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    @JsonBackReference
    public List<Song> songs = new ArrayList<>();

    public Artist()
    {

    }
    public Artist(String id, String artist_name, Integer active)
    {
        this.id = id;
        this.artist_name = artist_name;
        this.active = active;
        this.songs = null;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addSongs(List<Song> songs)
    {
        if(this.songs == null)
            this.songs = songs;
        else
            this.songs.addAll(songs);

        for (Song song : songs)
            song.artists.add(this);
    }

    public void addSong(Song song)
    {
        if(this.songs == null)
            this.songs = new ArrayList<Song>();

        this.songs.add(song);

    }
}
