# 🐍 Java Snake Game (Evolution Edition)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![Status](https://img.shields.io/badge/Status-Completed-success)

> A classic arcade game re-engineered with **Persistent Data Storage (SQL)** and **Immersive Audio**.

---

## 🧐 Project Evolution
I built this project to simulate a real-world software lifecycle, evolving it through three distinct engineering phases:

1.  **Phase 1: The Engine (Core Logic)** 🧠
    * Implemented the game loop, collision detection algorithms, and rendering using Java Swing.
2.  **Phase 2: The Memory (Backend)** 💾
    * Integrated **MySQL** via JDBC to persist player scores.
    * Designed a relational database schema to track high scores across sessions.
3.  **Phase 3: The Experience (UI/UX)** 🔊
    * Added audio event listeners for gameplay feedback (eating, collisions).

---

## ⚙️ Technical Architecture

### 1. Object-Oriented Design
* **Encapsulation:** Game entities (Snake, Food) are decoupled from the rendering engine.
* **Inheritance:** Extended `JPanel` and implemented `ActionListener` for fluid game cycles.

### 2. Database Integration
* **SQL Connectivity:** Uses `PreparedStatement` to prevent SQL injection while saving scores.
* **Schema:** simple `scores` table structure (Name `VARCHAR`, Score `INT`).

---

## 📸 Screenshots
*(You will upload images here in the next step!)*

---

## 🛠 Installation & Setup

1.  **Clone the Repo:**
    ```bash
    git clone [https://github.com/Jayesh-001/Java-Snake-Game-Evolution.git](https://github.com/Jayesh-001/Java-Snake-Game-Evolution.git)
    ```
2.  **Database Setup:**
    * Run the included `database_setup.sql` script in MySQL Workbench.
    * Update `db_config.java` with your MySQL credentials.
3.  **Run:**
    * Compile and run `Main.java`.

---

## 🤝 Contact
**Jayesh** - Aspiring Data Scientist & Developer
[LinkedIn](https://linkedin.com/in/your-linkedin-profile)