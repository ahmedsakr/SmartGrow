JAVA_LIBRARIES=lib/*
DIST_PATH=dist

DATABASE_NAME=smartgrow
DATABASE_SCHEMA=cps/database/schemas/plants.sql

SERVER_MAIN=cps/CentralProcessingServer.java
SERVER_CLASS=cps.CentralProcessingServer


all: compile-server

clean:
	rm -rf dist

create-database:
	psql -c "\i ${DATABASE_SCHEMA};"

destroy-database:
	psql -c "DROP DATABASE ${DATABASE_NAME};"

compile-server:
	javac -proc:none -d "${DIST_PATH}" -cp "${JAVA_LIBRARIES}:." ${SERVER_MAIN}

server: compile-server
	java -cp "${JAVA_LIBRARIES}:${DIST_PATH}:." ${SERVER_CLASS}