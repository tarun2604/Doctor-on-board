# Doctor on board

## Overview
The Medical Subscription System is a Java Swing-based application that allows patients to register, modify their details, view prescriptions, and book blood tests. Doctors can log in, view patient details, and prescribe medications. The system also integrates Google Maps for locating nearby hospitals.

## Features
- **Patient Interface**
  - Register a new patient with personal details and subscription plans.
  - Modify existing patient details.
  - View prescription and book blood tests.
  - Locate nearby hospitals on Google Maps.

- **Doctor Interface**
  - Login authentication.
  - View patient details.
  - Prescribe medication to patients.

- **Database Integration**
  - Uses MySQL to store patient details and prescriptions.
  - Table `patients` is created dynamically if it does not exist.

## Technologies Used
- Java Swing (GUI)
- MySQL (Database)
- Google Maps (Hospital search)
- JDBC (Database connection)

## Installation and Setup
### Prerequisites
- Java Development Kit (JDK) installed
- MySQL Server installed and running

### Steps to Run
1. Clone or download the project files.
2. Ensure MySQL is running and update `JDBC_URL`, `USERNAME`, and `PASSWORD` in the code.
3. Compile and run `Main.java` using the following commands:
   ```sh
   javac Main.java
   java Main
   ```
4. The application GUI will launch.

## Database Setup
- The system uses a MySQL database named `medical_system`.
- Ensure MySQL is running and execute the following query to create the database:
  ```sql
  CREATE DATABASE medical_system;
  ```
- The application will automatically create the `patients` table.

## Usage
### Patient Actions
1. Click on "Patient" in the main menu.
2. Select an option: Add New Patient, Modify Existing Patient, or View Prescription.
3. Enter required details and submit.

### Doctor Actions
1. Click on "Doctor" in the main menu.
2. Enter username and password (`doctor/password`).
3. Select a patient and prescribe medication.

## Future Enhancements
- Implement user authentication using a database.
- Add appointment scheduling.
- Enhance UI/UX with better design elements.

![image](https://github.com/user-attachments/assets/d6c1715b-54b9-43e3-a71f-f360761248b8)
![image](https://github.com/user-attachments/assets/0b2cfc72-138a-4a00-935d-985600926806)
![image](https://github.com/user-attachments/assets/8694d7ec-2db9-4a9f-ae0d-187a44a260c2)
![image](https://github.com/user-attachments/assets/b33d3af3-98b4-4ceb-9aed-7882c7700be1)
![image](https://github.com/user-attachments/assets/a4073c35-64e0-40e4-910d-96f30f5b92ed)
![image](https://github.com/user-attachments/assets/c90e78be-3702-4c3f-9459-feca39709e6c)






