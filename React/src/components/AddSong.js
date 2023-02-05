import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Playlists.css';

const AddSong = ({jwt}) => {
    const navigate = useNavigate();
    const [song, setSong] = useState({
        song_name: '',
        music_genre: '',
        elem_type: '',
        release_year: '',
        album: ''
    });

    const handleChange = event => {
        setSong({
            ...song,
            [event.target.name]: event.target.value
        });
    };

    const handleSubmit = event => {
        event.preventDefault();
        axios
          .post(`http://localhost:8080/api/songs`, song, {
            headers: {
              Authorization: jwt            
            }
          })
          .then(response => {
            console.log(response);
            alert('song added!');
            navigate('/home');
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
            }else if(error.response.status === 409)
            {
                alert(error.response.data)
            }else if(error.response.status === 406)
            {
                alert('Reprezentarea nu corespunde!')
            }else if(error.response.status === 422)
            {
                alert('Anul nu poate fi in viitor')
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
                    Name:
                    <input required
                        type="text"
                        name="song_name"
                        value={song.song_name}
                        onChange={handleChange}
                    />
                </label>
                <br />
                <label>
                    Music Genre:
                    <select required name="music_genre" value={song.music_genre} onChange={handleChange}>
                      <option value="Rock">ROCK</option>
                      <option value="Pop">POP</option>
                      <option value="Electronic">ELECTRONIC</option>
                      <option value="Rap">RAP</option>
                      <option value="Oriental">ORIENTAL</option>
                      <option value="Latino">LATINO</option>
                      <option value="Jazz">JAZZ</option>
                    </select>
                </label>
                <br />
                <label>
                    Type:
                    <select required name="elem_type" value={song.elem_type} onChange={handleChange}>
                      <option value="album">ALBUM</option>
                      <option value="song">SONG</option>
                      <option value="single">SINGLE</option>
                    </select>
                </label>
                <br />
                <label>
                    Release Year:
                    <input required
                        type="number"
                        name="release_year"
                        value={song.release_year}
                        onChange={handleChange}
                    />
                </label>
                <br />
                <label>
                    Album:
                    <input required
                        type="text"
                        name="album"
                        value={song.album}
                        onChange={handleChange}
                    />
                </label>
                <br />
                <button type="submit">Save</button>
            </form>
        </div>
    );
}

export default AddSong;

