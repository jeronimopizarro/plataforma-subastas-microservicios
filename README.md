# Plataforma de subastas en tiempo real - Microservicios 

Plataforma de subastas en tiempo real diseñada bajo una arquitectura de **Microservicios**. Este proyecto demuestra el
manejo de sistemas distribuidos, concurrencia y comunicación en tiempo real, aplicando estrictamente los principios de *
*Clean Architecture**, **Domain-Driven Design (DDD)** y **SOLID**.

## Arquitectura y Diseño

El sistema abandona el enfoque monolítico tradicional para separar las responsabilidades en servicios independientes,
permitiendo un escalado quirúrgico y alta tolerancia a fallos. La arquitectura interna de cada microservicio está
dividida en 4 capas (Domain, Application, Infrastructure, Web) asegurando un bajo acoplamiento y alta cohesión.

### Estructura de Microservicios (En desarrollo)

1. **Auction Service (Core):** Gestión transaccional de productos y subastas (Apertura, finalización y reglas de negocio
   estáticas).
2. **Bidding Service (Real-Time):** Manejo de conexiones persistentes (WebSockets) y alta concurrencia para el
   procesamiento de pujas en vivo.
3. **Wallet / Payment Service (Mock):** Simulación de billetera virtual para garantizar transacciones distribuidas
   seguras.

## Stack Tecnológico

* **Backend:** Java 21, Spring Boot 3
* **Arquitectura:** Microservices, Clean Architecture, Domain-Driven Design (DDD)
* **Persistencia:** Spring Data JPA, H2 Database (Fase inicial) / PostgreSQL (Producción)
* **Tiempo Real:** Spring WebSocket (STOMP)
* **Gestión de Proyecto:** Maven
* **Control de Versiones:** Git & GitHub
* **Despliegue & Orquestación:** Docker & Docker Compose (Próximamente)

## Alcance del Proyecto (B2C Model)

* Los **Administradores** tienen la capacidad de publicar productos y gestionar el ciclo de vida de una subasta (
  apertura, cancelación, cierre).
* Los **Usuarios (Compradores)** reciben una billetera virtual al registrarse y pueden conectarse a subastas activas
  para realizar pujas en tiempo real.
* El sistema resuelve automáticamente al ganador al finalizar el tiempo establecido, deduciendo los fondos mediante
  comunicación entre microservicios.