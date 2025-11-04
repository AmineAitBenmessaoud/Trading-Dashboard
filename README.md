# Trading Dashboard

A microservices-based trading dashboard application built with Spring Boot and Docker.

## Architecture

This project consists of multiple microservices:
- **Gateway Service**: API Gateway for routing requests (Port 8080)
- **Auth Service**: Authentication and authorization (Port 8081) - *To be implemented*
- **Market Service**: Market data and trading functionality (Port 8082) - *To be implemented*
- **Watchlist Service**: User watchlist management (Port 8083) - *To be implemented*

## Prerequisites

- Java 21+
- Maven 3.6+
- Docker & Docker Compose
- Git

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/AmineAitBenmessaoud/Trading-Dashboard.git
cd Trading-Dashboard
```

### 2. Configure Environment Variables

Copy the example environment file and update with your values:

```bash
cp .env.example .env
```

**Important**: Update the `JWT_SECRET` in `.env` with a strong secret key before running in production.

```env
JWT_SECRET=your-very-secure-secret-key-here
```

### 3. Build and Run with Docker

```bash
docker-compose up --build
```

The services will be available at:
- Gateway Service: http://localhost:8080
- Auth Service: http://localhost:8081
- Market Service: http://localhost:8082
- Watchlist Service: http://localhost:8083

### 4. Development Setup

To run the gateway service locally:

```bash
cd backend/gateway-service
./mvnw spring-boot:run
```

## Security

- **Never commit sensitive data** like API keys, passwords, or secrets to Git
- Always use environment variables for sensitive configuration
- The `.env` file is ignored by Git - keep your secrets there
- Use `.env.example` as a template for required environment variables

## API Documentation

### Health Check
```
GET http://localhost:8080/actuator/health
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.
