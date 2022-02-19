# WORDTRAIN #

A web application that helps people to memorize new words from various languages.

## Usage ##

* Required tools: 
    * Docker Engine, version 20.10.12 or newer.
    * Docker Compose, version 1.29.x or newer.

* To run, use command `docker-compose up --build`.
* To enable CORS, change `spring.profiles.active` property of API to 'production'.
* To run on a separate machine:
    * Change environment variable `REACT_APP_HOST`'s hostname to your server's address.
    * Change `wordtr41n.app.jwtSecret` property of API to a secret of the same length (512 bits) or longer.
    * Change `POSTGRES_USER`, `POSTGRES_PASSWORD` and `POSTGRES_DB` enviroment variables to suitable values in `docker-compose.yml`.
    * Change `spring.datasource.url`, `spring.datasource.username` and `spring.datasource.password` properties to chosen values.

## Roadmap ##

* Application as a whole
    * [ ] Wider choice of languages
    * [ ] Phrases
        * [ ] Training view for phrases
    * [ ] Difficulty level for words
        * [ ] Word recommendations/lists based on user level

* API
    * [x] Basic insertion, changing and removal of words
    * [ ] Authentication and authorization
        * [x] JWT authentication
        * [ ] User roles
        * [ ] (Optional) Email usage
    * [x] Resolve conflicts if users have same words
    * [ ] Automated testing
    * [ ] Allowed language check

* Frontend
    * [x] Login and sign up forms
    * [x] Word insertion, changing and removal
        * [x] Free form insertion
    * [x] Training view
    * [x] Word search
    * [ ] Automated testing

### Author ###

* Denis Kole, denis.kole4@gmail.com