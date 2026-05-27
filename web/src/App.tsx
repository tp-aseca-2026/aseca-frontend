import { BrowserRouter, Route, Routes } from "react-router";
import { LandingPage } from "./pages/LandingPage";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { HomePage } from "./pages/HomePage";
import { EdgarPage } from "./pages/EdgarPage";
import { WatchlistPage } from "./pages/WatchlistPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/edgar" element={<EdgarPage />} />
        <Route path="/watchlist" element={<WatchlistPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;