FROM node:16-alpine
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
ENV REACT_APP_HOST localhost:8080
COPY package.json ./
# COPY package-lock.json ./
RUN npm install --silent
COPY . ./
ENTRYPOINT ["npm", "start"]