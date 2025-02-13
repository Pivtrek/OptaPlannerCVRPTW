// Updated with Bootstrap styling for FleetManager.js
import React, { useState, useEffect } from "react";
import api from "../../api/axios";

function FleetManager() {
    const [vehicles, setVehicles] = useState([]);
    const [newVehicle, setNewVehicle] = useState({ name: "", capacity: "", type: "" });

    // Fetch vehicles from backend
    const fetchVehicles = async () => {
        try {
            const response = await api.get("/vehicles");
            setVehicles(response.data);
        } catch (error) {
            console.error("Error fetching vehicles:", error);
        }
    };

    // Add a new vehicle
    const addVehicle = async () => {
        try {
            const response = await api.post("/vehicles", newVehicle);
            setVehicles((prev) => [...prev, response.data]);
            setNewVehicle({ name: "", capacity: "", type: "" });
            alert("Vehicle added successfully!");
        } catch (error) {
            console.error("Error adding vehicle:", error);
            alert("Failed to add vehicle.");
        }
    };

    // Delete a vehicle
    const deleteVehicle = async (id) => {
        try {
            await api.delete(`/vehicles/${id}`);
            setVehicles((prev) => prev.filter((vehicle) => vehicle.id !== id));
            alert("Vehicle deleted successfully!");
        } catch (error) {
            console.error("Error deleting vehicle:", error);
            alert("Failed to delete vehicle.");
        }
    };

    useEffect(() => {
        fetchVehicles();
    }, []);

    return (
        <div className="container py-5">
            <div className="card p-4 shadow-lg">
                <h1 className="text-center mb-4">Zarządzanie Flotą</h1>

                <h2 className="mb-3">Dodaj pojazd</h2>
                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        addVehicle();
                    }}
                >
                    <div className="mb-3">
                        <label className="form-label">Nazwa:</label>
                        <input
                            type="text"
                            value={newVehicle.name}
                            onChange={(e) => setNewVehicle({ ...newVehicle, name: e.target.value })}
                            required
                            className="form-control"
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Pojemność (kg):</label>
                        <input
                            type="number"
                            value={newVehicle.capacity}
                            onChange={(e) => setNewVehicle({ ...newVehicle, capacity: e.target.value })}
                            required
                            className="form-control"
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Typ:</label>
                        <input
                            type="text"
                            value={newVehicle.type}
                            onChange={(e) => setNewVehicle({ ...newVehicle, type: e.target.value })}
                            required
                            className="form-control"
                        />
                    </div>
                    <button type="submit" className="btn btn-primary w-100">Dodaj pojazd</button>
                </form>
            </div>

            <div className="mt-5">
                <h2 className="mb-3">Lista pojazdów</h2>
                {vehicles.length === 0 ? (
                    <p className="text-muted">Brak pojazdów do wyświetlenia.</p>
                ) : (
                    <ul className="list-group">
                        {vehicles.map((vehicle) => (
                            <li key={vehicle.id} className="list-group-item d-flex justify-content-between align-items-center">
                                {vehicle.name} - {vehicle.capacity} kg - {vehicle.type}
                                <button
                                    onClick={() => deleteVehicle(vehicle.id)}
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

export default FleetManager;
