# Rediscogs
RediSearch demo based on data from [discogs.com](https://data.discogs.com).

## Setup

This demo requires Java 8, [Redisearch](https://oss.redislabs.com/redisearch/Quick_Start/) and [npm](https://www.npmjs.com)

You will also need to register at Discogs.com and [generate an API token](https://www.discogs.com/settings/developers)

```bash
git clone https://github.com/Redislabs-Solution-Architects/rediscogs.git
cd rediscogs
```

## Running the demo

### RediSearch
To run a RediSearch instance using docker:
```bash
docker run -p 6379:6379 redislabs/redisearch:latest
```

### Server

To run the server, cd into `server` and run:
 
```bash
mvn spring-boot:run -Ddiscogs-api-token=<your_Discogs_token> -Dspring.redis.host=localhost -Dspring.redis.port=6379
```

### Client

To run the client, cd into the `client` folder and run:
 
```bash
npm install
ng serve
```