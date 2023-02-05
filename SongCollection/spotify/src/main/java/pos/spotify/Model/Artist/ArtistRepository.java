package pos.spotify.Model.Artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pos.spotify.Model.Song.Song;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, String> {

}
