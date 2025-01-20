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
    const [vehicles, setVehicles] = useState([]);
    const [newGarage, setNewGarage] = useState({
        name: "",
        latitude: "",
        longitude: "",
    });
    const [selectedVehicles, setSelectedVehicles] = useState({});
    const [markerPosition, setMarkerPosition] = useState(defaultCenter);

    const { isLoaded } = useLoadScript({
        googleMapsApiKey: "AIzaSyDavdCnLdO5lrvmQ5hHZV2VeXdV4ZF0lXU", // Uzupełnij klucz API
    });

    // Pobranie garaży z backendu
    const fetchGarages = async () => {
        try {
            const response = await api.get("/garages");
            setGarages(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania garaży:", error);
        }
    };

    // Pobranie pojazdów z backendu
    const fetchVehicles = async () => {
        try {
            const response = await api.get("/vehicles");
            setVehicles(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania pojazdów:", error);
        }
    };

    // Dodawanie garażu
    const addGarage = async () => {
        try {
            const response = await api.post("/garages", {
                ...newGarage,
                latitude: markerPosition.lat,
                longitude: markerPosition.lng,
            });
            setGarages((prev) => [...prev, response.data]);
            setNewGarage({ name: "", latitude: "", longitude: "" });
            alert("Garaż dodany!");
        } catch (error) {
            console.error("Błąd podczas dodawania garażu:", error);
            alert("Nie udało się dodać garażu.");
        }
    };

    // Usuwanie garażu
    const handleRemoveGarage = async (garageId) => {
        try {
            await api.delete(`/garages/${garageId}`);
            setGarages((prevGarages) => prevGarages.filter((garage) => garage.id !== garageId));
            alert("Garaż usunięty!");
        } catch (error) {
            console.error("Błąd podczas usuwania garażu:", error);
            alert("Nie udało się usunąć garażu.");
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
            setSelectedVehicles((prevState) => ({
                ...prevState,
                [garageId]: vehicleId,
            }));
            alert("Pojazd przypisany do garażu!");
            fetchGarages(); // Odśwież listę garaży
        } catch (error) {
            console.error("Błąd podczas przypisywania pojazdu:", error);
            alert("Nie udało się przypisać pojazdu.");
        }
    };

    // Usuwanie pojazdu z garażu
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

    // Pobranie danych po załadowaniu komponentu
    useEffect(() => {
        fetchGarages();
        fetchVehicles();
    }, []);

    if (!isLoaded) return <p>Ładowanie mapy...</p>;

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
                <div>
                    <label>Nazwa:</label>
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
                        center={markerPosition}
                        zoom={10}
                        onClick={(event) => {
                            setMarkerPosition({
                                lat: event.latLng.lat(),
                                lng: event.latLng.lng(),
                            });
                        }}
                    >
                        <Marker position={markerPosition} />
                    </GoogleMap>
                </div>
                <button type="submit">Dodaj Garaż</button>
            </form>

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
                        <button onClick={() => handleRemoveGarage(garage.id)}>Usuń garaż</button>
                    </div>
                ))
            ) : (
                <p>Brak garaży do wyświetlenia.</p>
            )}
        </div>
    );
}

export default GarageManager;
