JAVA_LIBRARIES=lib/*
DIST_PATH=dist
SERVER_MAIN=cps/CentralProcessingServer.java
SERVER_CLASS=cps.CentralProcessingServer


all: compile-server

clean:
	rm -rf dist

compile-server:
	javac -proc:none -d "${DIST_PATH}" -cp "${JAVA_LIBRARIES}:." ${SERVER_MAIN}

server: compile-server
	java -cp "${JAVA_LIBRARIES}:${DIST_PATH}:." ${SERVER_CLASS}