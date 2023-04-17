# **Summary**

This project involved learning Scala and its frameworks and concepts such as cats, cats-effects, http4s, fs2, sbt, and tagless final. The learning phase proved valuable, as it led to contributing to fixing an issue found in the official Scala documentation (**[https://github.com/scala/docs.scala-lang/pull/2764](https://github.com/scala/docs.scala-lang/pull/2764)**).

## ****Strategy and Decision-Making****

Based on the requirements, the service should support at least 10,000 successful requests per day with 1 API token, and the rate should not be older than 5 minutes. The One-Frame service has a limitation of supporting only 1,000 requests per day for any given authentication token.

To work around this limitation, the solution refreshes the cache every 90 seconds. Here's how the 90-second interval was determined:

1. There are 86,400 seconds in a day (24 hours * 60 minutes * 60 seconds).
2. The rate should not be older than 5 minutes, so the cache can be updated every 5 minutes (300 seconds).
3. The One-Frame service allows 1,000 requests per day, which means you can make 86,400 / 1,000 = 86.4 requests per day evenly.
4. The ideal refresh interval would be 300 seconds / 86.4 = ~3.47 seconds.
5. However, making a request every 3.47 seconds would use up the entire quota of 1,000 requests per day, leaving no room for extra requests or occasional spikes in traffic. To ensure the service supports at least 10,000 successful requests per day, a more conservative refresh interval is chosen, such as 90 seconds.

Using a 90-second interval to refresh the cache helps ensure that the rates are not older than 5 minutes and that there is enough room to handle at least 10,000 successful requests per day with the given One-Frame API limitations.

## ****Implementation Overview****

The services were divided into three parts:

1. **`services/oneframe`**: This component handles communication with the OneFrame API to retrieve exchange rates.
2. **`services/cache`**: This component manages an in-memory cache for efficient lookups.
3. **`services/worker`**: This component is responsible for using the API client provided by **`services/oneframe`** to fetch exchange rates and cache them entirely in the cache managed by **`services/cache`**. This worker runs in the background every 90 seconds to refresh the cache contents and ensure the 5-minute requirement is met.

For simplicity, an in-memory implementation of the cache was used via a Scala Map. A more robust solution would be to utilize a proven library like Redis.

## ****Running the Application****

**Prerequisites**

Before you can run the application, ensure that you have installed Docker and Docker Compose on your system. If you haven't installed them yet, follow the instructions below:

1. **Install Docker**

   Download and install Docker for your specific platform from the official website: **[https://www.docker.com/get-started](https://www.docker.com/get-started)**

   Follow the installation instructions for your operating system. Once installed, check if Docker is running correctly by running the following command in your terminal:

    ```
    docker --version
    ```

   This should display the installed Docker version.

2. **Install Docker Compose**

   Docker Compose is included in the Docker Desktop installation for Windows and macOS. For Linux, you'll need to install Docker Compose separately.

   Follow the installation instructions for your platform provided in the official documentation: **[https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)**

   After installation, verify if Docker Compose is installed correctly by running the following command:

    ```
    docker-compose --version
    ```

   This should display the installed Docker Compose version.


With Docker and Docker Compose installed, you can now proceed to dockerize the service, run Docker Compose, and access the service.

1. **Dockerize the Service**

   Package the service into a Docker container using the following command:

    ```
    sbt docker:publishLocal
    ```

2. **Run Docker Compose**

   Launch the containerized application using Docker Compose:

    ```
    docker-compose up
    ```

3. **Access the Service**

   Send a request to the running service using the curl command:

    ```
    curl -v 'http://localhost:9000/rates?from=USD&to=JPY'
    ```

   This will fetch the exchange rate between USD and JPY from the locally running service.

