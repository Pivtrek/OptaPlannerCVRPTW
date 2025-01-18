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
            navigate("/login"); // Powrót do logowania
        } catch (error) {
            console.error("Błąd podczas rejestracji:", error);
            alert("Nie udało się zarejestrować. Sprawdź dane i spróbuj ponownie.");
        }
    };

    return (
        <div>
            <h2>Rejestracja</h2>
            <form onSubmit={handleRegister}>
                <div>
                    <label>Nazwa użytkownika:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Hasło:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Zarejestruj się</button>
            </form>
        </div>
    );
}

export default Register;

