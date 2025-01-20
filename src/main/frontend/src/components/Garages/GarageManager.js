import React, { useState, useEffect } from "react";
import api from "../../api/axios";
import { GoogleMap, Marker, useLoadScript } from "@react-google-maps/api";

const mapContainerStyle = {
    width: "100%",
    height: "400px",
};

const defaultCenter = {
    lat: 52.2296756,
    lng: 21.0122287, // Warszawa jako domyślne centrum
};

function GarageManager() {
    const [garages, setGarages] = useState([]); // Lista garaży
    const [vehicles, setVehicles] = useState([]); // Lista pojazdów
    const [garageVehicles, setGarageVehicles] = useState({}); // Pojazdy przypisane do garaży
    const [newGarage, setNewGarage] = useState({
        name: "",
        latitude: defaultCenter.lat,
        longitude: defaultCenter.lng,
    });
    const [selectedCoordinates, setSelectedCoordinates] = useState(defaultCenter); // Współrzędne mapy

    const { isLoaded } = useLoadScript({
        googleMapsApiKey: "AIzaSyDavdCnLdO5lrvmQ5hHZV2VeXdV4ZF0lXU", // Wstaw swój klucz API
    });

    // Pobieranie garaży z backendu
    const fetchGarages = async () => {
        try {
            const response = await api.get("/garages");
            setGarages(response.data);

            // Inicjalizacja przypisanych pojazdów
            const initialGarageVehicles = {};
            response.data.forEach((garage) => {
                initialGarageVehicles[garage.id] = garage.vehicles || [];
            });
            setGarageVehicles(initialGarageVehicles);
        } catch (error) {
            console.error("Błąd podczas pobierania garaży:", error);
        }
    };

    // Pobieranie pojazdów
    const fetchVehicles = async () => {
        try {
            const response = await api.get("/vehicles");
            setVehicles(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania pojazdów:", error);
        }
    };

    // Obsługa dodawania garażu
    const addGarage = async () => {
        try {
            const response = await api.post("/garages", newGarage);
            setGarages((prevGarages) => [...prevGarages, response.data]);
            setNewGarage({
                name: "",
                latitude: defaultCenter.lat,
                longitude: defaultCenter.lng,
            }); // Reset danych garażu
            alert("Garaż dodany!");
        } catch (error) {
            console.error("Błąd podczas dodawania garażu:", error);
            alert("Nie udało się dodać garażu.");
        }
    };

    // Obsługa przypisywania pojazdów
    const assignVehicleToGarage = async (garageId, vehicleId) => {
        try {
            await api.post(`/garages/${garageId}/vehicles/${vehicleId}`);
            setGarageVehicles((prev) => ({
                ...prev,
                [garageId]: [...prev[garageId], vehicles.find((v) => v.id === vehicleId)],
            }));
            alert("Pojazd przypisany do garażu!");
        } catch (error) {
            console.error("Błąd podczas przypisywania pojazdu:", error);
            alert("Nie udało się przypisać pojazdu.");
        }
    };

    // Obsługa usuwania pojazdów
    const removeVehicleFromGarage = async (garageId, vehicleId) => {
        try {
            await api.delete(`/garages/${garageId}/vehicles/${vehicleId}`);
            setGarageVehicles((prev) => ({
                ...prev,
                [garageId]: prev[garageId].filter((v) => v.id !== vehicleId),
            }));
            alert("Pojazd usunięty z garażu!");
        } catch (error) {
            console.error("Błąd podczas usuwania pojazdu:", error);
            alert("Nie udało się usunąć pojazdu.");
        }
    };

    useEffect(() => {
        fetchGarages();
        fetchVehicles();
    }, []);

    return (
        <div>
            <h1>Zarządzanie Garażami</h1>

            <h2>Dodaj Garaż</h2>
            <form
                onSubmit={(e) => {
                    e.preventDefault();
                    addGarage();
                }}
            >
                <input
                    type="text"
                    placeholder="Nazwa garażu"
                    value={newGarage.name}
                    onChange={(e) => setNewGarage({...newGarage, name: e.target.value})}
                    required
                />
                <div>
                    <label>Wybierz lokalizację na mapie:</label>
                    <GoogleMap
                        mapContainerStyle={mapContainerStyle}
                        zoom={12}
                        center={defaultCenter}
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

            <h2>Lista Garaży</h2>
            {garages.map((garage) => (
                <div key={garage.id} style={{border: "1px solid black", padding: "10px", margin: "10px"}}>
                    <h3>{garage.name}</h3>
                    <p>Współrzędne: {garage.latitude}, {garage.longitude}</p>

                    <h4>Pojazdy przypisane:</h4>
                    <ul>
                        {garageVehicles[garage.id]?.map((vehicle) => (
                            <li key={vehicle.id}>
                                {vehicle.name} - {vehicle.type}
                                <button onClick={() => removeVehicleFromGarage(garage.id, vehicle.id)}>Usuń</button>
                            </li>
                        ))}
                    </ul>

                    <h4>Przypisz pojazd:</h4>
                    <select
                        onChange={(e) => assignVehicleToGarage(garage.id, e.target.value)}
                        defaultValue=""
                    >
                        <option value="" disabled>
                            Wybierz pojazd
                        </option>
                        {vehicles.map((vehicle) => (
                            <option key={vehicle.id} value={vehicle.id}>
                                {vehicle.name} - {vehicle.type}
                            </option>
                        ))}
                    </select>
                </div>
            ))}
        </div>
    );
}

export default GarageManager;