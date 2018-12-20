#!/bin/bash
docker run --rm -ePOSTGRES_DB=wds_db -e POSTGRES_USER=wds -e POSTGRES_PASSWORD=password -p 45433:5432 postgres:latest

