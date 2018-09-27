# Rediscogs
RediSearch demo based on data from [discogs.com](https://data.discogs.com).

## Setup

This demo requires Java 8, [Redisearch](https://oss.redislabs.com/redisearch/Quick_Start/) and [npm](https://www.npmjs.com)

If you would like to see album covers you will also need to register at Discogs.com and [generate an API token](https://www.discogs.com/settings/developers)

## Running the demo

### RediSearch
To run a RediSearch instance using docker:
```bash
docker run -p 6379:6379 redislabs/redisearch:latest
```

### Server
Clone and build JRediSearch 0.12:
```bash
git clone https://github.com/RedisLabs/JRediSearch.git
cd JRediSearch
mvn clean install -DskipTests
```

Clone this git repository and build it:
```bash
git clone https://github.com/Redislabs-Solution-Architects/rediscogs.git
cd rediscogs
mvn clean install
```

### Running locally
Run the application:
```bash
java -jar server/target/rediscogs-server-0.0.1-SNAPSHOT.jar --discogs-api-token=<your_discogs_token> --spring.redis.host=<host> --spring.redis.port=<port>
```

### Running in Docker
Build the Docker image:
```bash
cd server
mvn dockerfile:build
```
Run the container:
```bash
docker run  -e "spring.redis.host=docker.for.mac.localhost" -e "discogs-api-token=<your_discogs_token>" -p 8080:8080 redislabs/rediscogs
```

### Deploying to Cloud Foundry
1. Create a Redis service instance named `rediscogs_redis` with Apps Manager or `cf create-service`
2. Push the application
```bash
cf push
```

## Demo Steps
### Redis CLI
1. Show number of keys in Redis: `info`
2. Run simple keyword search: `FT.SEARCH mastersIdx spring`
3. Show Hash for one of the previous matches: `HGETALL "master:834798"`
4. Highlight the `_class` field that Spring Data Redis uses to keep track of the original class (for object deserialization purposes)
4. Run prefix search: `FT.SEARCH mastersIdx spring*`
5. Show Hash for one of the previous matches: `HGETALL "master:151353"`

### Web UI
1. Open [http://localhost:8080]()
2. Enter some characters in the Artist field to retrieve suggestions from RediSearch (e.g. `Dusty`)
3. Select an artist from the autocompleted options and click on the `Submit` button
4. Notice how long it takes to load images from the [Discogs API](https://api.discogs.com)
5. After all images have been loaded, click on the `Submit` button again
6. Notice how fast the images are loading this time around
7. In `redis-cli` show cached images: `KEYS "images::*"`
8. Show type of a cached image: `TYPE "images::319832"`
9. Display image bytes stored in String data structure: `GET "images::319832"`
10. Go back to Web UI and select a different artist (e.g. `Bruce Springsteen`)
11. Hit the `Submit` button
12. Refine the search by adding a numeric filter on release year in `Query` field: `@year:[1980 1990]`
13. Refine the search further by adding a filter on release genre: `@year:[1980 1990] @genres:pop`