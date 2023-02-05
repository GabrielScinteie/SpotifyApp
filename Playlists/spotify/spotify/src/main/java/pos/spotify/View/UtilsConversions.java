package pos.spotify.View;

import pos.spotify.Model.Playlist.Playlist;
import pos.spotify.Model.Song.Song;

import static java.util.stream.Collectors.toList;

public class UtilsConversions {
    public SongDTO convertSongToDTO(Song song)
    {
        SongDTO songDTO = new SongDTO();
        songDTO.setId(song.getSong_id());
        songDTO.setSong_name(song.getSong_name());
        songDTO.setRelease_year(song.getRelease_year());

        return songDTO;
    }

    public PlaylistDTO convertPlaylistToDTO(Playlist playlist)
    {
        UtilsConversions utilsConversions = new UtilsConversions();
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setPlaylistId(playlist.getPlaylistId());
        playlistDTO.setPlaylistName(playlist.getPlaylistName());
        playlistDTO.setSongsDTO(playlist.getSongs().stream().map((utilsConversions::convertSongToDTO)).toList());

        return playlistDTO;
    }

//    public Playlist addLink(Playlist playlist, )
}

