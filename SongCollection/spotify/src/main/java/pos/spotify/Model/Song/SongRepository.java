package pos.spotify.Model.Song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pos.spotify.Model.Artist.Artist;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Integer> {

}
