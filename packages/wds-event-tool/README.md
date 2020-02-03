# WDS Event Tool Backend

## Getting Started

1. Start `/scripts/database/start_dev_database.sh`
2. Run the `flywayMIgrate` gradle task
3. Run the `build` gradle task

## Running with docker
First build the containers:
```sh~
$ docker-compose build
```
Then run:
```sh
$ docker-compose up
```
