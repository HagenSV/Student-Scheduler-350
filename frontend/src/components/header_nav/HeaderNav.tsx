import React from 'react';
import './HeaderNav.css';
import logo from './gcc-logo-primary.png';

const HeaderNav = () => {
  return (
    <div class="header-nav">
    <img src={logo} alt="logo" />
    <ul>
        <li><a href="/">My Schedule</a></li>
        <li><a href="/search">Course Search</a></li>
        <li><a href="/#export">Export</a></li>
    </ul>
    </div>
  );
}

export default HeaderNav;