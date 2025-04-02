import React from 'react';
import './HeaderNav.css';
import logo from './gcc-logo-primary.png';

const HeaderNav = () => {
  return (
    <div class="header-nav">
    <img src={logo} alt="logo" />
    <a href="/">My Schedule</a>
    <a href="/search">Course Search</a>
    <a href="/#export">Export</a>
    </div>
  );
}

export default HeaderNav;