import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';
import './Playlists.css';

const AddArtist = ({jwt}) => {
    const navigate = useNavigate();
    const [artist, setArtist] = useState({
        id: uuidv4().substring(0, 16),
        artist_name: '',
        active: 0
    });

    const handleChange = event => {
        setArtist({
            ...artist,
            [event.target.name]: event.target.value
        });
    };

    const handleSubmit = event => {
        event.preventDefault();
        axios
          .put(`http://localhost:8080/api/artists/${artist.id}`, artist, {
            headers: {
              Authorization: jwt
            }
          })
          .then(response => {
            console.log(response);
            alert('Artist creat!');
          })
          .catch(error => {
            if(error.response.status === 401)
            {
                alert('Eroare autorizare!')
                navigate('/')
            }else if(error.response.status === 403)
            {
                alert('Eroare, nu ai dreptul sa adaugi un artist!')
                navigate('/')
            }else if(error.response.status === 404)
            {
                alert('Eroare, uuid-ul artistului trebuie sa fie unic!')
            }
            else
            {
                alert(error.response.message);
            }
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
                    Artist Name:
                    <input required
                        type="text"
                        name="artist_name"
                        value={artist.artist_name}
                        onChange={handleChange}
                    />
                </label>
                <br />
                <label>
                    Active:
                    <input required
                        type="number"
                        name="active"
                        value={artist.active}
                        onChange={handleChange}
                    />
                </label>
                <br />
                <button type="submit">Save</button>
            </form>
        </div>
    );
}

export default AddArtist;
