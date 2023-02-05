import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const AddPlaylist = ({jwt}) => {
    const [playlist, setPlaylist] = useState({
        playlistName: ''
    });
    const navigate = useNavigate();

    const handleChange = event => {
        setPlaylist({   
            ...playlist,
            [event.target.name]: event.target.value
        });
    };

    const handleSubmit = event => {
        event.preventDefault();
        const userId = JSON.parse(atob(jwt.split('.')[1]))['sub']
        axios
          .post(`http://localhost:8081/api/profiles/${userId}/playlists`, {playlistName:playlist.playlistName}, {
            headers: {
              Authorization: jwt
            }
          })
          .then(response => {
            console.log(response);
            alert('Playlist adaugat!');
            navigate('/playlists');
          })
          .catch(error => {
            if(error.response.status === 401)
            {
                alert('Eroare autorizare!')
                navigate('/')
            }else if(error.response.status === 403)
            {
                alert('Eroare, nu ai dreptul sa adaugi o melodie!')
                navigate('/')
            }
            else if(error.response.status === 404)
            {
                alert('Eroare, profilul la care incerci sa adaugi un playlist nu exista!!')
            }
            else
            {
                alert(error.response.message)
            }
            console.log(error);
          });
    };

    const redirectHome = () => {
        navigate('/home')
    }

    return (
        <div>
             <div className="navbar">
                <div className="navbar-links">
                    <button onClick={redirectHome}> Home </button>
                </div>
            </div>
            <form onSubmit={handleSubmit}>
                <label>
                    Playlist Name:
                    <input required
                        type="text"
                        name="playlistName"
                        value={playlist.playlistName}
                        onChange={handleChange}
                    />
                </label>
                <br />
                <button type="submit">Save</button>
            </form>
        </div>
    );
}

export default AddPlaylist;
