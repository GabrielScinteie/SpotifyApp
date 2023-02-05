package pos.spotify.VIew;

import pos.spotify.Model.Artist.Artist;
import pos.spotify.Model.Song.Song;

public class UtilConversions {
    public SongDTO SongToDTO(Song song)
    {

        if(song == null)
            return new SongDTO();

        Song parent = song.getAlbum();
        Integer parentID = parent != null ? parent.getId() : null;

        return new SongDTO(song.getId(), song.getSong_name(), song.getMusic_genre(), song.getElem_type(), song.getRelease_year(), parentID);
    }

    public ArtistDTO ArtistToDTO(Artist artist)
    {
        if(artist == null)
            return new ArtistDTO();

        return new ArtistDTO(artist.getId(), artist.getArtist_name(), artist.getActive());
    }
}
