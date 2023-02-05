package pos.spotify.Model.Song;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Link;
import pos.spotify.Model.Artist.Artist;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Song {

    private Integer song_id;
    private String song_name;
    private Integer release_year;
    private Map<String, Map<String, String>> _links;
}
