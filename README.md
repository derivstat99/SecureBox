# SecureBox

A secure cloud-based document management platform built with Spring Boot. Upload, organize, and manage personal files through a nested folder structure — with AI-powered PDF summarization.

---

## Features

- **Authentication** — Register, login, JWT-based sessionless auth, BCrypt password encryption
- **Folders** — Create nested folders, rename, delete, view full hierarchy
- **Files** — Upload, download, rename, associate with folders (5MB limit per file)
- **Cloud Storage** — Files stored on Cloudinary; metadata in PostgreSQL
- **Favorites** — Star and filter important files
- **Trash** — Soft delete, restore, or permanently remove files
- **Search** — Find files by name
- **Storage Dashboard** — Track usage against a 100MB quota
- **Recent Uploads** — Quickly access newly added files
- **AI Summarization** — Extract text from PDFs and generate summaries via Google Gemini

---

## Tech Stack

| | |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 4, Spring Security, Spring Data JPA |
| **Database** | PostgreSQL |
| **Cloud Storage** | Cloudinary |
| **AI** | Google Gemini API, Apache PDFBox |
| **Auth** | JWT, BCrypt |
| **Build** | Maven |

---

## Architecture

```
Client (Postman / Frontend)
        │
        ▼
   Controllers        ← REST endpoints, HTTP handling
        │
        ▼
    Services          ← Business logic
        │
   ┌────┴────┐
   ▼         ▼
Repositories  External APIs
(PostgreSQL)  (Cloudinary, Gemini)
```

---

## Prerequisites

- Java 21+
- Maven (or use included `./mvnw`)
- PostgreSQL (local)
- Cloudinary account — [cloudinary.com](https://cloudinary.com)
- Google Gemini API key — [Google AI Studio](https://aistudio.google.com/apikey)

---

## Getting Started

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd SecureBox
```

### 2. Create the database

```sql
CREATE DATABASE securebox;
```

### 3. Configure environment variables

Set these in your shell before running the app:

```powershell
# Windows PowerShell
$env:CLOUDINARY_CLOUD_NAME="your-cloud-name"
$env:CLOUDINARY_API_KEY="your-api-key"
$env:CLOUDINARY_API_SECRET="your-api-secret"
$env:GEMINI_API_KEY="your-gemini-key"
$env:JWT_SECRET="your-long-random-secret-key"
```

```bash
# Linux / macOS
export CLOUDINARY_CLOUD_NAME="your-cloud-name"
export CLOUDINARY_API_KEY="your-api-key"
export CLOUDINARY_API_SECRET="your-api-secret"
export GEMINI_API_KEY="your-gemini-key"
export JWT_SECRET="your-long-random-secret-key"
```

### 4. Update database credentials (if needed)

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/securebox
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 5. Run the application

```bash
./mvnw spring-boot:run
```

Server starts at `http://localhost:8080`

---

## API Reference

All protected endpoints require the header:

```
Authorization: Bearer <your-jwt-token>
```

### Auth

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | No | Register a new user |
| POST | `/api/auth/login` | No | Login and receive JWT |

### User

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/user/profile` | Get logged-in user profile |

### Folders

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/folders?name=&parentId=` | Create folder |
| GET | `/api/folders` | Get folder tree |
| PUT | `/api/folders/{id}?name=` | Rename folder |
| DELETE | `/api/folders/{id}` | Delete folder |

### Files

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/files/upload` | Upload file (multipart) |
| GET | `/api/files` | List all active files |
| GET | `/api/files/folder/{folderId}` | List files in folder |
| GET | `/api/files/{id}/download` | Download file |
| PUT | `/api/files/{id}?name=` | Rename file |
| DELETE | `/api/files/{id}` | Move to trash |
| POST | `/api/files/{id}/restore` | Restore from trash |
| DELETE | `/api/files/{id}/permanent` | Delete permanently |
| PUT | `/api/files/{id}/favorite` | Toggle favorite |
| GET | `/api/files/favorites` | List favorites |
| GET | `/api/files/trash` | List trashed files |
| GET | `/api/files/search?q=` | Search by filename |
| GET | `/api/files/storage` | Storage usage stats |
| GET | `/api/files/recent?limit=10` | Recent uploads |

### AI

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/files/{id}/summarize` | Summarize a PDF file |

---

## Example Usage

### Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "harsh",
  "password": "secret123"
}
```

### Upload a PDF

```http
POST /api/files/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: document.pdf
folderId: 1   (optional)
```

### Summarize a PDF

```http
POST /api/files/1/summarize
Authorization: Bearer <token>
```

Response:

```json
{
  "fileId": 1,
  "fileName": "document.pdf",
  "summary": "This document covers...",
  "truncated": false
}
```

---

## Project Structure

```
src/main/java/com/octral/SecureBox/
├── config/           # Security, Cloudinary configuration
├── controller/       # REST endpoints
├── dto/              # Request/response objects
├── model/            # JPA entities
├── repository/       # Database access
├── security/         # JWT filter, auth utilities
└── service/          # Business logic
    ├── CloudinaryService.java
    ├── FileService.java
    ├── FolderService.java
    ├── GeminiService.java
    ├── PdfTextExtractorService.java
    └── DocumentSummaryService.java
```

---

## Roadmap

- [ ] Global exception handling
- [ ] Request validation
- [ ] Swagger / OpenAPI docs
- [ ] Docker support
- [ ] Persist AI summaries to database
- [ ] Basic frontend UI

---

## License

This project is for personal learning and portfolio use.
