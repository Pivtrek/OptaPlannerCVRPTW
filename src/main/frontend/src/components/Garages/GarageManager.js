// Updated with Bootstrap styling for GarageManager.js
import React, { useState, useEffect } from "react";
import api from "../../api/axios";
import { GoogleMap, Marker, useLoadScript } from "@react-google-maps/api";

const mapContainerStyle = {
    width: "100%",
    height: "400px",
};

const defaultCenter = {
    lat: 52.2296756,
    lng: 21.0122287,
};

function GarageManager() {
    const [garages, setGarages] = useState([]);
    const [newGarage, setNewGarage] = useState({
        name: "",
    });
    const [markerPosition, setMarkerPosition] = useState(defaultCenter);

    const { isLoaded } = useLoadScript({
        googleMapsApiKey: "AIzaSyDavdCnLdO5lrvmQ5hHZV2VeXdV4ZF0lXU", // Replace with your API key
    });

    const fetchGarages = async () => {
        try {
            const response = await api.get("/garages");
            setGarages(response.data);
        } catch (error) {
            console.error("Error fetching garages:", error);
        }
    };

    const addGarage = async () => {
        try {
            const response = await api.post("/garages", {
                ...newGarage,
                latitude: markerPosition.lat,
                longitude: markerPosition.lng,
            });
            setGarages((prev) => [...prev, response.data]);
            setNewGarage({ name: "" });
            alert("Garage added successfully!");
        } catch (error) {
            console.error("Error adding garage:", error);
            alert("Failed to add garage.");
        }
    };

    const deleteGarage = async (id) => {
        try {
            await api.delete(`/garages/${id}`);
            setGarages((prev) => prev.filter((garage) => garage.id !== id));
            alert("Garage deleted successfully!");
        } catch (error) {
            console.error("Error deleting garage:", error);
            alert("Failed to delete garage.");
        }
    };

    useEffect(() => {
        fetchGarages();
    }, []);

    if (!isLoaded) return <p>Loading map...</p>;

    return (
        <div className="container py-5">
            <div className="card p-4 shadow-lg">
                <h1 className="text-center mb-4">Zarządzanie Garażami</h1>

                <h2 className="mb-3">Dodaj Garaż</h2>
                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        addGarage();
                    }}
                >
                    <div className="mb-3">
                        <label className="form-label">Nazwa Garażu:</label>
                        <input
                            type="text"
                            value={newGarage.name}
                            onChange={(e) => setNewGarage({ name: e.target.value })}
                            required
                            className="form-control"
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Wybierz Lokalizację:</label>
                        <GoogleMap
                            mapContainerStyle={mapContainerStyle}
                            center={markerPosition}
                            zoom={10}
                            onClick={(event) =>
                                setMarkerPosition({
                                    lat: event.latLng.lat(),
                                    lng: event.latLng.lng(),
                                })
                            }
                        >
                            <Marker position={markerPosition} />
                        </GoogleMap>
                    </div>
                    <button type="submit" className="btn btn-primary w-100">Dodaj Garaż</button>
                </form>
            </div>

            <div className="mt-5">
                <h2 className="mb-3">Lista Garaży</h2>
                {garages.length === 0 ? (
                    <p className="text-muted">Brak garaży do wyświetlenia.</p>
                ) : (
                    <ul className="list-group">
                        {garages.map((garage) => (
                            <li key={garage.id} className="list-group-item d-flex justify-content-between align-items-center">
                                {garage.name} - {garage.latitude}, {garage.longitude}
                                <button
                                    onClick={() => deleteGarage(garage.id)}
                                    className="btn btn-danger btn-sm"
                                >
                                    Usuń
                                </button>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}

export default GarageManager;
