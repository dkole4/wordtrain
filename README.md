# WORDTRAIN #

A web application that helps people to memorize new words from different languages.

### How to set up ###

* Required tools: 
    * SBT, tested version: 1.5.5
    * PostgreSQL, tested version: 12.8
    * Node.js, tested version: 10.19.0
* Backend of the app requires 4 environment variables related to the database: 
    * DB_HOST 
    * DB_NAME
    * DB_USER
    * DB_PASSWD
* Frontend of the app requires an `.env` file inside the [frontend](https://bitbucket.org/dkole4/wordapp/src/master/frontend/) folder that contains `REACT_APP_HOST=?` line, where `?` is the address of your backend.

* The app requires a running PostgreSQL database with tables contained in the [database](https://bitbucket.org/dkole4/wordapp/src/master/database/) folder.
* After setting up the database, use `sbt run` inside the [backend](https://bitbucket.org/dkole4/wordapp/src/master/backend/) folder to launch the backend of the app.
* Use `npm install` and `npm start` inside the [frontend](https://bitbucket.org/dkole4/wordapp/src/master/frontend/) folder to launch the frontend of the app.

### Author ###

* Denis Kole, denis.kole4@gmail.com