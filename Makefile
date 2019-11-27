JAVA_LIBRARIES=lib/*
DIST_PATH=dist

DATABASE_NAME=smartgrow
DATABASE_SCHEMA=cps/database/schemas/smartgrow.sql

SERVER_MAIN=cps/CentralProcessingServer.java
SIMULATION_MAIN=endpoint/simulation/SimulatedPlantEndpoint.java
SERVER_CLASS=cps.CentralProcessingServer
SIMULATION_CLASS=endpoint.simulation.SimulatedPlantEndpoint

NETWORK_JAR=${DIST_PATH}/smartgrow-network.jar
ENDPOINT_JAR=${DIST_PATH}/smartgrow-endpoint.jar
LOGGING_JAR=${DIST_PATH}/smartgrow-logging.jar
SENSORSDATA_UNITTEST=network/unittests/SensorsDataUnittest.java

all: clean compile-server compile-simulation tests network-library endpoint-library logging-library install-libraries

clean:
	@echo "Cleaning the existing build"
	@rm -rf dist
	@mkdir -p dist

create-database:
	@echo "Creating SmartGrow database"
	@psql -U "smartgrow_client" -c "\i ${DATABASE_SCHEMA};"

destroy-database:
	@echo "Destroying SmartGrow database"
	@psql -U "smartgrow_client" -c "DROP DATABASE ${DATABASE_NAME};"

compile-server:
	@echo "Compiling server"
	@javac -proc:none -d "${DIST_PATH}" -cp "${JAVA_LIBRARIES}:." ${SERVER_MAIN}

compile-simulation:
	@echo "Compiling simulation"
	@javac -proc:none -d "${DIST_PATH}" -cp "${JAVA_LIBRARIES}:." ${SIMULATION_MAIN}

logging-library:
	@echo "Creating SmartLog Library"
	@jar cf ${LOGGING_JAR} -C ${DIST_PATH} logging

network-library:
	@echo "Creating SmartGrow Network Library"
	@jar cf ${NETWORK_JAR} -C ${DIST_PATH} network

endpoint-library:
	@echo "Creating SmartGrow Endpoint Library"
	@jar cf ${ENDPOINT_JAR} -C ${DIST_PATH} endpoint/sensors

server: compile-server
	@echo "Running server"
	@java -cp "${JAVA_LIBRARIES}:${DIST_PATH}" ${SERVER_CLASS}

simulation: compile-simulation
	@echo "Running simulation"
	@java -cp "${JAVA_LIBRARIES}:${DIST_PATH}" ${SIMULATION_CLASS}

compile-tests:
	@echo "Compling tests"
	@javac -cp "${JAVA_LIBRARIES}:${DIST_PATH}" -d "${DIST_PATH}" ${SENSORSDATA_UNITTEST}

tests: compile-tests
	@echo "Running tests"
	@java -cp "${JAVA_LIBRARIES}:${DIST_PATH}" org.junit.runner.JUnitCore network.unittests.SensorsDataUnittest

install-libraries:
	@echo "Installing SmartGrow libraries in android application"
	@mkdir -p application/app/libs
	@cp dist/*.jar application/app/libs