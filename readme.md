# Contract Management API - Personal Learning Project

## 📋 Project Overview

This is a **personal backend project** I'm building to deepen my understanding of **Spring Boot, REST APIs, and modern backend development**. The goal is to create a fully-functional contract management system where users can create, manage, and track various types of contracts.

### 🎯 My Learning Objectives
- Master **Spring Boot 4.0.0** with Java 21
- Implement **JWT-based authentication** from scratch
- Design **clean REST APIs** with proper security
- Practice **database design** with PostgreSQL
- Learn **professional error handling** and validation
- Eventually connect this backend with a **React/Vue frontend** (future phase)

## 🚀 What This API Does

### 🔐 **Security & Authentication**
I implemented **JWT (JSON Web Token) authentication** where:
- Users can register and login
- Each request is secured with a Bearer token
- Tokens expire and can be refreshed
- Different roles (USER/ADMIN) have different permissions

### 📄 **Contract Management Core Features**
Users can:
- **Create** new contracts with titles, content, and categories
- **View** all their contracts or filter them
- **Update** contract details (but not status directly)
- **Change status** through specific actions (like publishing)
- **Track** creation dates and last updates

### ⚙️ **Smart Design Choices I Made**
1. **Separate status updates** - Contracts have a lifecycle (Draft → Pending → Active → Archived)
2. **Protected endpoints** - You can only edit your own contracts
3. **Automatic timestamps** - Created/Updated dates managed automatically
4. **Input validation** - All data is validated before processing

## 🏗️ API Structure (Simple Version)

### **Authentication Controllers** (`/api/v1/auth/`)
- `POST /register` - Create new account
- `POST /login` - Get JWT tokens
- `POST /refresh` - Get new tokens when old ones expire
- `GET /me` - See current user info

### **Contract Controllers** (`/api/v1/contracts/`)
- `GET /` - List all my contracts
- `GET /{id}` - Get specific contract
- `POST /` - Create new contract (starts as "Draft")
- `PUT /{id}` - Update contract details
- `PATCH /{id}/publish` - Publish a draft contract
- `DELETE /{id}` - Remove a contract

### **Admin Controllers** (`/api/v1/admin/`) 
- Special endpoints for administrators to manage all contracts

## 🎓 What I'm Learning Through This Project

### **Backend Skills I'm Practicing:**
- Building **secure REST APIs** from scratch
- Implementing **JWT authentication flow**
- **Database relationships** and query optimization
- **Error handling** and meaningful error messages
- **API documentation** with Swagger/OpenAPI
- **Testing** REST endpoints

### **Future Plans:**
1. **Add more features** like contract templates, reminders
2. **Improve search** with filters and sorting
3. **Add file attachments** to contracts
4. **Build a frontend** (React or Vue.js) to consume this API
5. **Deploy** to cloud (AWS/Azure) for real-world experience

## 💡 Why I Built This

This isn't just another tutorial project—it's my **hands-on learning journey** into professional backend development. Each challenge (like getting JWT refresh tokens working) teaches me real problem-solving skills I'll use in my career.

---

*This project represents my commitment to mastering backend development—one controller, one service, one bug fix at a time.* 🚀