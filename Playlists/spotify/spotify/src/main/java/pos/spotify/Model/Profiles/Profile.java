package pos.spotify.Model.Profiles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pos.spotify.Model.Playlist.Playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Profiles")
@Getter
@Setter
public class Profile {

    @Id
    private String id;

    private Integer userId;

    private String username;

    List<Playlist> playlists;

    private Map<String, Map<String, String>> _links;

    public Profile(Integer userId, String username, ArrayList<Playlist> playlists) {

    }
}
