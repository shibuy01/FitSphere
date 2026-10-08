# 🏋️ FitSphere – AI Powered Fitness Recommendation System

<p align="center">

**A modern fitness platform built with Spring Boot Microservices, React.js, Kafka, Keycloak and AI**

</p>

---

## 📌 Overview

**FitSphere** is a full-stack, AI-powered fitness recommendation platform built using a **Microservices Architecture**.

The application allows users to manage their fitness activities and receive personalized recommendations using an AI-powered service.

The backend is divided into multiple independent microservices communicating through **REST APIs and Apache Kafka**, while **Spring Cloud Eureka** provides service discovery and **Spring Cloud Config Server** manages centralized configuration.

Authentication and authorization are handled using **Keycloak and Spring Security**, providing secure access to protected APIs.

The project also includes a React.js frontend for interacting with the fitness platform.

---

# ✨ Key Features

### 👤 User Management

* User registration
* User login
* User profile management
* User validation
* Secure authentication
* Protected APIs
* User-specific data

### 🏃 Activity Management

* Create fitness activities
* View fitness activities
* Track user activities
* Activity-based recommendations
* REST API based activity management
* Event-driven activity processing

### 🤖 AI Fitness Recommendations

* AI-powered fitness analysis
* Personalized recommendations
* Activity analysis
* Improvement suggestions
* Fitness insights
* AI-generated recommendations
* Structured AI response processing

### 🔐 Authentication & Security

* Keycloak Identity and Access Management
* Spring Security
* OAuth2 / OpenID Connect based authentication
* JWT-based API security
* Protected microservice endpoints
* Role-based authorization
* Secure frontend authentication flow

### 📡 Event-Driven Architecture

* Apache Kafka
* Activity event publishing
* Kafka consumers
* Asynchronous processing
* Event-based communication between services

### 🌐 API Gateway

* Centralized API entry point
* Spring Cloud Gateway
* Service-based routing
* Load-balanced service communication
* Centralized security handling
* Frontend-to-backend API communication

### 🔎 Service Discovery

* Netflix Eureka Server
* Dynamic service registration
* Service discovery
* Load-balanced communication

### ⚙️ Centralized Configuration

* Spring Cloud Config Server
* Centralized application configuration
* Environment-based configuration
* Externalized service properties

---

# 🏗️ Microservices Architecture

```text
                         ┌──────────────────────┐
                         │      React.js        │
                         │      Frontend        │
                         │   localhost:5173     │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │    API Gateway       │
                         │      :8080            │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┼────────────────┐
                    │               │                │
                    ▼               ▼                ▼
             ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
             │ User Service│ │  Activity   │ │ AI Service  │
             │    :8081    │ │  Service    │ │    :8084    │
             └──────┬──────┘ │    :8082    │ └──────┬──────┘
                    │        └──────┬──────┘        │
                    │               │               │
                    ▼               ▼               ▼
             ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
             │    MySQL    │ │    MySQL    │ │   MongoDB   │
             │ userService │ │activitySvc  │ │  aiService  │
             └─────────────┘ └─────────────┘ └─────────────┘
                                    │
                                    ▼
                           ┌─────────────────┐
                           │ Apache Kafka    │
                           │ activity-events │
                           └────────┬────────┘
                                    │
                                    ▼
                              AI Service
```

---

# 🔧 Infrastructure Components

```text
                    ┌──────────────────────┐
                    │   Eureka Server      │
                    │        :8761         │
                    └──────────┬───────────┘
                               │
                    Service Discovery
                               │
         ┌─────────────────────┼─────────────────────┐
         │                     │                     │
         ▼                     ▼                     ▼
    User Service        Activity Service        AI Service
         │                     │                     │
         └─────────────────────┼─────────────────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │   Config Server      │
                    │        :8888         │
                    └──────────────────────┘
```

---

# 🧩 Microservices

| Service          |   Port | Responsibility             |
| ---------------- | -----: | -------------------------- |
| Eureka Server    | `8761` | Service Discovery          |
| Config Server    | `8888` | Centralized Configuration  |
| API Gateway      | `8080` | API Routing & Gateway      |
| User Service     | `8081` | User Management            |
| Activity Service | `8082` | Fitness Activities         |
| AI Service       | `8084` | AI Fitness Recommendations |

---

# 🛠️ Technology Stack

