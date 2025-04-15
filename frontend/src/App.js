import HeaderNav from './components/header_nav/HeaderNav.tsx';
import Schedule from './pages/Schedule.tsx';
import Search from './pages/Search.tsx';
import Account from './pages/Account.tsx';
import Profile from './pages/Profile.tsx';
import Login from './pages/Login.tsx';
import Register from './pages/Register.tsx';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <>
      <Router>
        <HeaderNav />
        <Routes>
            <Route path="/" element={<Schedule />} />
            <Route path="/search" element={<Search />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/account" element={<Account />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
