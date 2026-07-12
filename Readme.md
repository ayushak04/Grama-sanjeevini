# Grama-Sanjeevini (Rural Healthcare Network) 
[cite_start]**Document ID:** PRD-GS-028 [cite: 7] | [cite_start]**Version:** v1.0 Draft [cite: 8]

[cite_start]Grama-Sanjeevini is an Android application designed to bridge the gap between rural patients and local medical stores by creating a shared inventory network[cite: 21, 22]. In remote villages, the "Local Medical Store" is often the only source of advice; if a medicine is out of stock, villagers may have to travel 20km to the city without knowing if a shop in the next village has it. This app solves that problem by pooling the inventory of 5-10 village medical stores into a single searchable network.

---

## 📱 Project Scope

### In Scope
* [cite_start]**F-01:** Real-time inventory search across 5-10 Mock Shops[cite: 25].
* [cite_start]**F-02:** Distance-based filtering (within 10 km-20 km radius)[cite: 25].
* [cite_start]**E-03:** Life Saving Drugs dashboard with high-visibility red badges[cite: 26, 27].
* [cite_start]**F-04:** Pharmacist stock management and Expiry Watch alerts[cite: 27].
* [cite_start]**F-05:** Firebase Firestore integration for real-time data syncing[cite: 28].

### Out of Scope
* [cite_start]**OOS-01:** Online payment gateways and digital transactions[cite: 30].
* [cite_start]**OOS-02:** Home delivery logistics and tracking[cite: 31].
* [cite_start]**OOS-03:** Direct tele-consultation with doctors[cite: 32].

---

## 👥 Stakeholders & User Personas

### Stakeholder Map
| Stakeholder | Role | Power/Interest | Key Requirement |
| :--- | :--- | :--- | :--- |
| **Pharmacist** | [cite_start]Primary User [cite: 35] | [cite_start]HIGH/HIGH [cite: 35] | [cite_start]Needs easy inventory entry & expiry alerts[cite: 35]. |
| **Villager** | [cite_start]End User [cite: 35] | [cite_start]LOW/HIGH [cite: 35] | [cite_start]Needs extreme UI simplicity (low digital literacy)[cite: 35]. |
| **Admin** | [cite_start]Oversight [cite: 35] | [cite_start]HIGH/LOW [cite: 35] | [cite_start]Needs to manage the shop network/onboarding[cite: 35]. |

### User Personas
[cite_start]**Persona 1: Basappa (The Villager)** [cite: 40]
* **Profile:** Age 58 | [cite_start]Occupation: Farmer[cite: 41].
* [cite_start]**Pain Point:** Travels 20 km to the city only to find medicine out of stock[cite: 42].
* [cite_start]**Expectation:** An app with big buttons that tells him where his Insulin is available nearby[cite: 43].

[cite_start]**Persona 2: Arjun (The Pharmacist)** [cite: 44]
* **Profile:** Age 29 | [cite_start]Occupation: Local Medical Store Owner[cite: 45].
* [cite_start]**Pain Point:** Medicine expires on the shelf; loses money; cannot help people if stock is low[cite: 46].
* [cite_start]**Expectation:** A way to notify others about near-expiry discounts[cite: 47].

---

## 🏗️ System Architecture & Tech Stack
[cite_start]The system follows the **MVVM (Model-View-ViewModel)** architecture [cite: 49] [cite_start]and targets the **Android** platform[cite: 9].

* [cite_start]**View Layer:** Jetpack Compose for high-contrast, accessible UI components[cite: 50].
* [cite_start]**ViewModel Layer:** Manages search logic and inventory filtering[cite: 50].
* [cite_start]**Repository Layer:** Mediates between Firebase Firestore (Cloud) and Room DB (Local Cache)[cite: 50].
* [cite_start]**Data Layer:** Firebase Firestore - Real-time stock across multiple shops[cite: 50].

---

## ⚙️ Functional Requirements (FR)

### Group 1 - Medicine Search & Discovery
* [cite_start]**FR-GS-01:** The system shall allow users to search for medicine by name (partial match)[cite: 53].
* [cite_start]**FR-GS-02:** The system shall display search results including Shop Name, Distance (in km), and Stock Status[cite: 54].
* [cite_start]**FR-GS-03:** The system shall filter results based on a user-selected radius (10 km, 15 km, 20 km)[cite: 55].
* [cite_start]**FR-GS-04:** The system shall highlight Life Saving Drugs (e.g., Insulin, Snake Venom) with a prominent Red Badge[cite: 59].

### Group 2 - Pharmacist Inventory Management
* [cite_start]**FR-GS-05:** The system shall allow authenticated pharmacists to update stock levels for their specific store[cite: 61].
* [cite_start]**FR-GS-06:** The system shall trigger an Expiry Watch alert for items expiring within 30 days[cite: 62].
* [cite_start]**FR-GS-07:** The system shall allow pharmacists to mark near-expiry items for Discount Sale visible to villagers[cite: 63].

---

## 🚀 Non-Functional Requirements (NFR)
* [cite_start]**NFR-USAB-01 (Usability):** UI shall be optimised for low digital literacy using large icons, 18pt+ font sizes, and vernacular language support[cite: 65].
* [cite_start]**NFR-PERF-01 (Performance):** Search results across the 10-shop network shall load within <1.5 seconds on a 3G connection[cite: 66].
* [cite_start]**NFR-RELY-01 (Reliability):** The app shall provide an Offline Mode showing the last synced stock data when the internet is unavailable[cite: 67].

---

## 🗄️ Data Requirements & Schema

### [cite_start]Shop Table [cite: 75]
* [cite_start]`shop_id` (PK) [cite: 76]
* [cite_start]`shop_name` [cite: 77]
* [cite_start]`latitude` [cite: 78]
* [cite_start]`longitude` [cite: 79]
* [cite_start]`contact_no` [cite: 80]

