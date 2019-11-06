
CREATE DATABASE smartgrow;
GRANT ALL PRIVILEGES ON DATABASE smartgrow TO smartgrow_client;
\c smartgrow;

CREATE TABLE recommended_properties(
    plant_name VARCHAR(32) PRIMARY KEY,
    light_intensity NUMERIC,
    air_humidity NUMERIC,
    air_temperature NUMERIC,
    soil_moisture NUMERIC
);

CREATE TABLE plant_data(
    plant_id NUMERIC,
    light_intensity NUMERIC,
    air_humidity NUMERIC,
    air_temperature NUMERIC,
    soil_moisture NUMERIC
);