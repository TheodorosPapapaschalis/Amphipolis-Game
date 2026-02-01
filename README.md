# Amphipolis Board Game

A digital implementation of the board game **"Amphipolis"**, developed in **Java** as part of the **Object-Oriented Programming (HY252)** course at the University of Crete.

## 🏛️ Overview
In this game, players take on the role of archaeologists trying to rescue precious artifacts from the Amphipolis excavation site before it is destroyed by landslides. The goal is to collect the most points by gathering mosaics, statues, amphorae, and skeletons while managing resources and using special character abilities.

## 🎮 Game Features
- **MVC Architecture:** Built using the Model-View-Controller pattern for clean code separation.
- **Graphical User Interface (GUI):** Interactive interface developed with Java Swing/AWT.
- **Game Modes:** Supports up to 4 players.
- **Save & Load System:** Ability to save the current game state and resume later.
- **Game Timer:** Integrated timer for turn management.
- **Audio:** Background music that changes dynamically based on the player's turn.
- **Exceptions Handling:** Robust error handling for a smooth user experience.

## 🧩 Gameplay Mechanics
The game simulates the core rules of the physical board game:
* **Artifact Collection:** Players draw tiles (Mosaics, Amphorae, Statues, Skeletons) to score points based on specific combinations.
* **Landslides:** "Landslide" tiles block the entrance. If the entrance is fully blocked, the game ends immediately.
* **Characters:** Usage of 4 distinct character cards (Archaeologist, Assistant, Digger, Professor) with unique abilities to manipulate the game state.
* **Scoring System:** Automated point calculation for:
    * *Mosaics:* Based on color matching.
    * *Skeletons:* Points for complete families (adults + child).
    * *Amphorae:* Points for variety of colors.
    * *Statues:* Majority rules (Sphinxes & Caryatids).

## 🛠️ Technical Stack
- **Language:** Java
- **GUI Library:** Java Swing
- **Concepts:** OOP, Inheritance, Polymorphism, Interfaces, Collections Framework.
- **Testing:** JUnit tests for logic verification.

## 🚀 How to Run

### Prerequisites
* Java Development Kit (JDK) 8 or higher.

### Installation
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/TheodorosPapapaschalis/Amphipolis-Game.git](https://github.com/TheodorosPapapaschalis/Amphipolis-Game.git)
    ```
2.  **Open in IDE:** Import the project into IntelliJ IDEA, Eclipse, or NetBeans.
3.  **Build & Run:**
    * Run the `Main` class located in the `src` folder.
    * *Alternatively, if a JAR is provided:*
        ```bash
        java -jar Amphipolis.jar
        ```

## 📝 Author
* **Theodoros Papapaschalis** - [GitHub Profile](https://github.com/TheodorosPapapaschalis)

---
*Disclaimer: This project was created for educational purposes for the HY252 course.*
