# 🚚 Logistics Route Optimization System (CVRPTW)

## 📌 Project Overview
This web application optimizes vehicle routing for logistics companies using **Capacitated Vehicle Routing Problem with Time Windows (CVRPTW)**. The system enables users to:
- **Manage** vehicles, warehouses, and depots.
- **Optimize** delivery routes based on vehicle capacity and time constraints.
- **Visualize** routes using **Google Maps API**.

The backend, powered by **Spring Boot** and **OptaPlanner**, calculates the most efficient routes, while the frontend, built with **React**, provides an intuitive interface.

---

## 🛠 Technologies Used
### **Backend**
- Java **(Spring Boot)**
- **OptaPlanner** (route optimization)
- **PostgreSQL** (database)
- **Gradle** (build tool)

### **Frontend**
- **React.js** (user interface)
- **Bootstrap** (styling)
- **Google Maps API** (route visualization)
- **Axios** (API communication)

---

## 🚀 Features
✅ Vehicle and warehouse management  
✅ Route optimization using **CVRPTW**  
✅ Google Maps integration for visualization  
✅ Competitive results tested on benchmark datasets  

---

## ⚙️ Installation & Setup

### Clone the Repository
```bash
git clone <repo_url>
cd <project_folder>
```

### Setup Google Maps API and Database
You need to get api key from google maps and then apply it in frontend code.
To set up database go to src/main/resources and in application.properties follow further instruction


### Gradle build
```bash
gradle build
```

### Launch frontend 
```bash
cd src/main/frontend
npm install
npm start
```

### Launch backend

run main class in RoutePlanerApplication



**TODO**
- In class RoutePlanConstraintProvider unnecesary overloaded methods calculateDistance - use interface instead
- clean Vehicle class - variables totalVehicles and totalWarehouses should not be there 
- Class RoutePlanConstraintProvider which is key one in this program is not readable - too much low code and computation 
- Email registration with confirmation link
- Memory of routes to avoid unnecesary computations
