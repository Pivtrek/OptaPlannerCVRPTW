//API: AIzaSyDavdCnLdO5lrvmQ5hHZV2VeXdV4ZF0lXU

import React, { useState, useEffect } from "react";
import api from "../../api/axios";
import { GoogleMap, Marker, useLoadScript } from "@react-google-maps/api";

const mapContainerStyle = {
    width: "100%",
    height: "400px",
};

const defaultCenter = {
    lat: 52.2296756, // Warszawa jako domyślna lokalizacja
    lng: 21.0122287,
};

function WarehouseManager() {
    const [warehouses, setWarehouses] = useState([]);
    const [newWarehouse, setNewWarehouse] = useState({
        name: "",
        address: "",
        latitude: "",
        longitude: "",
        openingHours: "",
    });
    const [selectedLocation, setSelectedLocation] = useState(defaultCenter);

    const { isLoaded } = useLoadScript({
        googleMapsApiKey: "AIzaSyDavdCnLdO5lrvmQ5hHZV2VeXdV4ZF0lXU", // Wklej tutaj swój klucz API Google Maps
    });

    const fetchWarehouses = async () => {
        try {
            const response = await api.get("/warehouses");
            setWarehouses(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania magazynów:", error);
        }
    };

    const deleteWarehouse = async (id) => {
        try {
            await api.delete(`/warehouses/${id}`);
            setWarehouses((prevWarehouses) =>
                prevWarehouses.filter((warehouse) => warehouse.id !== id)
            );
            alert("Magazyn został usunięty.");
        } catch (error) {
            console.error("Błąd podczas usuwania magazynu:", error);
            alert("Nie udało się usunąć magazynu.");
        }
    };

    const addWarehouse = async () => {
        try {
            const response = await api.post("/warehouses", {
                ...newWarehouse,
                latitude: selectedLocation.lat,
                longitude: selectedLocation.lng,
            });
            setWarehouses((prev) => [...prev, response.data]);
            setNewWarehouse({
                name: "",
                address: "",
                latitude: "",
                longitude: "",
                openingHours: "",
            });
            alert("Magazyn został dodany!");
        } catch (error) {
            console.error("Błąd podczas dodawania magazynu:", error);
            alert("Nie udało się dodać magazynu.");
        }
    };

    useEffect(() => {
        fetchWarehouses();
    }, []);

    if (!isLoaded) return <div>Ładowanie mapy...</div>;

    return (
        <div>
            <h1>Zarządzanie Magazynami</h1>

            {/* Formularz dodawania magazynu */}
            <h2>Dodaj Magazyn</h2>
            <form
                onSubmit={(e) => {
                    e.preventDefault();
                    addWarehouse();
                }}
            >
                <div>
                    <label>Nazwa:</label>
                    <input
                        type="text"
                        value={newWarehouse.name}
                        onChange={(e) =>
                            setNewWarehouse({ ...newWarehouse, name: e.target.value })
                        }
                        required
                    />
                </div>
                <div>
                    <label>Adres:</label>
                    <input
                        type="text"
                        value={newWarehouse.address}
                        onChange={(e) =>
                            setNewWarehouse({ ...newWarehouse, address: e.target.value })
                        }
                        required
                    />
                </div>
                <div>
                    <label>Godziny otwarcia:</label>
                    <input
                        type="text"
                        value={newWarehouse.openingHours}
                        onChange={(e) =>
                            setNewWarehouse({ ...newWarehouse, openingHours: e.target.value })
                        }
                    />
                </div>
                <button type="submit">Dodaj Magazyn</button>
            </form>

            {/* Mapa Google */}
            <h2>Wybierz Lokalizację na Mapie</h2>
            <GoogleMap
                mapContainerStyle={mapContainerStyle}
                zoom={10}
                center={defaultCenter}
                onClick={(event) =>
                    setSelectedLocation({
                        lat: event.latLng.lat(),
                        lng: event.latLng.lng(),
                    })
                }
            >
                {/* Marker wskazuje wybraną lokalizację */}
                <Marker position={selectedLocation} />
            </GoogleMap>


            <h2>Lista Magazynów</h2>
            {warehouses.length === 0 ? (
                <p>Brak magazynów do wyświetlenia.</p>
            ) : (
                <ul>
                    {warehouses.map((warehouse) => (
                        <li key={warehouse.id}>
                            {warehouse.name} - {warehouse.address} - {warehouse.latitude},{" "}
                            {warehouse.longitude} - {warehouse.openingHours}
                            <button onClick={() => deleteWarehouse(warehouse.id)}>Usuń</button>
                        </li>
                    ))}
                </ul>
            )}

        </div>
    );
}

export default WarehouseManager;