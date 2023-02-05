import React, { useState } from 'react';
import XMLParser from "react-xml-parser/xmlParser";
import { useNavigate } from 'react-router-dom';
import './Playlists.css';

const Login = ({setJWT, jwt}) =>{
    const navigate = useNavigate();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();
        const soapRequest = '<soap11env:Envelope xmlns:soap11env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sample="services.dbManager.soap">' +
            '<soap11env:Body>' +
            '<sample:login>'+
            '<sample:username>'+username+'</sample:username>' +
            '<sample:password>'+password+'</sample:password>' +
            '</sample:login>' +
            '</soap11env:Body>' +
            '</soap11env:Envelope>';
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
                var xml = new XMLParser().parseFromString(xmlhttp.response);
                //console.log(xml);
                jwt = xml.getElementsByTagName('tns:loginResult')[0].value;
                console.log(jwt);
                if(jwt === 'Parola gresita.')
                {
                    alert('Nume sau parola gresita');
                    navigate('/');
                }
                else
                {
                    setJWT(jwt);
                    navigate('/home');
                }
            } else if (xmlhttp.readyState === 4) {
                console.error(xmlhttp.response);
            }
        };
        xmlhttp.open('POST',"http://127.0.0.1:7999",true);
        xmlhttp.send(soapRequest);
    }

    return (
        <div className="login-container">
            <form onSubmit={handleSubmit}>
                <label>
                    Username:
                    <input required type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
                </label>
                <br />
                <label>
                    Password:
                    <input required type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                </label>
                <br />
                <button type="submit">Log in</button>
            </form>
        </div>
    );
}


export default Login;