| Category          | Technologies               |
| ----------------- | -------------------------- |
| Language          | Java                       |
| Backend           | Spring Boot                |
| Architecture      | Microservices              |
| API               | REST APIs                  |
| Security          | Spring Security            |
| Identity          | Keycloak                   |
| Authentication    | OAuth2 / OIDC / JWT        |
| Gateway           | Spring Cloud Gateway       |
| Service Discovery | Netflix Eureka             |
| Configuration     | Spring Cloud Config Server |
| Messaging         | Apache Kafka               |
| AI                | Spring AI                  |
| AI Model          | Google Gemini              |
| ORM               | Hibernate / JPA            |
| Database          | MySQL                      |
| NoSQL Database    | MongoDB                    |
| Frontend          | React.js                   |
| Styling           | Tailwind CSS               |
| UI Components     | shadcn/ui                  |
| HTTP Client       | Axios                      |
| Build Tool        | Maven                      |
| Containerization  | Docker                     |
| API Testing       | Postman                    |
| Version Control   | Git / GitHub               |
| IDE               | IntelliJ IDEA              |

---

# 🔐 Security Architecture

FitSphere uses **Keycloak + Spring Security** to secure the application.

```text
                     React Frontend
                           │
                           ▼
                     Keycloak Login
                           │
                           ▼
                    Access Token / JWT
                           │
                           ▼
                      API Gateway
                           │
                           ▼
                  Spring Security
                           │
                           ▼
                 Protected Microservices
```

### Security Features

* Keycloak authentication
* OAuth2 / OpenID Connect
* JWT access tokens
* Spring Security
* Protected REST endpoints
* Role-based authorization
* Secure frontend authentication
* Token-based API access

---

# 📡 Kafka Event Flow

FitSphere uses **Apache Kafka** for asynchronous activity processing.

```text
User
 │
 ▼
React Frontend
 │
 ▼
API Gateway
 │
 ▼
Activity Service
 │
 │  Publish Activity Event
 ▼
┌───────────────────────┐
│      Apache Kafka     │
│   activity-events     │
└───────────┬───────────┘
            │
            │ Consume Event
            ▼
      ┌─────────────┐
      │ AI Service  │
      └──────┬──────┘
             │
             ▼
       AI Processing
             │
             ▼
     Fitness Recommendation
```

This event-driven approach allows activity processing to happen asynchronously and keeps the services loosely coupled.

---

# 🤖 AI Recommendation Flow

```text
Fitness Activity
       │
       ▼
 Activity Service
       │
       ▼
 Apache Kafka
       │
       ▼
   AI Service
       │
       ▼
 Spring AI
       │
       ▼
 Google Gemini
       │
       ▼
 AI Analysis
       │
       ├── Analysis
       ├── Improvements
       ├── Suggestions
       └── Safety
```

The AI service processes activity events and generates structured fitness insights and recommendations.

---

# 🖥️ Frontend

FitSphere provides a modern React-based frontend.

### Frontend Technologies

* React.js
* Tailwind CSS
* shadcn/ui
* Axios
* React Router
* Keycloak authentication flow
* Responsive UI

### Frontend Responsibilities

* User authentication
* User registration/login flow
* Activity management
* Activity listing
* Fitness recommendations
* API integration
* Protected routes
* Responsive user interface

---

# 📁 Project Structure

```text
FitSphere/
│
├── activityService/
│   └── Spring Boot Activity Microservice
│
├── aiService/
│   └── AI Recommendation Microservice
│
├── configServer/
│   └── Spring Cloud Config Server
│
├── eureka/
│   └── Eureka Service Discovery Server
│
├── gateway/
│   └── Spring Cloud API Gateway
│
├── userService/
│   └── Spring Boot User Microservice
│
└── frontend/
    └── React.js Frontend
```

The GitHub repository currently contains the core backend service modules including `activityService`, `aiService`, `configServer`, `eureka`, `gateway`, and `userService`.

---

# 🚀 Getting Started

## Prerequisites

Install the following before running FitSphere:

* Java 17+
* Maven
* Node.js
* npm
* MySQL
* MongoDB
* Apache Kafka
* Docker
* Keycloak
* Git
* IntelliJ IDEA

---

# 📥 Clone Repository

```bash
git clone https://github.com/shibuy01/FitSphere.git
```

```bash
cd FitSphere
```

---

# ▶️ Start Infrastructure

