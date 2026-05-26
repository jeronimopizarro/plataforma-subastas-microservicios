# 1. Usamos una imagen oficial de Java 21 ligera (Alpine)
FROM eclipse-temurin:21-jre-alpine

# 2. Creamos una carpeta dentro del contenedor donde vivirá nuestra app
WORKDIR /app

# 3. Copiamos el archivo .jar que compiló Maven en el paso anterior hacia el contenedor
COPY target/*.jar app.jar

# 4. Le decimos a Docker qué puerto usa este microservicio
EXPOSE 8080

# 5. El comando exacto que Docker ejecutará para encender tu aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]