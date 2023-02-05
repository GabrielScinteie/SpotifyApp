package pos.spotify.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pos.spotify.JWTService;
import pos.spotify.Model.Playlist.Playlist;
import pos.spotify.Model.Profiles.Profile;
import pos.spotify.Model.Profiles.ProfileRepository;
import pos.spotify.Model.Song.Song;
import pos.spotify.View.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/profiles")
public class ProfilesController {
    @Autowired
    private ProfileRepository profileRepository;

    private final UtilsConversions utilConversions = new UtilsConversions();

    @Autowired
    private JWTService jwtService;

    @CrossOrigin
    @GetMapping("")
    public ResponseEntity<CollectionModel<Profile>> getProfiles(){
        Link selfLink = linkTo(methodOn(ProfilesController.class).getProfiles()).withSelfRel();
        CollectionModel<Profile> result = CollectionModel.of(profileRepository.findAll(), selfLink);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable(value = "userId") Integer userId){
        Profile profile = null;
        try{
            profile = profileRepository.findByUserId(userId);
            if(profile == null)
                throw new Exception();
        }
        catch(Exception e)
        {
            return new ResponseEntity<>("Profilul nu exista!", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/{userId}/playlists")
    public ResponseEntity<?> getPlaylists(@PathVariable(value = "userId") Integer userId)
    {
        Profile profile = null;
        try{
            profile = profileRepository.findByUserId(userId);
            if(profile == null)
                throw new Exception();
        }
        catch(Exception e)
        {
            return new ResponseEntity<>("Profilul nu exista!", HttpStatus.NOT_FOUND);
        }

        PlaylistList playlistList = new PlaylistList();
        playlistList.setPlaylists(profile.getPlaylists());

        Map<String, Map<String, String>> links = new HashMap<String, Map<String, String>>();
        Map<String, String> self = new HashMap<String, String>();

        self.put("href", "http://localhost:8081/api/profiles/" + userId + "/playlists");

        links.put("self", self);

        playlistList.set_links(links);

        return new ResponseEntity<>(playlistList, HttpStatus.OK);
    }

    @CrossOrigin("http://localhost:3000")
    @PostMapping("")
    public ResponseEntity<?> createProfile(@RequestBody Profile profile, @RequestHeader (name="Authorization") String jwt)
    {
        String response = null;
        try{
            response = jwtService.validateJWT(jwt);
        }
        catch(RuntimeException e)
        {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Integer userId = Integer.parseInt(jwtService.getUserId(response));
        if(!jwtService.isClient(response))
            return new ResponseEntity<>("Doar clientii isi pot face profil pentru playlist-uri!", HttpStatus.FORBIDDEN);
        // Daca user-ul are deja un playlist
        Profile profile2 = null;
        profile2 = profileRepository.findByUserId(userId);
        if(profile2 != null)
            return new ResponseEntity<>("Username-ul este deja utilizat!", HttpStatus.CONFLICT);
        // Daca username-ul deja exista
        profile2 = profileRepository.findByUsername(profile.getUsername());
        if(profile2 != null)
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);

        profile.setPlaylists(new ArrayList<>());

        Map<String, Map<String, String>> links = new HashMap<String, Map<String, String>>();
        Map<String, String> self = new HashMap<String, String>();

        self.put("href", "http://localhost:8081/api/profiles/" + userId);

        Map<String, String> parent = new HashMap<String, String>();
        parent.put("href", "http://localhost:8081/api/profiles");

        links.put("self", self);
        links.put("parent", parent);

        profile.set_links(links);

        return new ResponseEntity<>(profileRepository.save(profile), HttpStatus.CREATED);
    }

    // DEMO
    @CrossOrigin("http://localhost:3000")
    @PostMapping("/{userId}/playlists")
    public ResponseEntity<?> addPlaylist(@PathVariable(value = "userId") Integer userId, @RequestBody Playlist playlist, @RequestHeader (name="Authorization") String jwt)
    {

        String response = null;
        try{
            response = jwtService.validateJWT(jwt);
        }
        catch(RuntimeException e)
        {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Integer userid = Integer.parseInt(jwtService.getUserId(response));
        if(!jwtService.isClient(response))
            return new ResponseEntity<>("Doar clientii isi pot face playlist-uri!", HttpStatus.FORBIDDEN);
        if(!Objects.equals(userid, userId))
        {
            return new ResponseEntity<>("Playlist-urile se pot adauga doar in contul tau!", HttpStatus.FORBIDDEN);
        }

        Profile profile = null;
        try{
            profile = profileRepository.findByUserId(userId);
            if(profile == null)
                throw new Exception();
        }
        catch(Exception e)
        {
            return new ResponseEntity<>("Profilul nu exista!", HttpStatus.NOT_FOUND);
        }

        if(playlist.getSongs() == null)
            playlist.setSongs(new ArrayList<>());

        if(profile.getPlaylists() == null)
            profile.setPlaylists(new ArrayList<>());
        List<Playlist> playlists = profile.getPlaylists();

        Playlist playlistToAdd = new Playlist(playlist.getPlaylistName(), playlist.getSongs());

//        // Verificare nume unic
//        for(Playlist p : playlists)
//        {
//            if(Objects.equals(p.getPlaylistName(), playlistToAdd.getPlaylistName()) || Objects.equals(p.getPlaylistId(), playlistToAdd.getPlaylistId()))
//                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
//        }

        Map<String, Map<String, String>> links = new HashMap<String, Map<String, String>>();
        Map<String, String> self = new HashMap<String, String>();

        self.put("href", "http://localhost:8081/api/profiles/" + userId + "/playlists/" + playlistToAdd.getPlaylistId());

        Map<String, String> parent = new HashMap<String, String>();
        parent.put("href", "http://localhost:8081/api/profiles/" + userId + "/playlists");

        links.put("self", self);
        links.put("parent", parent);

        playlistToAdd.set_links(links);
        playlists.add(playlistToAdd);

        profile.setPlaylists(playlists);
        profileRepository.save(profile);

        return new ResponseEntity<>(playlistToAdd, HttpStatus.CREATED);
    }

    @CrossOrigin("http://localhost:3000")
    @PostMapping("/{userId}/playlists/{playlistId}/songs")
    public ResponseEntity<?> addSongToPlaylist(@PathVariable(value = "userId") Integer userId,
                                               @PathVariable(value = "playlistId") String playlistId,
                                               @RequestBody SongList songs,
                                               @RequestHeader (name="Authorization") String jwt)
    {
        String response2 = null;
        try{
            response2 = jwtService.validateJWT(jwt);
        }
        catch(RuntimeException e)
        {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Integer userid = Integer.parseInt(jwtService.getUserId(response2));
        if(!jwtService.isClient(response2))
            return new ResponseEntity<>("Doar clientii isi pot adauga melodii!", HttpStatus.FORBIDDEN);
        if(!userid.equals(userId))
        {
            return new ResponseEntity<>("Melodiile se pot adauga doar in in playlist-urile din contul tau!", HttpStatus.FORBIDDEN);
        }
        Profile profile = null;
        try{
            profile = profileRepository.findByUserId(userId);
            if(profile == null)
                throw new Exception();
        }
        catch(Exception e)
        {
            return new ResponseEntity<>("Profilul nu exista!", HttpStatus.NOT_FOUND);
        }

        // verific ca exista playlist-ul
        boolean playlistExists = false;
        int playlistIndex = -1;
        for(int i = 0; i < profile.getPlaylists().size(); i++)
            if (Objects.equals(profile.getPlaylists().get(i).getPlaylistId(), playlistId)) {
                playlistExists = true;
                playlistIndex = i;
                break;
            }
        if(!playlistExists)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        Playlist updatedPlaylist = profile.getPlaylists().get(playlistIndex);
        List<Song> songsList = updatedPlaylist.getSongs();


        for(int i = 0; i < songs.getSongs().size(); i++)
            for(int j = 0; j < profile.getPlaylists().get(playlistIndex).getSongs().size(); j++)
                if(Objects.equals(profile.getPlaylists().get(playlistIndex).getSongs().get(j).getSong_id(), songs.getSongs().get(i)))
                    return new ResponseEntity<>("Melodia se afla deja in playlist!", HttpStatus.CONFLICT); // daca o melodie se afla deja in playlist


        RestTemplate restTemplate = new RestTemplate();
        // VERIFIC daca EXISTA MELODIILE SI DE ADAUGAT. DACA NU EXISTA UN ID DE MELODIE DIN LISTA ATUNCI RETURNEZ CONFLICT
        for(Integer id: songs.getSongs())
        {
            String uri = "http://localhost:8080/api/songs/" + id;
            ResponseEntity<SongDTO> response = null;
            try{
                response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), SongDTO.class);
            }
            catch(Exception e)
            {

                return new ResponseEntity<>("Melodia cu id-ul" + id +  "nu exista!", HttpStatus.CONFLICT); // daca cumva melodia nu exista
            }

            SongDTO songDTO = response.getBody();
            Song songToAdd = new Song();
            songToAdd.setSong_id(songDTO.getId());
            songToAdd.setSong_name(songDTO.getSong_name());
            songToAdd.setRelease_year(songDTO.getRelease_year());
            songToAdd.set_links(songDTO.get_links());
            songToAdd.get_links().get("self").get("href");
            songsList.add(songToAdd);
        }

        updatedPlaylist.setSongs(songsList);

        Map<String, Map<String, String>> links = new HashMap<String, Map<String, String>>();
        Map<String, String> self = new HashMap<String, String>();

        self.put("href", "http://localhost:8081/api/profiles/" + userId + "/playlists/" + playlistId);

        Map<String, String> parent = new HashMap<String, String>();
        parent.put("href", "http://localhost:8081/api/profiles/" + userId + "/playlists");

        links.put("self", self);
        links.put("parent", parent);

        updatedPlaylist.set_links(links);

        List<Playlist> playlists = profile.getPlaylists();
        playlists.set(playlistIndex, updatedPlaylist);

        profile.setPlaylists(playlists);
        profileRepository.save(profile);

        return new ResponseEntity<>(updatedPlaylist, HttpStatus.CREATED);
    }
    @CrossOrigin("http://localhost:3000")
    @GetMapping("/{userId}/playlists/{playlistId}")
    public ResponseEntity<?> getPlaylistById(@PathVariable(value = "userId") Integer userId,
                                             @PathVariable(value = "playlistId") String playlistId)
    {

        Profile profile = null;
        try{
            profile = profileRepository.findByUserId(userId);
            if(profile == null)
                throw new Exception();
        }
        catch(Exception e)
        {
            return new ResponseEntity<>("Profilul nu exista!", HttpStatus.NOT_FOUND);
        }

        Playlist playlist = null;
        List<Playlist> playlists = profile.getPlaylists();
        for(int i = 0; i < playlists.size(); i++)
        {
            if(Objects.equals(playlists.get(i).getPlaylistId(), playlistId))
            {
                playlist = playlists.get(i);
                break;
            }
        }

        if(playlist == null)
            return new ResponseEntity<>("Playlist-ul nu exista!", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(playlist, HttpStatus.OK);
    }

    @CrossOrigin("http://localhost:3000")
    @DeleteMapping("/{userId}/playlists/{playlistId}/songs/{songId}")
    public ResponseEntity<?> deleteSongFromPlaylist(@PathVariable(value = "userId") Integer userId,
                                                @PathVariable(value = "playlistId") String playlistId,
                                                @PathVariable(value = "songId") Integer songId,
                                                @RequestHeader (name="Authorization") String jwt)
    {

        String response2 = null;
        try{
            response2 = jwtService.validateJWT(jwt);
        }
        catch(RuntimeException e)
        {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Integer userid = Integer.parseInt(jwtService.getUserId(response2));
        if(!jwtService.isClient(response2))
            return new ResponseEntity<>("Doar clientii pot sterge melodii din playlist-uri!", HttpStatus.FORBIDDEN);
        if(!Objects.equals(userid, userId))
        {
            return new ResponseEntity<>("Poti sterge doar melodiile din contul tau!", HttpStatus.FORBIDDEN);
        }
        Profile profile = null;
        try{
            profile = profileRepository.findByUserId(userId);
            if(profile == null)
                throw new Exception();
        }
        catch(Exception e)
        {
            return new ResponseEntity<>("Profilul nu exista!", HttpStatus.NOT_FOUND);
        }

        // verific ca exista playlist-ul
        boolean playlistExists = false;
        int playlistIndex = -1;
        Playlist playlist = null;
        List<Playlist> playlists = profile.getPlaylists();

        for(int i = 0; i < playlists.size(); i++)
            if (Objects.equals(playlists.get(i).getPlaylistId(), playlistId)) {
                playlistExists = true;
                playlist = playlists.get(i);
                playlistIndex = i;
                break;
            }
        if(!playlistExists)
            return new ResponseEntity<>("PLaylist-ul nu exista!", HttpStatus.NOT_FOUND);

        List<Song> songs = playlist.getSongs();

        int index = -1;
        for(int i = 0; i < songs.size(); i++)
        {
            if(Objects.equals(songs.get(i).getSong_id(), songId))
            {
                index = i;
                break;
            }
        }

        if(index == -1)
            return new ResponseEntity<>("Melodia nu exista!", HttpStatus.NOT_FOUND);

        songs.remove(index);

        Playlist newPlaylist = playlists.get(playlistIndex);
        newPlaylist.setSongs(songs);
        playlists.set(playlistIndex, newPlaylist);

        profile.setPlaylists(playlists);

        profileRepository.save(profile);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