Before starting the microservices, make sure the following services are running:

```text
MySQL
MongoDB
Apache Kafka
Keycloak
```

---

# ▶️ Start Eureka Server

Navigate to:

```text
eureka/
```

Run:

```bash
mvn spring-boot:run
```

Eureka Dashboard:

```text
http://localhost:8761
```

---

# ⚙️ Start Config Server

Navigate to:

```text
configServer/
```

Run:

```bash
mvn spring-boot:run
```

Default port:

```text
http://localhost:8888
```

---

# 👤 Start User Service

Navigate to:

```text
userService/
```

Run:

```bash
mvn spring-boot:run
```

Default port:

```text
http://localhost:8081
```

---

# 🏃 Start Activity Service

Navigate to:

```text
activityService/
```

Run:

```bash
mvn spring-boot:run
```

Default port:

```text
http://localhost:8082
```

---

# 🤖 Start AI Service

Navigate to:

```text
aiService/
```

Run:

```bash
mvn spring-boot:run
```

Default port:

```text
http://localhost:8084
```

---

# 🌐 Start API Gateway

Navigate to:

```text
gateway/
```

Run:

```bash
mvn spring-boot:run
```

Gateway:

```text
http://localhost:8080
```

---

# 💻 Start React Frontend

Navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start development server:

```bash
npm run dev
```

Frontend:

```text
http://localhost:5173
```

---

# 🔗 API Gateway Routes

The frontend communicates with backend services through the API Gateway.

```text
React Frontend
      │
      ▼
http://localhost:8080
      │
      ├── /api/users/**
      │          │
      │          ▼
      │     USER-SERVICE
      │
      └── /api/activities/**
                 │
                 ▼
            ACTIVITY-SERVICE
```

This keeps the frontend decoupled from individual microservice ports.

---

# 🗄️ Database Architecture

FitSphere uses different databases based on service responsibility.

```text
             FitSphere Microservices
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
   User Service             Activity Service
        │                         │
        ▼                         ▼
      MySQL                     MySQL


                     AI Service
                         │
                         ▼
                      MongoDB
```

### MySQL

Used by:

* User Service
* Activity Service

### MongoDB

Used by:

* AI Service

---

# 🧪 Testing

The APIs can be tested using:

* Postman
* Browser
* React Frontend

Important areas to test:

```text
✓ User Registration
✓ User Login
✓ Authentication
✓ Token Validation
✓ User Validation
✓ Create Activity
✓ View Activities
✓ Kafka Event Publishing
✓ Kafka Event Consumption
✓ AI Processing
✓ Fitness Recommendations
✓ Gateway Routing
```

---

# 🐳 Docker

FitSphere infrastructure can be containerized using Docker.

Recommended containerized components:

```text
┌───────────────────────┐
│       Frontend        │
└───────────┬───────────┘
            │
┌───────────▼───────────┐
│     API Gateway       │
└───────────┬───────────┘
            │
    ┌───────┼────────┐
    │       │        │
    ▼       ▼        ▼
  User   Activity    AI
 Service  Service  Service
    │       │        │
    ▼       ▼        ▼
 MySQL   MySQL    MongoDB

       Apache Kafka
            │
            ▼
        AI Service

       Keycloak
            │
            ▼
      Authentication
```

---

# 🔄 Complete Application Flow

```text
                    USER
                     │
                     ▼
              React Frontend
                     │
                     ▼
                Keycloak
                     │
              Authentication
                     │
                     ▼
                API Gateway
                     │
          ┌──────────┴──────────┐
          │                     │
          ▼                     ▼
     User Service         Activity Service
          │                     │
          ▼                     ▼
        MySQL                 MySQL
                                │
                                │ Event
                                ▼
                           Apache Kafka
                                │
                                ▼
                           AI Service
                                │
                                ▼
                          Spring AI
                                │
                                ▼
                          Google Gemini
                                │
                                ▼
                     Fitness Recommendation
```

---

# 📊 Architecture Benefits

### 🔹 Scalability

Each microservice can be scaled independently according to its workload.

### 🔹 Loose Coupling

Services communicate through APIs and Kafka events instead of depending directly on each other's internal implementation.

### 🔹 Service Discovery

Eureka allows services to discover each other dynamically.

### 🔹 Centralized Configuration

Config Server keeps service configuration centralized and manageable.

### 🔹 Secure Authentication

