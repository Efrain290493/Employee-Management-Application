# Employee Management System

## Project Overview

Employee Management System is a comprehensive web application designed to manage and retrieve employee information efficiently. The application leverages modern Java technologies for backend development and React with Redux for the frontend.

## Technologies Stack

### Backend
- Java 17
- Spring Boot 3.2.5
- Spring Cloud 2023.0.0
- Spring Cloud OpenFeign
- Spring Cache
- Maven
- Caffeine Cache 3.1.8

### Frontend
- React 18
- Redux
- Vite
- Axios

### Key Features
- Retrieve employee list
- View individual employee details
- Calculate annual salary
- Caching mechanism
- Error handling
- Rate limiting
- Retry mechanism for external API calls

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) 17
- Maven 3.8+
- Node.js 16+
- npm or yarn

## Backend Setup

### Clone the Repository Backend
```bash
git clone https://github.com/Efrain290493/Employee-Management-Application.git


```

```bash
### Clone the Repository Backend
git clone https://github.com/Efrain290493/Employee-management-frontend.git
```

### Backend Configuration
1. Navigate to backend directory
2. Configure `application.properties` or `application.yml`

```yaml
# API External Configuration
api:
  external:
    base-url: http://dummy.restapiexample.com/api/v1/
    employees-endpoint: employees
    employee-by-id-endpoint: employee/{id}

# Cache Configuration
cache:
  employees:
    max-size: 100
    expire-after-minutes: 60
```

### Build and Run Backend
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## Frontend Setup

### Navigate to Frontend Directory
```bash
cd frontend
```

### Install Dependencies
```bash
# Using npm
npm install

# Using yarn
yarn install
```

### Environment Configuration
Create a `.env` file in the frontend directory:
```
VITE_API_BASE_URL=http://localhost:8080/api
```

### Run Frontend
```bash
# Using npm
npm run dev

# Using yarn
yarn dev
```

## API Endpoints

### Employees
- `GET /api/employees`: Retrieve all employees
- `GET /api/employees/{id}`: Retrieve specific employee
- `GET /api/employees/{id}/annual-salary`: Calculate annual salary

## Error Handling

The application implements comprehensive error handling:
- Resource Not Found
- Rate Limit Exceeded
- External API Errors
- Generic Server Errors

## Caching Strategy

- Employees list cached for 1 hour
- Individual employee details cached by ID
- Configurable cache size and expiration

## Rate Limiting

- Limits simultaneous API calls to external service
- Configurable number of concurrent requests
- Timeout mechanism

## Retry Mechanism

- Exponential backoff for failed requests
- Configurable retry attempts
- Prevents overwhelming external services

## Security

- CORS configured
- Basic error handling
- Secure API interactions

## Logging

Comprehensive logging implemented using SLF4J:
- Debug logs for API interactions
- Warn logs for rate limit events
- Error logs for exceptions

## Performance Optimization

- Reactive programming with WebClient
- Efficient caching
- Rate limiting
- Retry mechanisms

## Deployment

### Backend
- Deployable as JAR or WAR
- Compatible with major application servers
- Docker support available

### Frontend
- Build for production
```bash
npm run build
```

## Testing

### Backend
```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify
```

### Frontend
```bash
# Run tests
npm test
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Contact

Efrain Lopez Mazo efrain.lopezmazo@amaris.com

Project Link: [https://github.com/Efrain290493/Employee-management-frontend](https://github.com/your-username/employee-management)
```