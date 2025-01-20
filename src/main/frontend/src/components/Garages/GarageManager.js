import React, { useState, useEffect } from "react";
import api from "../../api/axios";
import GoogleMapReact from "google-map-react";

function GarageManager() {
    const [garages, setGarages] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [newGarage, setNewGarage] = useState({ name: "", latitude: null, longitude: null });
    const [selectedGarage, setSelectedGarage] = useState(null);
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const [mapCenter, setMapCenter] = useState({ lat: 52.2296756, lng: 21.0122287 });

    const fetchGarages = async () => {
        try {
            const response = await api.get("/garages");
            setGarages(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania garaży:", error);
        }
    };

    const fetchVehicles = async () => {
        try {
            const response = await api.get("/vehicles");
            setVehicles(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania pojazdów:", error);
        }
    };

    const addGarage = async () => {
        try {
            const response = await api.post("/garages", newGarage);
            setGarages((prev) => [...prev, response.data]);
            setNewGarage({ name: "", latitude: null, longitude: null });
            alert("Garaż został dodany!");
        } catch (error) {
            console.error("Błąd podczas dodawania garażu:", error);
        }
    };

    const deleteGarage = async (id) => {
        try {
            await api.delete(`/garages/${id}`);
            setGarages((prev) => prev.filter((garage) => garage.id !== id));
            alert("Garaż został usunięty!");
        } catch (error) {
            console.error("Błąd podczas usuwania garażu:", error);
        }
    };

    const assignVehicleToGarage = async (garageId) => {
        try {
            await api.post(`/garages/${garageId}/vehicles`, { vehicleId: selectedVehicle });
            alert("Pojazd został przypisany do garażu!");
            setSelectedVehicle(null);
        } catch (error) {
            console.error("Błąd podczas przypisywania pojazdu do garażu:", error);
        }
    };

    useEffect(() => {
        fetchGarages();
        fetchVehicles();
    }, []);

    return (
        <div>
            <h1>Zarządzanie Garażami</h1>

            {/* Dodawanie nowego garażu */}
            <h2>Dodaj Garaż</h2>
            <form
                onSubmit={(e) => {
                    e.preventDefault();
                    addGarage();
                }}
            >
                <div>
                    <label>Nazwa Garażu:</label>
                    <input
                        type="text"
                        value={newGarage.name}
                        onChange={(e) => setNewGarage({ ...newGarage, name: e.target.value })}
                        required
                    />
                </div>
                <div style={{ height: "400px", width: "100%" }}>
                    <GoogleMapReact
                        bootstrapURLKeys={{ key: "<YOUR_GOOGLE_MAPS_API_KEY>" }}
                        center={mapCenter}
                        defaultZoom={11}
                        onClick={({ lat, lng }) => setNewGarage({ ...newGarage, latitude: lat, longitude: lng })}
                    >
                        {newGarage.latitude && newGarage.longitude && (
                            <div
                                lat={newGarage.latitude}
                                lng={newGarage.longitude}
                                style={{
                                    color: "red",
                                    fontSize: "20px",
                                    fontWeight: "bold",
                                }}
                            >
                                📍
                            </div>
                        )}
                    </GoogleMapReact>
                </div>
                <button type="submit">Dodaj Garaż</button>
            </form>

            {/* Lista garaży */}
            <h2>Lista Garaży</h2>
            {garages.length === 0 ? (
                <p>Brak garaży do wyświetlenia.</p>
            ) : (
                <ul>
                    {garages.map((garage) => (
                        <li key={garage.id}>
                            <strong>{garage.name}</strong> ({garage.latitude}, {garage.longitude})
                            <button onClick={() => deleteGarage(garage.id)}>Usuń</button>
                            <div>
                                <label>Przypisz pojazd:</label>
                                <select
                                    value={selectedVehicle || ""}
                                    onChange={(e) => setSelectedVehicle(e.target.value)}
                                >
                                    <option value="" disabled>Wybierz pojazd</option>
                                    {vehicles.map((vehicle) => (
                                        <option key={vehicle.id} value={vehicle.id}>
                                            {vehicle.name}
                                        </option>
                                    ))}
                                </select>
                                <button onClick={() => assignVehicleToGarage(garage.id)}>
                                    Przypisz
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default GarageManager;