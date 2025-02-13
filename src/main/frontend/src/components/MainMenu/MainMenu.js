// Updated with Bootstrap styling for MainMenu.js
import React from "react";
import { useNavigate } from "react-router-dom";

function MainMenu() {
    const navigate = useNavigate();

    return (
        <div className="container d-flex flex-column justify-content-center align-items-center vh-100">
            <div className="card p-4 shadow-lg text-center">
                <h1 className="mb-4">Menu Główne</h1>
                <button
                    onClick={() => navigate("/fleet")}
                    className="btn btn-primary w-100 mb-2"
                >
                    Edycja floty
                </button>
                <button
                    onClick={() => navigate("/warehouses")}
                    className="btn btn-primary w-100 mb-2"
                >
                    Edycja magazynów
                </button>
                <button
                    onClick={() => navigate("/garages")}
                    className="btn btn-primary w-100 mb-2"
                >
                    Edycja garaży
                </button>
                <button
                    onClick={() => navigate("/routes")}
                    className="btn btn-primary w-100"
                >
                    Wytyczanie trasy
                </button>
            </div>
        </div>
    );
}

export default MainMenu;

