# 🌿 Grama-Sanjeevini (Rural Healthcare Network)

**Document ID:** PRD-GS-028  
**Version:** v1.0 Draft

Grama-Sanjeevini is an Android application designed to bridge the gap between rural patients and local medical stores by creating a shared medicine inventory network.

In many villages, people have to travel long distances when medicines are unavailable in their local pharmacy. This application solves that problem by allowing multiple village medical stores to share their inventory in real time through Firebase Firestore.

---

# 📱 Project Scope

## ✅ In Scope

| Feature | Description |
|---------|-------------|
| Real-time Inventory Search | Search medicines across multiple village pharmacies |
| Distance Filter | Search within 10 km, 15 km or 20 km |
| Life Saving Drugs | Highlight emergency medicines using red badges |
| Pharmacist Dashboard | Manage medicine stock and expiry alerts |
| Firebase Integration | Real-time Firestore synchronization |

## ❌ Out of Scope

- Online Payments
- Home Delivery
- Telemedicine / Doctor Consultation

---

# 👥 User Roles

| Role | Description |
|------|-------------|
| Villager | Search medicines without login |
| Pharmacist | Manage inventory and medicine stock |
| Admin | Monitor users and pharmacy network |

---

# 👤 User Personas

## Villager

- Farmer living in a rural area
- Needs medicines quickly
- Wants a very simple interface

## Pharmacist

- Local Medical Store Owner
- Manages stock
- Gets expiry alerts
- Can update medicine quantity

---

# 🏗 Architecture

The application follows **MVVM Architecture**.

```
UI (Jetpack Compose)
        │
ViewModel
        │
Repository
        │
Firebase Firestore
```

---

# 🛠 Tech Stack

| Technology | Usage |
|------------|------|
| Kotlin | Programming Language |
| Jetpack Compose | UI Development |
| Firebase Authentication | User Authentication |
| Cloud Firestore | Database |
| Hilt | Dependency Injection |
| MVVM | Architecture |
| Coroutines | Async Programming |

---

# ⚙ Functional Requirements

## Medicine Search

- Search medicine by name
- Partial search supported
- Show nearest pharmacy
- Show stock availability
- Show emergency medicines first

## Pharmacist Features

- Add medicines
- Update stock
- Expiry alerts
- Discount medicines

## Admin Features

- View all pharmacists
- View villagers
- Monitor registrations

---

# 🚀 Non Functional Requirements

- Simple UI
- Fast search (<1.5 sec)
- Reliable Firebase sync
- Offline-ready architecture
- Easy accessibility

---

# 🗄 Database Collections

## Users

| Field | Type |
|------|------|
| uid | String |
| fullName | String |
| email | String |
| role | admin / pharmacist |
| contactNo | String |

---

## Medicines

| Field | Type |
|------|------|
| medicineId | String |
| medicineName | String |
| quantity | Number |
| expiryDate | Timestamp |
| isLifeSaving | Boolean |

---

## Requests

| Field | Type |
|------|------|
| villagerId | String |
| medicineName | String |
| status | Pending / Accepted |

---

# 📱 Main Screens

- Splash Screen
- Login / Register
- Role Selection
- Villager Dashboard
- Pharmacist Dashboard
- Admin Dashboard
- Medicine Search
- Emergency Medicines
- Inventory Management

---

# 🔐 Firebase

The application uses

- Firebase Authentication
- Cloud Firestore

> **Note**
>
> The repository does **NOT** include the production `google-services.json` file.

To run this project:

1. Create a Firebase Project.
2. Add Android App.
3. Download `google-services.json`.
4. Place it inside

```
app/google-services.json
```

5. Enable

- Email Authentication
- Cloud Firestore

---

# 📂 Project Structure

```
app
 ├── auth
 ├── dashboard
 ├── data
 ├── di
 ├── navigation
 ├── ui
 └── core
```

---

# ✨ Features Implemented

- Splash Screen
- Role Selection
- Guest Villager Access
- Pharmacist Registration/Login
- Admin Login
- Firebase Authentication
- Firestore Integration
- Medicine Search
- Inventory Management
- Emergency Medicine Section
- MVVM Architecture
- Jetpack Compose UI
- Hilt Dependency Injection

---

# 📌 Future Improvements
- Admin Analytics dashboard
- Notification alert system for Emergency medicines 
- Offline Room Database
- Google Maps Integration
- Image Upload
- Better Analytics

---

# 👨‍💻 Developer

**Ayush Kumar**

Android App Development using Gen AI

MindMatrix Industry Readiness Programme

---

# ⭐ Project Highlights

- Rural Healthcare Solution
- Real-time Firebase Database
- Modern Android Architecture
- Kotlin + Jetpack Compose
- Firebase Authentication
- Cloud Firestore
- MVVM
- Hilt Dependency Injection

---

# 📄 License

This project is created for educational and internship purposes.

