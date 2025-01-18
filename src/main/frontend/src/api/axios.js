import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`, // Pobieranie tokena z localStorage
        "Content-Type": "application/json"
    },
    withCredentials: true // Ważne dla ciasteczek i CORS
});

export default api;