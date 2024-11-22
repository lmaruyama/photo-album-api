# Photo Album API

## Overview

The **Photo Album API** is a backend application for managing photo albums, allowing users to create albums, upload photos, and retrieve them through a RESTful API. Built with **Spring Boot 3.3.5** and **Java 17**, the application leverages **AWS S3** for photo storage and **MySQL** for relational data persistence, with seamless integration to **AWS RDS** for scalable and secure database management.

---

## Features

- **Create and Manage Albums**: Add, update, and delete photo albums.
- **Upload Photos**: Upload photos directly to AWS S3.
- **Retrieve Photos**: Fetch photo details and access links for viewing or downloading.
- **Secure Configuration**: Utilizes AWS RDS for database storage, ensuring high availability and durability.
- **RESTful API**: Adheres to REST standards, ensuring scalability and simplicity.

---

## Technologies Used

### Backend
- **Java 17**
- **Spring Boot 3.3.5**

### Cloud and Storage
- **AWS S3**
- **AWS RDS**

### Database
- **MySQL 8**

---

## Setup and Configuration

### Prerequisites
1. **Java 17** installed.
2. **MySQL 8** database or AWS RDS instance set up.
3. **AWS S3** bucket created for photo storage.
4. Access credentials for AWS (Access Key, Secret Key).

---

## Future Improvements
1. **Authentication and Authorization**: Add user management and secure endpoints with JWT.
2. **Pagination**: Improve scalability for listing albums/photos.
3. **CI/CD Pipeline**: Automate build and deployment with GitHub Actions.

--- 

### Contributions and Feedback
Feel free to fork the repository and submit pull requests. Your feedback is welcome via the issues section.