Keycloak handles identity and authentication while Spring Security protects backend APIs.

### 🔹 Event-Driven Processing

Kafka allows activity events to be processed asynchronously.

### 🔹 AI Integration

Spring AI provides an integration layer for AI-powered fitness recommendations.

---

# ☁️ Deployment

FitSphere is designed as a distributed microservices application and can be deployed using cloud infrastructure.

Possible deployment architecture:

```text
                    Internet
                       │
                       ▼
                React Frontend
                       │
                       ▼
                 API Gateway
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
      User          Activity          AI
    Service         Service         Service
        │              │              │
        ▼              ▼              ▼
     MySQL           MySQL          MongoDB
                       │
                       ▼
                  Apache Kafka

                 Keycloak
                    │
                    ▼
              Authentication

               Eureka Server
                    │
                    ▼
              Service Discovery

              Config Server
                    │
                    ▼
         Centralized Configuration
```

---

# 🔒 Production Security Recommendations

For production deployment:

* Store secrets in environment variables.
* Never commit Keycloak client secrets.
* Never commit database passwords.
* Secure Kafka credentials.
* Use HTTPS.
* Configure CORS carefully.
* Use secure JWT validation.
* Use separate production databases.
* Use environment-specific configuration.
* Enable proper logging and monitoring.
* Use secret-management solutions for sensitive configuration.

---

# 🔮 Future Improvements

### ☁️ DevOps

* Docker Compose
* CI/CD pipeline
* GitHub Actions
* AWS deployment
* Kubernetes
* Container orchestration

### 📊 Monitoring

* Prometheus
* Grafana
* Distributed tracing
* Centralized logging
* Health monitoring

### 🤖 AI

* Advanced fitness analytics
* Personalized workout plans
* Nutrition recommendations
* AI fitness chatbot
* AI-generated weekly plans
* Recommendation history

### 📱 Frontend

* Mobile responsive improvements
* Fitness dashboard
* Activity charts
* Progress tracking
* Workout calendar

### 🔐 Security

* Advanced RBAC
* Refresh token handling
* Fine-grained permissions
* Production OAuth2 configuration

---

# 💡 Key Learning Outcomes

This project demonstrates practical experience with:

* Java backend development
* Spring Boot
* Spring Cloud
* Microservices Architecture
* REST API Development
* Spring Cloud Gateway
* Netflix Eureka
* Spring Cloud Config Server
* Spring Security
* Keycloak
* OAuth2 / OIDC
* JWT
* Apache Kafka
* Event-driven architecture
* Spring AI
* Google Gemini
* MySQL
* MongoDB
* React.js
* Tailwind CSS
* Docker
* Maven
* Git & GitHub

---

# 🎯 Why FitSphere?

FitSphere combines multiple modern backend engineering concepts into a single real-world project.

It demonstrates:

```text
Java
  ↓
Spring Boot
  ↓
Spring Cloud
  ↓
Microservices
  ↓
Eureka
  ↓
Config Server
  ↓
API Gateway
  ↓
Spring Security + Keycloak
  ↓
Apache Kafka
  ↓
Spring AI
  ↓
Google Gemini
  ↓
MySQL + MongoDB
  ↓
React.js
  ↓
Docker
```

This makes FitSphere a strong portfolio project for:

**Java Developer | Java Backend Developer | Spring Boot Developer | Microservices Developer | Software Engineer**

---

# 👨‍💻 Author

## Shibu Kumar

**Java Backend Developer | Spring Boot Developer**

### Technical Skills

```text
Java
SQL
JavaScript

Spring Boot
Spring MVC
Spring Security
REST API Development
Hibernate / JPA
JWT Authentication
Microservices

Spring Cloud
Spring Cloud Gateway
Eureka
Config Server

Apache Kafka
Redis
Spring AI
Google Gemini

React.js
HTML
CSS
Bootstrap
Tailwind CSS

MySQL
PostgreSQL
MongoDB

Docker
AWS EC2
CI/CD

Git
GitHub
Maven
IntelliJ IDEA
Postman
Swagger
```

---

# 🔗 Repository

**GitHub:**

https://github.com/shibuy01/FitSphere

---

# ⭐ Support

If you find this project useful, please consider giving the repository a ⭐ on GitHub.

---

<p align="center">

### 🏋️ Built with Java • Spring Boot • Microservices • Kafka • Keycloak • AI • React

</p>
