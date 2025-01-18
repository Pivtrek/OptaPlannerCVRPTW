import React, { useState, useEffect } from "react";
import api from "../../api/axios";

function WarehouseManager() {
    const [warehouses, setWarehouses] = useState([]);

    useEffect(() => {
        fetchWarehouses();
    }, []);

    const fetchWarehouses = async () => {
        try {
            const response = await api.get("/warehouses");
            setWarehouses(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania magazynów:", error);
        }
    };

    const addWarehouse = async (warehouse) => {
        try {
            const response = await api.post("/warehouses", warehouse);
            setWarehouses((prevWarehouses) => [...prevWarehouses, response.data]);
        } catch (error) {
            console.error("Błąd podczas dodawania magazynu:", error);
        }
    };

    return (
        <div>
            <h1>Twoje Magazyny</h1>
            <ul>
                {warehouses.map((warehouse) => (
                    <li key={warehouse.id}>
                        {warehouse.name} - {warehouse.address}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default WarehouseManager;
