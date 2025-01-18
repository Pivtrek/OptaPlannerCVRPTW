import React, { useState, useEffect } from "react";
import api from "../../api/axios";

function FleetManager() {
    const [vehicles, setVehicles] = useState([]);
    const [newVehicle, setNewVehicle] = useState({ name: "", capacity: "", type: "" });
    const [editingVehicle, setEditingVehicle] = useState(null); // Dodano brakującą zmienną

    // Pobieranie listy pojazdów z backendu
    const fetchVehicles = async () => {
        try {
            const response = await api.get("/vehicles", {
                params: { userId: 1 }, // Przykładowe userId
            });
            setVehicles(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania pojazdów:", error);
        }
    };

    // Obsługa dodawania pojazdu
    const addVehicle = async () => {
        try {
            const response = await api.post("/vehicles", {
                name: newVehicle.name,
                capacity: newVehicle.capacity,
                type: newVehicle.type,
                userId: 1,
            });
            setVehicles((prev) => [...prev, response.data]);
            setNewVehicle({ name: "", capacity: "", type: "" }); // Reset formularza
            alert("Pojazd został dodany!");
        } catch (error) {
            console.error("Błąd podczas dodawania pojazdu:", error);
            alert("Nie udało się dodać pojazdu.");
        }
    };

    // Usuwanie pojazdu
    const deleteVehicle = async (id) => {
        try {
            await api.delete(`/vehicles/${id}`);
            setVehicles((prevVehicles) => prevVehicles.filter((vehicle) => vehicle.id !== id));
            alert("Pojazd został usunięty.");
        } catch (error) {
            console.error("Błąd podczas usuwania pojazdu:", error);
            alert("Nie udało się usunąć pojazdu.");
        }
    };

    // Aktualizacja pojazdu
    const updateVehicle = async (id, updatedVehicle) => {
        try {
            const response = await api.put(`/vehicles/${id}`, updatedVehicle);
            setVehicles((prevVehicles) =>
                prevVehicles.map((vehicle) =>
                    vehicle.id === id ? response.data : vehicle
                )
            );
            alert("Pojazd został zaktualizowany.");
        } catch (error) {
            console.error("Błąd podczas edytowania pojazdu:", error);
            alert("Nie udało się zaktualizować pojazdu.");
        }
    };

    // Obsługa edycji pojazdu
    const handleEdit = (vehicle) => {
        setEditingVehicle({ ...vehicle }); // Tworzymy kopię, aby edytować bezpośrednio
    };

    // Pobranie pojazdów po załadowaniu komponentu
    useEffect(() => {
        fetchVehicles();
    }, []);

    return (
        <div>
            <h1>Zarządzanie Flotą</h1>

            {/* Formularz dodawania pojazdu */}
            <h2>Dodaj pojazd</h2>
            <form
                onSubmit={(e) => {
                    e.preventDefault();
                    addVehicle();
                }}
            >
                <div>
                    <label>Nazwa:</label>
                    <input
                        type="text"
                        value={newVehicle.name}
                        onChange={(e) => setNewVehicle({...newVehicle, name: e.target.value})}
                        required
                    />
                </div>
                <div>
                    <label>Pojemność (kg):</label>
                    <input
                        type="number"
                        value={newVehicle.capacity}
                        onChange={(e) => setNewVehicle({...newVehicle, capacity: e.target.value})}
                        required
                    />
                </div>
                <div>
                    <label>Typ:</label>
                    <input
                        type="text"
                        value={newVehicle.type}
                        onChange={(e) => setNewVehicle({...newVehicle, type: e.target.value})}
                        required
                    />
                </div>
                <button type="submit">Dodaj pojazd</button>
            </form>

            {/* Formularz edycji pojazdu */}
            {editingVehicle && (
                <div>
                    <h2>Edytuj Pojazd</h2>
                    <form
                        onSubmit={(e) => {
                            e.preventDefault();
                            updateVehicle(editingVehicle.id, editingVehicle);
                            setEditingVehicle(null);
                        }}
                    >
                        <div>
                            <label>Nazwa:</label>
                            <input
                                type="text"
                                value={editingVehicle.name}
                                onChange={(e) =>
                                    setEditingVehicle({...editingVehicle, name: e.target.value})
                                }
                                required
                            />
                        </div>
                        <div>
                            <label>Pojemność (kg):</label>
                            <input
                                type="number"
                                value={editingVehicle.capacity}
                                onChange={(e) =>
                                    setEditingVehicle({...editingVehicle, capacity: e.target.value})
                                }
                                required
                            />
                        </div>
                        <div>
                            <label>Typ:</label>
                            <input
                                type="text"
                                value={editingVehicle.type}
                                onChange={(e) =>
                                    setEditingVehicle({...editingVehicle, type: e.target.value})
                                }
                                required
                            />
                        </div>
                        <button type="submit">Zapisz</button>
                        <button onClick={() => setEditingVehicle(null)}>Anuluj</button>
                    </form>
                </div>
            )}

            {/* Lista pojazdów */}
            <h2>Lista pojazdów</h2>
            <div>
                <h2>Lista pojazdów</h2>
                {vehicles.length === 0 ? (
                    <p>Brak pojazdów do wyświetlenia.</p>
                ) : (
                    <ul>
                        {vehicles.map((vehicle) => (
                            <li key={vehicle.id}>
                                {vehicle.name} - {vehicle.capacity} kg - {vehicle.type}
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}

export default FleetManager;
