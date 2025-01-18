import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Auth/Login";
import Register from "./components/Auth/Register";
import MainMenu from "./components/MainMenu/MainMenu";
import FleetManager from "./components/Fleet/FleetManager";
import WarehouseManager from "./components/Warehouses/WarehouseManager";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/menu" element={<MainMenu />} />
                <Route path="/fleet" element={<FleetManager />} />
                <Route path="/warehouses" element={<WarehouseManager />} />
                {/* Trasa do wytyczania tras (do zaimplementowania później) */}
                <Route path="/routes" element={<div>Wytyczanie trasy (do zrobienia)</div>} />
            </Routes>
        </Router>
    );
}

export default App;
