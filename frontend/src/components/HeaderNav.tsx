import React from 'react';
import './HeaderNav.css';

const HeaderNav = () => {
  return (
    <div class="header-nav">
    <img src="%PUBLIC_URL%/gcc-logo-primary.png" alt="logo" />
    <a href="/">My Schedule</a>
    <a href="/search">Course Search</a>
    <a href="/#export">Export</a>
    </div>
  );
}

export default HeaderNav;