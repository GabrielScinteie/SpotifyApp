package pos.spotify.View;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;
import pos.spotify.Model.Playlist.Playlist;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileDTO extends RepresentationModel {
    private Integer user_id;
    private String username;

    private String id;
    List<Playlist> playlists;
}
