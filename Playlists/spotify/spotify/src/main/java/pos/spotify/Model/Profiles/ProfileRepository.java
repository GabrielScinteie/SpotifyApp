package pos.spotify.Model.Profiles;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProfileRepository extends MongoRepository<Profile, String> {
    public Profile findByUserId(Integer userid);
    public Profile findByUsername(String username);
}
