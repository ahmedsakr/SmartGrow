JAVA_LIBRARIES=lib/*
DIST_PATH=dist

DATABASE_NAME=smartgrow
DATABASE_SCHEMA=cps/database/schemas/plants.sql

SERVER_MAIN=cps/CentralProcessingServer.java
SIMULATION_MAIN=endpoint/simulation/SimulatedPlantEndpoint.java
SERVER_CLASS=cps.CentralProcessingServer
SIMULATION_CLASS=endpoint.simulation.SimulatedPlantEndpoint


all: clean compile-server compile-simulation

clean:
	rm -rf dist

create-database:
	psql -U "smartgrow_client" -c "\i ${DATABASE_SCHEMA};"

destroy-database:
	psql -U "smartgrow_client" -c "DROP DATABASE ${DATABASE_NAME};"

compile-server:
	javac -proc:none -d "${DIST_PATH}" -cp "${JAVA_LIBRARIES}:." ${SERVER_MAIN}

compile-simulation:
	javac -proc:none -d "${DIST_PATH}" -cp "${JAVA_LIBRARIES}:." ${SIMULATION_MAIN}

server: compile-server
	java -cp "${JAVA_LIBRARIES}:${DIST_PATH}:." ${SERVER_CLASS}

simulation: compile-simulation
	java -cp "${JAVA_LIBRARIES}:${DIST_PATH}:." ${SIMULATION_CLASS}