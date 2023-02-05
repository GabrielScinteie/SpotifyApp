package pos.spotify.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.spotify.JWTService;
import pos.spotify.Model.Artist.Artist;
import pos.spotify.VIew.ArtistDTO;
import pos.spotify.Model.Artist.ArtistRepository;
import pos.spotify.Model.Song.Song;
import pos.spotify.Model.Song.SongRepository;
import pos.spotify.VIew.SongDTO;
import pos.spotify.VIew.UtilConversions;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private JWTService jwtService;

    private final UtilConversions utilConversions = new UtilConversions();

    @CrossOrigin
    @GetMapping("")
    public ResponseEntity<CollectionModel<ArtistDTO>> getAllArtists(){
        List<ArtistDTO> artistsDTO = new ArrayList<ArtistDTO>();
        for(ArtistDTO artistDTO : artistRepository.findAll().stream().map(utilConversions::ArtistToDTO).toList())
        {
            Link selfLink = linkTo(ArtistController.class).slash(artistDTO.getId()).withSelfRel();
            Link parentLink = linkTo(methodOn(ArtistController.class).getAllArtists()).withRel("parent");
            artistDTO.add(selfLink);
            artistDTO.add(parentLink);
            artistsDTO.add(artistDTO);
        }

        Link selfLink = linkTo(methodOn(ArtistController.class).getAllArtists()).withSelfRel();
        CollectionModel<ArtistDTO> result = CollectionModel.of(artistsDTO, selfLink);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/{artistId}")
    public ResponseEntity<?> getArtist(@PathVariable(value = "artistId") String artist_id){
        Artist artist = null;

        try{
            artist = artistRepository.findById(artist_id).orElseThrow();
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Artistul nu exista", HttpStatus.NOT_FOUND);
        }

        ArtistDTO artistDTO = utilConversions.ArtistToDTO(artist);
        Link selfLink = linkTo(ArtistController.class).slash(artist_id).withSelfRel();
        Link parentLink = linkTo(methodOn(ArtistController.class).getAllArtists()).withRel("parent");
        artistDTO.add(selfLink);
        artistDTO.add(parentLink);
        return new ResponseEntity<>(artistDTO, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/{artistId}/songs")
    public ResponseEntity<?> getAllSongsByArtistId(@PathVariable(value = "artistId") String artist_id)
    {
        Artist artist = null;

        try{
            artist = artistRepository.findById(artist_id).orElseThrow();
        }
        catch (Exception e) {
            return new ResponseEntity<>("Artistul nu exista", HttpStatus.NOT_FOUND);
        }

        if(artist.songs.size() == 0)
        {
            return new ResponseEntity<>("Artistul nu are melodii", HttpStatus.OK);
        }

        List<SongDTO> songsDTO = new ArrayList<SongDTO>();
        for(Song song : artist.songs)
        {
            SongDTO songDTO = utilConversions.SongToDTO(song);
            Link selfLink = linkTo(SongController.class).slash(song.getId()).withSelfRel();
            Link parentLink = linkTo(methodOn(SongController.class).getAllSongs()).withRel("parent");
            songDTO.add(selfLink);
            songDTO.add(parentLink);
            songsDTO.add(songDTO);
        }

        Link parentLink = linkTo(methodOn(ArtistController.class).getArtist(artist_id)).withRel("parent");
        Link selfLink = linkTo(methodOn(ArtistController.class).getAllSongsByArtistId(artist_id)).withSelfRel();
        CollectionModel<SongDTO> result = CollectionModel.of(songsDTO, selfLink);
        result.add(parentLink);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @CrossOrigin
    @PutMapping("/{uuid}")
    public ResponseEntity<ArtistDTO> createArtist(@PathVariable(value = "uuid") String artist_id, @RequestBody ArtistDTO artistDTO,
                                                  @RequestHeader (name="Authorization") String jwt)
    {
        String response = null;
        try{
            response = jwtService.validateJWT(jwt);
        }
        catch(RuntimeException e)
        {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(!jwtService.isContentManager(response))
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);

        Artist artist = new Artist(artist_id, artistDTO.getArtist_name(), artistDTO.getActive());

        // daca nu exista in baza de date creez si returnez 201 si obiectul
        if(!artistRepository.existsById(artist_id))
        {
            Artist created_artist = artistRepository.save(artist);
            Link selfLink = linkTo(ArtistController.class).slash(artist_id).withSelfRel();
            Link parentLink = linkTo(methodOn(ArtistController.class).getAllArtists()).withRel("parent");
            artistDTO.add(selfLink);
            artistDTO.add(parentLink);
            artistDTO.setId(artist_id);
            return new ResponseEntity<>(artistDTO, HttpStatus.CREATED);
        }

        // daca exista deja in baza de date updatez si returnez 204 NO content
        List<Song> songs = artistRepository.findById(artist_id).orElseThrow().songs;
        artist.addSongs(songs);

        artistRepository.save(artist);

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{artistId}/songs/{songId}")
    public ResponseEntity<?> addSongToArtist(@PathVariable(value = "artistId") String artist_id,
                                                @PathVariable(value = "songId") Integer song_id,
                                                @RequestHeader (name="Authorization") String jwt)
    {
        String response = null;
        try{
            response = jwtService.validateJWT(jwt);
        }
        catch(RuntimeException e)
        {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(jwtService.isContentManager(response) == Boolean.FALSE)
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);

        Artist artist = null;
        Song song = null;

        try{
            artist = artistRepository.findById(artist_id).orElseThrow();
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Artistul nu exista", HttpStatus.NOT_FOUND);
        }

        try{
            song = songRepository.findById(song_id).orElseThrow();
        }
        catch(Exception e)
        {
            return new ResponseEntity<>("Melodia nu exista", HttpStatus.NOT_FOUND);
        }

        artist.addSong(song);
        artistRepository.save(artist);

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
