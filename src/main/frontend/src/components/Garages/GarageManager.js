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
        latitude: "",
        longitude: "",
    });
    const [garageVehicles, setGarageVehicles] = useState({});
    const [vehicles, setVehicles] = useState([]);
    const [markerPosition, setMarkerPosition] = useState(defaultCenter);
    const [selectedCoordinates, setSelectedCoordinates] = useState(defaultCenter); // Współrzędne mapy
    const [selectedVehicles, setSelectedVehicles] = useState({});

    const { isLoaded } = useLoadScript({
        googleMapsApiKey: "YOUR_API_KEY", // Replace with your API key
    });

    const fetchGarages = async () => {
        try {
            const response = await api.get("/garages");
            setGarages(response.data);
        } catch (error) {
            console.error("Error fetching garages:", error);
        }
    };
    const handleRemoveVehicleFromGarage = async (garageId, vehicleId) => {
        try {
            await api.delete(`/garages/${garageId}/vehicles/${vehicleId}`);
            alert("Pojazd usunięty z garażu!");
            fetchGarages(); // Odśwież listę garaży
        } catch (error) {
            console.error("Błąd podczas usuwania pojazdu z garażu:", error);
            alert("Nie udało się usunąć pojazdu.");
        }
    };
    // Pobranie pojazdów z backendu
    const fetchVehicles = async () => {
        try {
            const response = await api.get("/vehicles");
            setVehicles(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania pojazdów:", error);
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

    // Przypisywanie pojazdu do garażu
    const handleAssignVehicle = async (garageId, vehicleId) => {
        if (!vehicleId || !garageId) {
            alert("Wybierz poprawny pojazd i garaż.");
            return;
        }

        try {
            await api.post(`/garages/${garageId}/vehicles/${vehicleId}`);
            const assignedVehicle = vehicles.find((v) => v.id === vehicleId);
            setGarageVehicles((prev) => ({
                ...prev,
                [garageId]: [...(prev[garageId] || []), assignedVehicle]
            }));

            setSelectedVehicles((prevState) => ({
                ...prevState,
                [garageId]: vehicleId
            }));

            alert("Pojazd przypisany do garażu!");
            fetchGarages();
        } catch (error) {
            console.error("Błąd podczas przypisywania pojazdu:", error);
            alert("Nie udało się przypisać pojazdu.");
        }
    };


    useEffect(() => {
        fetchGarages();
        fetchVehicles();
    }, []);

    if (!isLoaded) return <p>Ładowanie mapy...</p>;

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
                <h2>Lista Garaży</h2>
                {Array.isArray(garages) && garages.length > 0 ? (
                    garages.map((garage) => (
                        <div key={garage.id}>
                            <h3>{garage.name}</h3>
                            <p>Lokalizacja: {garage.latitude}, {garage.longitude}</p>
                            <div>
                                <label>Przypisz pojazd:</label>
                                <select
                                    value={selectedVehicles[garage.id] || ""}
                                    onChange={(e) => handleAssignVehicle(garage.id, e.target.value)}
                                >
                                    <option value="">-- Wybierz pojazd --</option>
                                    {Array.isArray(vehicles) && vehicles.length > 0 ? (
                                        vehicles.map((vehicle) => (
                                            vehicle && (
                                                <option key={vehicle.id} value={vehicle.id}>
                                                    {vehicle.name} - {vehicle.type}
                                                </option>
                                            )
                                        ))
                                    ) : (
                                        <option disabled>Brak pojazdów</option>
                                    )}
                                </select>
                            </div>
                            <h4>Pojazdy w garażu:</h4>
                            <ul>
                                {garage.vehicles && garage.vehicles.length > 0 ? (
                                    garage.vehicles.map((vehicle) => (
                                        <li key={vehicle.id}>
                                            {vehicle.name} - {vehicle.type}
                                            <button
                                                onClick={() =>
                                                    handleRemoveVehicleFromGarage(garage.id, vehicle.id)
                                                }
                                            >
                                                Usuń z garażu
                                            </button>
                                        </li>
                                    ))
                                ) : (
                                    <p>Brak pojazdów w tym garażu.</p>
                                )}
                            </ul>
                            <button onClick={() => deleteGarage(garage.id)}>Usuń garaż</button>
                        </div>
                    ))
                ) : (
                    <p>Brak garaży do wyświetlenia.</p>
                )}
            </div>
        </div>
    );
}

export default GarageManager;
