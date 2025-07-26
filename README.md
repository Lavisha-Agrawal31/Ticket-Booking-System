# 🎟️ Ticket Booking System

A simple ticket booking system built with **Java** and **Spring Boot**, providing core functionalities like user authentication, train search, and seat booking. This project uses **local JSON files** as a lightweight database and follows a modular, object-oriented structure.

---

## 🚀 Features

- 🔐 **User Authentication**
  - Sign up and login with password hashing using BCrypt
- 🚆 **Train Search**
  - Search available trains based on source and destination
- 📄 **Booking Management**
  - Book seats and fetch user-specific booking details
- 💾 **Local Data Storage**
  - Uses `users.json` and `trains.json` to persist data

---

## 🧱 Tech Stack

- **Language**: Java  
- **Framework**: Spring Boot  
- **Storage**: Local JSON files  
- **Libraries**:
  - `Jackson` for JSON serialization/deserialization  
  - `BCrypt` for password encryption
