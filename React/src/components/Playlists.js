import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Playlists.css';
import './navbar.css';

const Playlists = ({ jwt , setUrl}) => {
  const [playlists, setPlaylists] = useState([]);
  const [songs, setSongs] = useState([]);
  const [selectedValue, setSelectedValue] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const userId = JSON.parse(atob(jwt.split('.')[1]))['sub'];

    fetch(`http://localhost:8081/api/profiles/${userId}/playlists`)
      .then(response => response.json())
      .then(data => setPlaylists(data.playlists))
      .catch(err => console.log(err));
  }, [jwt]);

  useEffect(() => {
    fetch('http://localhost:8080/api/songs')
      .then(response => response.json())
      .then(data => setSongs(data._embedded.songDTOList))
      .catch(err => console.log(err));
  }, []);

const handleAddNewSong = (playlistId) => {
    let selected_song = songs.find(song => song.id === selectedValue);
    const userId = JSON.parse(atob(jwt.split('.')[1]))['sub']
    console.log(selected_song)
    const body = {"songs" : [parseInt(selected_song.id)]};
    console.log(body)
    axios.post(`http://localhost:8081/api/profiles/${userId}/playlists/${playlistId}/songs`, body, {
      headers: {
        Authorization: jwt
      }
    })
    .then(response => {
      console.log(response);
      alert('Song added!');
      fetch(`http://localhost:8081/api/profiles/${userId}/playlists`)
      .then(response => response.json())
      .then(data => setPlaylists(data.playlists))
      .catch(err => console.log(err));
    })
    .catch(error => {
        if(error.re)
      console.log(error);
      alert('Error adding Song');
    });
}

function handleChange(event) {
    setSelectedValue(event.target.value);
}

const redirectHome = () => {
    navigate('/home')
}

const redirectAddPlaylist= () => {
    navigate('/addPlaylist')
}

const redirectSongDetails= (songUrl) => {
    setUrl(songUrl);
    navigate('/songDetails')
}

return (
    <div>
        <div className="navbar">
            <div className="navbar-links">
                <button onClick={redirectHome}> Home </button>
            </div>
        </div>
        <h1> Playlists </h1>
        {
            playlists.map(playlist => {
                const availableSongs = songs.filter(song => !playlist.songs.map(pSong => pSong.song_id).includes(song.id));
                return (
                    <div key={playlist.playlistId}>
                        <h2>{playlist.playlistName}</h2>
                        <ul>
                            {playlist.songs.map(song => (
                                <li key={song.song_id}>
                                    {song.song_name}
                                        <button onClick = {() => redirectSongDetails(song._links.self.href)}>See song details</button>
                                </li>
                            ))}
                        </ul>
                        <button onClick={() => handleAddNewSong(playlist.playlistId)}>Add Song</button>
                        <select onChange={handleChange}>
                            {availableSongs.map(song => (
                                <option key={song.id} value={song.id}>
                                    {song.song_name}
                                </option>
                            ))}
                        </select>
                    </div>
                    )
                }
            )
        }
        <button onClick={redirectAddPlaylist}> Add playlist </button>
    </div>
)

    
        
                               


};



export default Playlists;
