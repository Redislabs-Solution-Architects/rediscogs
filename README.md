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
Clone this git repository:
```bash
git clone https://github.com/Redislabs-Solution-Architects/rediscogs.git
cd rediscogs
```

Build the project:
```bash
mvn clean install
```

Run the application:
```bash
java -jar server/target/server-0.0.1-SNAPSHOT.jar --discogs-api-token=<your_discogs_token> --spring.redis.host=localhost --spring.redis.port=6379
```

### Deploying to Cloud Foundry
1. Create a Redis Enterprise service instance named `rediscogs_redis` in Apps Manager
2. Push the application
```bash
cf push
```
