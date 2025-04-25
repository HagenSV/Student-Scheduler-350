import React, { useEffect, useState } from 'react';
import './HeaderNav.css';
const logo = require('./gcc-logo-primary.png');

const HeaderNav: React.FC = () => {
    const [name, setName] = useState<String>("Login");

    useEffect(() => {
        const fetchData = async () =>{
            const response = await fetch('/api/v1/name');

            if (response.ok){
                const json = await response.text();
                if (json){
                    setName(json);
                } else {
                    setName("Login");
                }
            }
        }
        fetchData()
    })


  return (
    <div className="header-nav">
    <a href="/"><img src={logo} alt="logo" /></a>
    <ul>
        <li><a href="/">My Schedule</a></li>
        <li><a href="/search">Course Search</a></li>
        <li><a href="/#export">Export</a></li>
        <li className="profile-button"><a href="/profile">{ name }</a></li>
    </ul>
    </div>
  );
}

export default HeaderNav;