import React, { useState, useEffect } from "react";
import api from "../../api/axios";
import { GoogleMap, Marker, useLoadScript } from "@react-google-maps/api";

function GarageManager() {
    const [garages, setGarages] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [newGarage, setNewGarage] = useState({ name: "", latitude: null, longitude: null });
    const [selectedGarage, setSelectedGarage] = useState(null);
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const [mapCenter, setMapCenter] = useState({ lat: 52.2296756, lng: 21.0122287 });

    const mapContainerStyle = {
        width: "100%",
        height: "400px",
    };

    const center = {
        lat: 52.2296756,
        lng: 21.0122287,
    };

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
                <div>
                    <label>Wybierz lokalizację na mapie:</label>
                    <GoogleMap
                        mapContainerStyle={mapContainerStyle}
                        zoom={12}
                        center={center}
                        onClick={(event) => {
                            setNewGarage({
                                ...newGarage,
                                latitude: event.latLng.lat(),
                                longitude: event.latLng.lng(),
                            });
                        }}
                    >
                        {newGarage.latitude && newGarage.longitude && (
                            <Marker position={{lat: newGarage.latitude, lng: newGarage.longitude}}/>
                        )}
                    </GoogleMap>
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
                                    onChange={(e) => assignVehicleToGarage(garage.id, e.target.value)}
                                    defaultValue=""

                                <option value="" disabled>
                                    Wybierz pojazd
                                </option>
                                    {vehicles.map((vehicle) => (
                                        <option key={vehicle.id} value={vehicle.id}>
                                            {vehicle.name} - {vehicle.type}
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