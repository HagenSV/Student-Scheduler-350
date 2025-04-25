import React from 'react';
import './HeaderNav.css';
const logo = require('./gcc-logo-primary.png');

const HeaderNav: React.FC = () => {
  return (
    <div className="header-nav">
    <a href="/"><img src={logo} alt="logo" /></a>
    <ul>
        <li><a href="/">My Schedule</a></li>
        <li><a href="/search">Course Search</a></li>
        <li><a href="/#export">Export</a></li>
        <li className="profile-button"><a href="/profile">Profile</a></li>
    </ul>
    </div>
  );
}

export default HeaderNav;