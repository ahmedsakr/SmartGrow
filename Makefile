JAVA_LIBRARIES=lib/*
DIST_PATH=dist

DATABASE_NAME=smartgrow
DATABASE_SCHEMA=cps/database/schemas/plants.sql

SERVER_MAIN=cps/CentralProcessingServer.java
SIMULATION_MAIN=endpoint/simulation/SimulatedPlantEndpoint.java
SERVER_CLASS=cps.CentralProcessingServer
SIMULATION_CLASS=endpoint.simulation.SimulatedPlantEndpoint

NETWORK_JAR=${DIST_PATH}/smartgrow-network.jar
ENDPOINT_JAR=${DIST_PATH}/smartgrow-endpoint.jar
LOGGING_JAR=${DIST_PATH}/smartgrow-logging.jar
SENSORSDATA_UNITTEST=network/unittests/SensorsDataUnittest.java

all: clean compile-server compile-simulation tests network-library endpoint-library logging-library

clean:
	rm -rf dist

create-database:
	psql -U "smartgrow_client" -c "\i ${DATABASE_SCHEMA};"

destroy-database:
	psql -U "smartgrow_client" -c "DROP DATABASE ${DATABASE_NAME};"

compile-server:
	mkdir -p dist
	javac -proc:none -d "${DIST_PATH}" -cp "${JAVA_LIBRARIES}:." ${SERVER_MAIN}

compile-simulation:
	javac -proc:none -d "${DIST_PATH}" -cp "${JAVA_LIBRARIES}:." ${SIMULATION_MAIN}

logging-library:
	jar cf ${LOGGING_JAR} -C ${DIST_PATH} logging

network-library:
	jar cf ${NETWORK_JAR} -C ${DIST_PATH} network

endpoint-library:
	jar cf ${ENDPOINT_JAR} -C ${DIST_PATH} endpoint/sensors

server: compile-server
	java -cp "${JAVA_LIBRARIES}:${DIST_PATH}" ${SERVER_CLASS}

simulation: compile-simulation
	java -cp "${JAVA_LIBRARIES}:${DIST_PATH}" ${SIMULATION_CLASS}

compile-tests:
	javac -cp "${JAVA_LIBRARIES}:${DIST_PATH}" -d "${DIST_PATH}" ${SENSORSDATA_UNITTEST}

tests: compile-tests
	java -cp "${JAVA_LIBRARIES}:${DIST_PATH}" org.junit.runner.JUnitCore network.unittests.SensorsDataUnittest