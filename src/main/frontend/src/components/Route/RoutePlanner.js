import React, { useEffect, useState } from "react";
import api from "../../api/axios";
import { GoogleMap, DirectionsRenderer, useJsApiLoader } from "@react-google-maps/api";

const RoutePlanner = () => {
    const [vehicles, setVehicles] = useState([]);
    const [warehouses, setWarehouses] = useState([]);
    const [selectedVehicles, setSelectedVehicles] = useState([]);
    const [selectedWarehouses, setSelectedWarehouses] = useState({});
    const [directions, setDirections] = useState([]);

    const { isLoaded, loadError } = useJsApiLoader({
        googleMapsApiKey: "YOUR_API_KEY",
    });

    // Pobieranie danych pojazdów i magazynów z API
    const fetchVehicles = async () => {
        try {
            const response = await api.get("/vehicles");
            setVehicles(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania pojazdów:", error);
        }
    };

    const fetchWarehouses = async () => {
        try {
            const response = await api.get("/warehouses");
            setWarehouses(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania magazynów:", error);
        }
    };

    const toggleVehicleSelection = (vehicleId) => {
        setSelectedVehicles((prev) =>
            prev.includes(vehicleId)
                ? prev.filter((id) => id !== vehicleId)
                : [...prev, vehicleId]
        );
    };

    const handleWarehouseChange = (warehouseId, field, value) => {
        setSelectedWarehouses((prev) => ({
            ...prev,
            [warehouseId]: {
                ...prev[warehouseId],
                [field]: value,
            },
        }));
    };

    const fetchDirections = async (routes) => {
        const directionsService = new window.google.maps.DirectionsService();
        const fetchedDirections = [];

        for (const route of routes) {
            const waypoints = route.path.slice(1, -1).map((point) => ({
                location: new window.google.maps.LatLng(point.lat, point.lng),
                stopover: true,
            }));

            try {
                const result = await directionsService.route({
                    origin: new window.google.maps.LatLng(route.path[0].lat, route.path[0].lng),
                    destination: new window.google.maps.LatLng(
                        route.path[route.path.length - 1].lat,
                        route.path[route.path.length - 1].lng
                    ),
                    waypoints: waypoints,
                    travelMode: window.google.maps.TravelMode.DRIVING,
                });

                fetchedDirections.push(result);
            } catch (error) {
                console.error("Error fetching directions:", error);
            }
        }

        setDirections(fetchedDirections);
    };

    // Wysyłanie żądania o wytyczenie trasy za pomocą `api`
    const handleRoutePlanSubmit = () => {
        const selectedWarehouseList = Object.entries(selectedWarehouses).map(
            ([warehouseId, data]) => ({
                id: parseInt(warehouseId),
                load: data.load || 0,
                serviceTime: data.serviceTime || 0,
            })
        );

        const payload = {
            vehicles: selectedVehicles,
            warehouses: selectedWarehouseList,
        };

        api.post("routePlan", payload)
            .then((response) => {
                console.log("Route plan:", response.data);
                fetchDirections(response.data.routes); // Fetch road directions for the returned routes
            })
            .catch((error) => {
                if (error.response) {
                    console.error("Backend error:", error.response.data);
                    alert("Error: " + error.response.data);
                } else {
                    console.error("Network or unknown error:", error.message);
                }
            });
    };

    // Pobranie danych po załadowaniu komponentu
    useEffect(() => {
        fetchWarehouses();
        fetchVehicles();
    }, []);

    return (
        <div className="container my-4">
            <h1 className="mb-4">Route Planner</h1>

            {/* Sekcja pojazdów */}
            <div className="mb-4">
                <h2 className="mb-3">Select Vehicles</h2>
                {vehicles.map((vehicle) => (
                    <div className="form-check mb-2" key={vehicle.id}>
                        <input
                            className="form-check-input"
                            type="checkbox"
                            checked={selectedVehicles.includes(vehicle.id)}
                            onChange={() => toggleVehicleSelection(vehicle.id)}
                            id={`vehicle-${vehicle.id}`}
                        />
                        <label
                            className="form-check-label"
                            htmlFor={`vehicle-${vehicle.id}`}
                        >
                            {vehicle.name} (Capacity: {vehicle.capacity})
                        </label>
                    </div>
                ))}
            </div>

            {/* Sekcja magazynów */}
            <div className="mb-4">
                <h2 className="mb-3">Select Warehouses</h2>
                {warehouses.map((warehouse) => (
                    <div className="border rounded p-3 mb-3" key={warehouse.id}>
                        <h3 className="mb-3">{warehouse.name}</h3>

                        <div className="mb-3">
                            <label className="form-label" htmlFor={`load-${warehouse.id}`}>
                                Load:
                            </label>
                            <input
                                id={`load-${warehouse.id}`}
                                className="form-control"
                                type="number"
                                min="0"
                                value={selectedWarehouses[warehouse.id]?.load || ""}
                                onChange={(e) =>
                                    handleWarehouseChange(
                                        warehouse.id,
                                        "load",
                                        parseInt(e.target.value, 10)
                                    )
                                }
                            />
                        </div>

                        <div>
                            <label
                                className="form-label"
                                htmlFor={`serviceTime-${warehouse.id}`}
                            >
                                Service Time:
                            </label>
                            <input
                                id={`serviceTime-${warehouse.id}`}
                                className="form-control"
                                type="number"
                                min="0"
                                value={selectedWarehouses[warehouse.id]?.serviceTime || ""}
                                onChange={(e) =>
                                    handleWarehouseChange(
                                        warehouse.id,
                                        "serviceTime",
                                        parseInt(e.target.value, 10)
                                    )
                                }
                            />
                        </div>
                    </div>
                ))}
            </div>

            {/* Przycisk do planowania trasy */}
            <button className="btn btn-primary mb-4" onClick={handleRoutePlanSubmit}>
                Plan Route
            </button>

            {/* Sekcja z mapą i zaplanowanymi trasami */}
            {directions.length > 0 && (
                <div className="mb-4">
                    <h2 className="mb-3">Planned Routes</h2>
                    <div style={{ width: "100%", height: "500px" }}>
                        <GoogleMap
                            mapContainerStyle={{ width: "100%", height: "100%" }}
                            center={{ lat: 50.0647, lng: 19.9450 }}
                            zoom={12}
                        >
                            {directions.map((direction, index) => (
                                <DirectionsRenderer key={index} directions={direction} />
                            ))}
                        </GoogleMap>
                    </div>
                </div>
            )}
        </div>
    );
};

export default RoutePlanner;
