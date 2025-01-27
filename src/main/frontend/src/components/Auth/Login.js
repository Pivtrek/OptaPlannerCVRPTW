// Updated Login.js with Bootstrap styling and registration button
import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post("http://localhost:8080/api/auth/login", {
                username,
                password,
            });
            const token = response.data.token;
            localStorage.setItem("token", token); // Save token to localStorage
            alert("Successfully logged in!");
            navigate("/menu"); // Redirect to the main menu
        } catch (error) {
            console.error("Error during login:", error);
            alert("Login failed. Please check your credentials and try again.");
        }
    };

    return (
        <div className="container d-flex justify-content-center align-items-center vh-100">
            <div className="card p-4 shadow-lg">
                <h2 className="text-center">Logowanie</h2>
                <form onSubmit={handleLogin}>
                    <div className="mb-3">
                        <label className="form-label">Nazwa użytkownika:</label>
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            className="form-control"
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Hasło:</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="form-control"
                        />
                    </div>
                    <button type="submit" className="btn btn-primary w-100 mb-3">Zaloguj</button>
                </form>
                <div className="text-center">
                    <p className="mb-0">Nie masz konta?</p>
                    <button className="btn btn-link" onClick={() => navigate("/register")}>Zarejestruj się</button>
                </div>
            </div>
        </div>
    );
}

export default Login;
