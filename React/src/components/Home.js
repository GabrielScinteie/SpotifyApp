import React, { useState, useEffect } from 'react';
import XMLParser from "react-xml-parser/xmlParser";
import { useNavigate } from 'react-router-dom';
import './Playlists.css';

const Home = ({jwt, roles, setRoles, id, setId}) => {
  const navigate = useNavigate();
  const [songs, setSongs] = useState([]);

     useEffect(() => {
            const soapRequest = '<soap11env:Envelope xmlns:soap11env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sample="services.dbManager.soap">' +
              '<soap11env:Body>' +
              '<sample:authorize>'+
              '<sample:my_jwt>'+jwt+'</sample:my_jwt>' +
              '</sample:authorize>' +
              '</soap11env:Body>' +
              '</soap11env:Envelope>';
          var xmlhttp = new XMLHttpRequest();
          xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                var xml = new XMLParser().parseFromString(xmlhttp.responseText);
                var response = xml.getElementsByTagName('tns:authorizeResult')[0].value.split("||")
                setId(response[0]);
                var roles1 = []
                for (let i = 1; i < response.length; i++) {
                    roles1.push(response[i]);
                }
                setRoles(roles1)
            } else if (xmlhttp.readyState === 4) {
                console.error(xmlhttp.response);
            }
          };
          xmlhttp.open('POST',"http://127.0.0.1:7999",true);
          xmlhttp.send(soapRequest);
      }, []);

  useEffect(() => {
    fetch('http://localhost:8080/api/songs')
      .then(response => response.json())
      .then(data => setSongs(data._embedded.songDTOList))
      .catch(err => console.log(err));
  }, []);

  const handleClick = () => {
          const soapRequest = '<soap11env:Envelope xmlns:soap11env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sample="services.dbManager.soap">' +
              '<soap11env:Body>' +
              '<sample:logout>'+
              '<sample:my_jwt>'+jwt+'</sample:my_jwt>' +
              '</sample:logout>' +
              '</soap11env:Body>' +
              '</soap11env:Envelope>';
          var xmlhttp = new XMLHttpRequest();
          xmlhttp.onreadystatechange = function() {
              if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                  navigate('/');
              } else if (xmlhttp.readyState === 4) {
                  console.error(xmlhttp.response);
              }
          };
          xmlhttp.open('POST',"http://127.0.0.1:7999",true);
          xmlhttp.send(soapRequest);
      };

    const redirectAddArtist = () => {
        console.log('content manager')
        navigate('/addArtist')
    }

    const redirectAddSong = () => {
        console.log("artist");
        navigate('/addSong')
    }

    const redirectPlaylist = () => {
       navigate('/playlists')
    }

  return (
    <div>
      <h1>Songs</h1>
      <ul>
        {songs.map(song => (
          <li key={song.id}>
            <b>{song.song_name} </b> <br></br>
            {song.music_genre} - {song.elem_type} - {song.release_year}
          </li>
        ))}
      </ul>
      <button onClick={handleClick}>Logout</button>
      {roles.indexOf("content manager") !== -1 ?(<button className="submit" onClick={redirectAddArtist}> Add artist </button>):<p></p>}
        {roles.indexOf("artist") !== -1 ?(<button className="submit" onClick={redirectAddSong}> Add song </button>):<p></p>}
        {roles.indexOf("client") !== -1 ?(<button className="submit" onClick={redirectPlaylist}> Playlists </button>):<p></p>}
    </div>
  );
};

export default Home;
