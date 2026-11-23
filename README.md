# Eaze-Money
Project Repo for Cache Keepers

## Description
Eaze Money is a JavaFX desktop application designed to help users take control of their personal spending habits. This app will provide a simple space to track spending, manage budgets, and gain financial insights about their spending habits.

Eaze Money addresses a common problem of existing money tracking apps being cluttered with unnecessary or complicated features that an average user will never touch. Our app prioritizes simplicity, fast transaction entry, and a fully-offline ad-free experience. We aim to provide assistance to make personal budgeting accessible and private, therefore Eaze Money is a perfect choice for individuals seeking alternatives for subscription-based tools or refusing to link their sensitive banking information just to track basic spending.

## Contributors
Vinh Thanh Nguyen: https://github.com/ThanhVinh2869  
Thanh U Nguyen: https://github.com/tynnguyen4002  
Siddhant Kapoor: https://github.com/Siddkap  
Luna Jaimes Sesmas: https://github.com/LunaJaimes  

## Prerequisites

- **Java Development Kit (JDK) 17 or newer.** The project compiles for Java 17; set `JAVA_HOME` to the JDK installation directory and add `%JAVA_HOME%\bin` (Windows) or `$JAVA_HOME/bin` (macOS/Linux) to `PATH`.
- **Internet access on the first run** to download Maven dependencies and Maven Wrapper files.
- **One of the following:**
  - Maven 3.8 or newer (optional when using the included Maven Wrapper), or
  - IntelliJ IDEA with Maven support.
- **JavaFX dependencies** are managed by Maven through `pom.xml`; no separate JavaFX installation is required.

## Instructions

1. Clone the repository and open a terminal in the project directory:
   ```bash
   git clone https://github.com/UTSA-CS-3443/Eaze-Money.git
   cd Eaze-Money
   ```

2. Run the application using the Maven Wrapper:

   **Windows:**
   ```bat
   mvnw.cmd clean javafx:run
   ```

   **macOS/Linux:**
   ```bash
   ./mvnw clean javafx:run
   ```

   If Maven is installed locally instead of using the Wrapper, run `mvn clean javafx:run` on any supported operating system.

Alternatively, open the project in IntelliJ IDEA, allow Maven to import dependencies, and run the `App` main class from `src/main/java/edu/utsa/cs3443/eazemoneyapp/App.java`.

## Known Issues
