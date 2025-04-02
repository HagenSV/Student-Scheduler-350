import HeaderNav from './components/HeaderNav.tsx';
import Schedule from './pages/Schedule.tsx';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <>
      <Router>
        <HeaderNav />
        <Routes>
            <Route path="/" element={<Schedule />} />
            <Route path="/search" element={<Schedule />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
