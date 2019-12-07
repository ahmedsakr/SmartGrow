Developers:
•	Ahmed Sakr
•	Valerie Figuracion

SmartGrow is developed for the purpose of facilitating the ease of growing plants in an optimal environment. Plants require certain levels of light exposure, watering, and air quality in order for optimal growth to be possible.

How the repository is setup
===

The SmartGrow repository is configured into modules that represent the different modules in the system.
•	application: The android application implementation for the SmartGrow phone interface
•	cps: The central processing server implementation for the SmartGrow central server
•	endpoint: The plant endpoint implementation for a SmartGrow plant system that collects sensory information
•	network: The UDP network abstraction layer for all SmartGrow components to communicate
Starting up SmartGrow
===

A Makefile has been created in the root of the repository to allow for quacking building and running of the different components of the system. Makefiles allow for pre-defined build recipes to be configured and executed multiple times by specifying the recipe name. For example, the server recipe (executed by typing ‘make server’ on the command-line) builds and runs the SmartGrow central processing server.
•	To start up the central processing server: make server
•	To start up the plant endpoint: make serial
•	To start up a plant simulation: make simulation

Building the SmartGrow application file
===
Building an android application file that may be loaded onto a phone requires a few more steps. You need to open the application folder using Android Studio and clicking Build -> Build APKs. This will generate an .apk file that may be downloaded on your phone and installed.
