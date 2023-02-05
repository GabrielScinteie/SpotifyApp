package pos.spotify.View;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import pos.spotify.Model.Song.Song;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlaylistDTO extends RepresentationModel {
    private String playlistId;
    private String playlistName;
    private List<SongDTO> songsDTO;
}
