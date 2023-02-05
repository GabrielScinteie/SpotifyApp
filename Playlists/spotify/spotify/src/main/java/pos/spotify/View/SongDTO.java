package pos.spotify.View;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SongDTO extends RepresentationModel {
    private Integer id;
    private String song_name;
    private Integer release_year;
    private Map<String, Map<String, String>> _links;
}
