import HeaderNav from './components/HeaderNav.tsx';
import Schedule from './pages/Schedule.tsx';
import Search from './pages/Search.tsx';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <>
      <Router>
        <HeaderNav />
        <Routes>
            <Route path="/" element={<Schedule />} />
            <Route path="/search" element={<Search />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
