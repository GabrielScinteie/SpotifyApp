package pos.spotify.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pos.spotify.JWTService;
import pos.spotify.Model.Artist.ArtistRepository;
import pos.spotify.Model.Song.MusicElemType;
import pos.spotify.Model.Song.Song;
import pos.spotify.VIew.ArtistDTO;
import pos.spotify.VIew.SongDTO;
import pos.spotify.Model.Song.SongRepository;
import pos.spotify.VIew.UtilConversions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private JWTService jwtService;
    private final UtilConversions utilConversions = new UtilConversions();

    @CrossOrigin
    @GetMapping("")
    public ResponseEntity<CollectionModel<SongDTO>> getAllSongs(){
        List<SongDTO> songsDTO = new ArrayList<SongDTO>();
        for(SongDTO song : songRepository.findAll().stream().map(utilConversions::SongToDTO).toList())
        {
            Link selfLink = linkTo(SongController.class).slash(song.getId()).withSelfRel();
            Link parentLink = linkTo(methodOn(SongController.class).getAllSongs()).withRel("parent");
            song.add(selfLink);
            song.add(parentLink);
            songsDTO.add(song);
        }

        Link selfLink = linkTo(methodOn(SongController.class).getAllSongs()).withSelfRel();
        CollectionModel<SongDTO> result = CollectionModel.of(songsDTO, selfLink);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/{id}/artists")
    public ResponseEntity<?> getAllArtistsOfSong(@PathVariable(value = "id") Integer song_id){
        Song song = null;

        try{
            song = songRepository.findById(song_id).orElseThrow();
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Melodia nu exista", HttpStatus.NOT_FOUND);
        }

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
    @GetMapping("/{id}")
    public ResponseEntity<?> getSong(@PathVariable(value = "id") Integer song_id){
        Song song = null;

        try{
            song = songRepository.findById(song_id).orElseThrow();
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Melodia nu exista", HttpStatus.NOT_FOUND);
        }

        SongDTO songDTO = utilConversions.SongToDTO(song);

        Link selfLink = linkTo(SongController.class).slash(song_id).withSelfRel();
        Link parentLink = linkTo(methodOn(SongController.class).getAllSongs()).withRel("parent");
        songDTO.add(selfLink);
        songDTO.add(parentLink);
        return new ResponseEntity<>(songDTO, HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("")
    public ResponseEntity<?> createSong(@RequestBody SongDTO songDTO, @RequestHeader (name="Authorization") String jwt){
        String response = null;
        try{
            response = jwtService.validateJWT(jwt);
        }
        catch(RuntimeException e)
        {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(jwtService.isClient(response) || jwtService.isAdmin(response))
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);

        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        if(songDTO.getRelease_year() > Integer.parseInt(format.format(new Date())))
        {
            return new ResponseEntity<>("Anul nu poate fi in viitor", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // single-urile nu pot avea albume
        if(songDTO.getElem_type() == MusicElemType.single && songDTO.getAlbum() != null)
            return new ResponseEntity<>("Single-urile nu pot face parte din albume!", HttpStatus.CONFLICT);

        if(songDTO.getElem_type() == MusicElemType.album && songDTO.getAlbum() != null)
            return new ResponseEntity<>("Album-urile nu pot face parte din alte albume!", HttpStatus.CONFLICT);

        // Conflict daca dau un album id care nu exista sau albumul nu este de fapt album
        Song aux = null;
        if(songDTO.getAlbum() != null)
        {
            try{
                aux = songRepository.findById(songDTO.getAlbum()).orElseThrow();
                if(aux.getElem_type() != MusicElemType.album)
                    throw new Exception();
            }
            catch(Exception e)
            {
                return new ResponseEntity<>("ID-ul de album nu este valid", HttpStatus.CONFLICT);
            }
        }

        Song song = new Song(songDTO.getSong_name(), songDTO.getMusic_genre(), songDTO.getElem_type(), aux ,songDTO.getRelease_year());
        Song created_song = null;
        try{
           created_song = songRepository.save(song);
        }
        catch(Exception e)
        {
            // DEMO
            // reprezentarea nu corespunde cu cea din tabela
            return new ResponseEntity<>("Reprezentarea nu corespunde cu cea din tabela", HttpStatus.NOT_ACCEPTABLE);
        }

        Link selfLink = linkTo(SongController.class).slash(created_song.getId()).withSelfRel();
        Link parentLink = linkTo(methodOn(SongController.class).getAllSongs()).withRel("parent");
        songDTO.add(selfLink);
        songDTO.add(parentLink);
        songDTO.setId(created_song.getId());

        return new ResponseEntity<>(songDTO, HttpStatus.CREATED);
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSong(@PathVariable(value = "id") Integer song_id, @RequestHeader (name="Authorization") String jwt)
    {
        String response = null;
        try{
            response = jwtService.validateJWT(jwt);
        }
        catch(RuntimeException e)
        {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(jwtService.isClient(response) || jwtService.isAdmin(response))
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        try{
            songRepository.deleteById(song_id);
        }
        catch(Exception e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
