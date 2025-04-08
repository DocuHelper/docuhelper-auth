FROM openjdk:17-jdk-slim-buster
LABEL maintainer=docuhelper-nextjs

ENV DB_HOST=192.168.0.7
ENV DB_PORT=5432
ENV DB_USER=postgres
ENV DB_PASSWD=password
ENV JWT_SECRET=e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
ENV OAUTH_GOOGLE_CLIENT_ID \
    OAUTH_GOOGLE_CLIENT_SECRE

COPY . /app

WORKDIR app

RUN chmod +x gradlew

RUN sh gradlew build --warning-mode all

ENTRYPOINT ["java", "-jar", "build/libs/docuhelper-auth-0.0.1-SNAPSHOT.jar"]