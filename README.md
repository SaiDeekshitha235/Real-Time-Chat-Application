# Real-Time Chat Application 💬

A full-stack real-time messaging application built with Spring Boot, WebSocket, and React. Supports user authentication, one-on-one conversations, and instant message delivery.

## ✨ Features

- 🔐 JWT-based user authentication (signup, login, logout)
- 💬 Real-time messaging via WebSocket (STOMP protocol)
- 👥 One-on-one and group conversations
- 📜 Persistent message history
- 🎨 Modern responsive UI with React
- 🔒 Secure REST APIs with Spring Security

## 🛠️ Tech Stack

**Backend:**
- Java 17, Spring Boot 3
- Spring Security + JWT
- Spring WebSocket (STOMP)
- Spring Data JPA
- MySQL / PostgreSQL

**Frontend:**
- React 18 (Vite)
- Tailwind CSS
- Axios for REST calls
- SockJS + STOMP.js for WebSocket

## 📁 Project Structure

\`\`\`
chat-app/
├── chat-backend/          # Spring Boot REST + WebSocket server
│   ├── src/main/java/com/chatapp/
│   │   ├── config/        # Security, CORS, WebSocket config
│   │   ├── controller/    # REST controllers
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── entity/        # JPA entities
│   │   ├── repository/    # Spring Data repos
│   │   ├── security/      # JWT filter & token provider
│   │   ├── service/       # Business logic
│   │   └── websocket/     # WebSocket message handlers
│   └── pom.xml
└── chat-frontend/         # React client app
    ├── src/
    │   ├── components/    # Reusable UI components
    │   ├── pages/         # Login, Signup, Chat, Profile pages
    │   ├── services/      # API service modules
    │   ├── context/       # Auth context
    │   └── hooks/         # Custom React hooks
    └── package.json
\`\`\`

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.8+

### Backend Setup
\`\`\`bash
cd chat-backend
mvn clean install
mvn spring-boot:run
\`\`\`
Backend runs at `http://localhost:8080`

### Frontend Setup
\`\`\`bash
cd chat-frontend
npm install
npm run dev
\`\`\`
Frontend runs at `http://localhost:5173`

## 📸 Screenshots

*(Add screenshots of your login page, chat page, etc. here later)*

## 👩‍💻 Author

**Sai Deekshitha K R**
- GitHub: [@SaiDeekshitha235](https://github.com/SaiDeekshitha235)
- LinkedIn: *(add your LinkedIn URL)*

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
