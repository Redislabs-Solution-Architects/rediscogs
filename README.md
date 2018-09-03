# Rediscogs
RediSearch demo based on data from [discogs.com](https://data.discogs.com).

## Setup

This demo requires Java 8 and [npm](https://www.npmjs.com)

You will also need to register at Discogs.com and [generate an API token](https://www.discogs.com/settings/developers)

```bash
git clone https://github.com/Redislabs-Solution-Architects/rediscogs.git
cd rediscogs
```

## Running the demo

### Server

To run the server, cd into `server` and run:
 
```bash
mvn spring-boot:run -Ddiscogs-api-token=<YOUR_DISCOGS_TOKEN>
```

### Client

To run the client, cd into the `client` folder and run:
 
```bash
npm install
ng serve
```
