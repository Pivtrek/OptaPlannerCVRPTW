import React from "react";
import { useNavigate } from "react-router-dom";

function MainMenu() {
    const navigate = useNavigate();

    return (
        <div>
            <h1>Menu Główne</h1>
            <button onClick={() => navigate("/fleet")}>Edycja floty</button>
            <button onClick={() => navigate("/warehouses")}>Edycja magazynów</button>
            <button onClick={() => navigate("/routes")}>Wytyczanie trasy</button>
        </div>
    );
}

export default MainMenu;
