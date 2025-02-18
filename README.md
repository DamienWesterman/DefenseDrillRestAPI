# DefenseDrillRestAPI
Spring Rest API Microservice for the [DefenseDrillWeb backend](https://github.com/DamienWesterman/DefenseDrillWeb/).

# Purpose
This microservice is responsible for interacting with the PostgreSQL database containing the core data for the app including drills, instructions, categories, and sub-categories, while exposing access via a RESTful API architecture.

# Security Considerations
Due to the simplicity and low sensitivity nature of the application, this microservice does not check for authorization. As such, it should only be accessed through the [API Gateway](https://github.com/DamienWesterman/DefenseDrillGateway).
