package pos.spotify.View;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pos.spotify.Model.Playlist.Playlist;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistList {
    List<Playlist> playlists;
    private Map<String, Map<String, String>> _links;
}
