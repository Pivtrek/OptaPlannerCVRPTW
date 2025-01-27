// Updated with Bootstrap styling for WarehouseManager.js
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

function WarehouseManager() {
    const [warehouses, setWarehouses] = useState([]);
    const [newWarehouse, setNewWarehouse] = useState({
        name: "",
        address: "",
    });
    const [selectedLocation, setSelectedLocation] = useState(defaultCenter);

    const { isLoaded } = useLoadScript({
        googleMapsApiKey: "AIzaSyDavdCnLdO5lrvmQ5hHZV2VeXdV4ZF0lXU", // Replace with your API key
    });

    const fetchWarehouses = async () => {
        try {
            const response = await api.get("/warehouses");
            setWarehouses(response.data);
        } catch (error) {
            console.error("Error fetching warehouses:", error);
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
            setNewWarehouse({ name: "", address: "" });
            alert("Warehouse added successfully!");
        } catch (error) {
            console.error("Error adding warehouse:", error);
            alert("Failed to add warehouse.");
        }
    };

    const deleteWarehouse = async (id) => {
        try {
            await api.delete(`/warehouses/${id}`);
            setWarehouses((prev) => prev.filter((warehouse) => warehouse.id !== id));
            alert("Warehouse deleted successfully!");
        } catch (error) {
            console.error("Error deleting warehouse:", error);
            alert("Failed to delete warehouse.");
        }
    };

    useEffect(() => {
        fetchWarehouses();
    }, []);

    if (!isLoaded) return <p>Loading map...</p>;

    return (
        <div className="container py-5">
            <div className="card p-4 shadow-lg">
                <h1 className="text-center mb-4">Zarządzanie Magazynami</h1>

                <h2 className="mb-3">Dodaj Magazyn</h2>
                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        addWarehouse();
                    }}
                >
                    <div className="mb-3">
                        <label className="form-label">Nazwa Magazynu:</label>
                        <input
                            type="text"
                            value={newWarehouse.name}
                            onChange={(e) => setNewWarehouse({ ...newWarehouse, name: e.target.value })}
                            required
                            className="form-control"
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Adres:</label>
                        <input
                            type="text"
                            value={newWarehouse.address}
                            onChange={(e) => setNewWarehouse({ ...newWarehouse, address: e.target.value })}
                            required
                            className="form-control"
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Wybierz Lokalizację:</label>
                        <GoogleMap
                            mapContainerStyle={mapContainerStyle}
                            center={selectedLocation}
                            zoom={10}
                            onClick={(event) =>
                                setSelectedLocation({
                                    lat: event.latLng.lat(),
                                    lng: event.latLng.lng(),
                                })
                            }
                        >
                            <Marker position={selectedLocation} />
                        </GoogleMap>
                    </div>
                    <button type="submit" className="btn btn-primary w-100">Dodaj Magazyn</button>
                </form>
            </div>

            <div className="mt-5">
                <h2 className="mb-3">Lista Magazynów</h2>
                {warehouses.length === 0 ? (
                    <p className="text-muted">Brak magazynów do wyświetlenia.</p>
                ) : (
                    <ul className="list-group">
                        {warehouses.map((warehouse) => (
                            <li key={warehouse.id} className="list-group-item d-flex justify-content-between align-items-center">
                                {warehouse.name} - {warehouse.address} - {warehouse.latitude}, {warehouse.longitude}
                                <button
                                    onClick={() => deleteWarehouse(warehouse.id)}
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

export default WarehouseManager;
