package pos.spotify.Model.Playlist;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;
import pos.spotify.Model.Song.Song;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Playlist {
    private String playlistId;
    private String playlistName;
    private List<Song> songs;

    private Map<String, Map<String, String>> _links;

    public Playlist(String playlistName, List<Song> songs, Map<String, Map<String, String>> links) {
        this.playlistName = playlistName;
        this.songs = songs;
        this._links = links;
    }

    public Playlist(String playlistName, List<Song> songs) {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString().replace("-", "").substring(0, 16);
        this.playlistName = playlistName;
        this.songs = songs;
        this.playlistId = uuidString;
    }
}
