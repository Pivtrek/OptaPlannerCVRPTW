// Updated with Bootstrap styling for Register.js
import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function Register() {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await axios.post("http://localhost:8080/api/auth/register", {
                username,
                email,
                password,
            });
            alert("Rejestracja zakończona sukcesem! Możesz się teraz zalogować.");
            navigate("/login"); // Redirect to login
        } catch (error) {
            console.error("Błąd podczas rejestracji:", error);
            alert("Nie udało się zarejestrować. Sprawdź dane i spróbuj ponownie.");
        }
    };

    return (
        <div className="container d-flex justify-content-center align-items-center vh-100">
            <div className="card p-4 shadow-lg">
                <h2 className="text-center">Rejestracja</h2>
                <form onSubmit={handleRegister}>
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
                        <label className="form-label">Email:</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
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
                    <button type="submit" className="btn btn-primary w-100">Zarejestruj się</button>
                </form>
                <div className="text-center mt-3">
                    <p className="mb-0">Masz już konto?</p>
                    <button className="btn btn-link" onClick={() => navigate("/login")}>Zaloguj się</button>
                </div>
            </div>
        </div>
    );
}

export default Register;
