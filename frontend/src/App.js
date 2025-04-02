import HeaderNav from './components/HeaderNav.tsx';
import Schedule from './pages/Schedule.tsx';
import Search from './pages/Search.tsx';
import Profile from './pages/Profile.tsx';
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
        </Routes>
      </Router>
    </>
  );
}

export default App;