### [cite_start]Medicine Table [cite: 81]
* [cite_start]`med_id` (PK) [cite: 82]
* [cite_start]`med_name` [cite: 83]
* [cite_start]`is_life_saving` (Boolean) [cite: 84]
* [cite_start]`base_price` [cite: 85]

### [cite_start]Stock Table (Firestore Collection) [cite: 86]
* [cite_start]`stock_id` (PK) [cite: 87]
* [cite_start]`quantity` [cite: 87]
* [cite_start]`shop_id` (FK) [cite: 88]
* [cite_start]`med_id` (FK) [cite: 89]
* [cite_start]`expiry_date` [cite: 90]

---

## [cite_start]🖼️ Application UI Screens [cite: 94]

[cite_start]The application consists of the following key screens designed for high accessibility[cite: 92, 94]:

1. [cite_start]**Home Dashboard** [cite: 95]
   * [cite_start]Features a search bar and a large Emergency View button with a high-contrast red background[cite: 96].
2. [cite_start]**Search Results** [cite: 97]
   * [cite_start]Displays a card-based list containing the Shop Name, Distance, Stock Status, and a Call Shop button[cite: 98].
3. [cite_start]**Pharmacist Portal** [cite: 98]
   * [cite_start]Provides a simple list of stock items with `+` and `-` buttons for easy quantity updates[cite: 99].

### [cite_start]Design System - High Accessibility Colour Palette [cite: 92]
* [cite_start]🔴 **Emergency (`#D32F2F` Red):** Usage: Life-saving drugs, stock-out background[cite: 93].
* [cite_start]🟢 **Availability (`#388E3C` Green):** Usage: Available stock, safe distance[cite: 93].
* [cite_start]🔵 **Primary UI (`#1976D2` Blue):** Usage: Buttons and navigation elements[cite: 93].

---

## [cite_start]✅ Acceptance Criteria (Sign-off) [cite: 102, 103]

| Check | Criteria |
| :--- | :--- |
| **Network Check** | [cite_start]Search must successfully aggregate and return results from at least 3 distinct Mock Shop profiles[cite: 105]. |
| **Emergency Check**| [cite_start]Any drug flagged as Life Saving must appear at the top of the search with a Red Badge[cite: 105]. |
| **UI Check** | [cite_start]A user must be able to find a medicine in fewer than 3 taps from the home screen[cite: 105]. |
| **Tech Check** | [cite_start]Data must sync to Firebase Firestore and reflect on a second device within 2 seconds of a stock update[cite: 105]. |

---

## 👨‍💻 Project Details & Credits
* [cite_start]**Developer Name:** Ayush Kumar [cite: 11]
* [cite_start]**Program:** MindMatrix Industry Readiness Programme - Android Internship [cite: 36]
* [cite_start]**Internship Title:** Android App Development using Gen AI [cite: 15]
* [cite_start]**Project Title:** Grama-Sanjeevini (28) [cite: 16]

---

## Kotlin Android App Implementation

This repository now contains a Kotlin Android application scaffold for **Grama-Sanjeevini**.

### What is built
* Splash screen, role selection, villager guest access, pharmacist auth, admin auth, and role dashboards.
* Firebase Authentication and Firestore repository layer for pharmacist signup/login, admin role verification, password reset, session restore, and logout.
* Jetpack Compose UI with medicine Search, Emergency, and Pharmacist Stock sections.
* MVVM-style Kotlin structure:
  * `app/src/main/kotlin/.../auth/` for auth domain models, Firebase repository, and auth ViewModels/screens.
  * `app/src/main/kotlin/.../dashboard/` for villager/pharmacist/admin dashboards.
  * `app/src/main/kotlin/.../navigation/` for Navigation Compose routes.
  * `app/src/main/kotlin/.../di/` for Hilt Firebase and repository modules.
  * `app/src/main/kotlin/.../data/` for the mock village pharmacy inventory source.
* Accessible UI choices from the PRD: large text, simple bottom navigation, emergency red badges, availability statuses, and large stock `+` / `-` controls.
* Safe shop calling through `ACTION_DIAL`.

### Firebase setup
Firebase project creation requires your Firebase/Google account, so this repository includes `app/google-services.json.example` instead of a private live config. To connect the app:

1. Create a Firebase project named `Grama-Sanjeevini`.
2. Add an Android app with package `com.mindmatrix.gramasanjeevini`.
3. Download `google-services.json` and place it at `app/google-services.json`.
4. Enable Authentication > Sign-in method > Email/Password.
5. Enable Cloud Firestore.
6. Create admin users manually in Firebase Authentication, then add matching Firestore documents:

```json
{
  "uid": "ADMIN_FIREBASE_UID",
  "fullName": "Admin Name",
  "email": "admin@example.com",
  "role": "admin",
  "shopName": "",
  "contactNo": "",
  "createdAt": "server timestamp"
}
```

Pharmacist signup writes the same `users/{uid}` structure automatically with `role = pharmacist`.

### Build notes
Open this folder in Android Studio and install/configure an Android SDK if prompted. Command-line assembly needs either:

```properties
sdk.dir=C\:\\Users\\hp\\AppData\\Local\\Android\\Sdk
```

in `local.properties`, or an `ANDROID_HOME` environment variable pointing to the installed SDK.

### Next production steps
* Replace the mock `InventoryRepository` inventory data with Firestore stock collections.
* Add Firestore security rules that enforce `users/{uid}.role`.
* Add Room cache for offline mode.
* Add UI tests for search, emergency sorting, and stock adjustment flows.
#   G r a m a - s a n j e e v i n i  
 