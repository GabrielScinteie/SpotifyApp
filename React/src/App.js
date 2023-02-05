import './App.css';
import React, { useState, useEffect } from 'react';
import Login from './components/Login';
import Home from './components/Home';

import { BrowserRouter, Route , Routes} from "react-router-dom";
import AddArtist from './components/AddArtist';
import AddSong from './components/AddSong';
import Playlists from './components/Playlists';
import AddPlaylist from './components/AddPlaylist';
import SongDetails from './components/SongDetails'

function App() {
    const [jwt, setJWT] = useState('')
    const [roles, setRoles] = useState([])
    const [playlistId, setPlaylistId] = useState()
    const [id, setId] = useState('')
    const [url, setUrl] = useState('')

  return (
    <BrowserRouter>
        <div>
            <Routes>
                <Route path="/" exact element={<Login setJWT = {setJWT} jwt = {jwt}/>}/>
                <Route path="/home" element={<Home jwt = {jwt} roles = {roles} setRoles = {setRoles} id = {id} setId = {setId} />} />
                <Route path="/addArtist" element={<AddArtist jwt = {jwt}/>} />
                <Route path="/addArtist" element={<AddArtist jwt = {jwt}/>} />
                <Route path="/addSong" element={<AddSong jwt = {jwt}/>} />
                <Route path="/playlists" element={<Playlists jwt = {jwt} setUrl = {setUrl}/>} />
                <Route path="/addPlaylist" element={<AddPlaylist jwt = {jwt}/>} />
                <Route path="/songDetails" element={<SongDetails url = {url}/>} />
                {/* <Route path="/addSongToPlaylist" element={<AddSongToPlaylist jwt = {jwt} playlistId = {playlistId} />} /> */}
            </Routes>
        </div>
    </BrowserRouter>
  );
}


export default App;
