import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Playlists.css';
import './navbar.css';

const SongDetails = ({ url }) => {
    const [song, setSong] = useState({});
    const navigate = useNavigate();

    useEffect(() => {
        fetch(url)
          .then(response => response.json())
          .then(data => setSong(data))
          .catch(err => console.log(err));
      }, []);

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
            Name: {song.song_name} <br></br>
            Music-Genre: {song.music_genre} <br></br>
            Type: {song.elem_type} <br></br>
            Release-year: {song.release_year} <br></br>
            {song.album != null ? (<div>Album: {song.album}</div>): <p></p>}
        </div>
    )
}

export default SongDetails;