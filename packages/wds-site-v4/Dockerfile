FROM node:13.10-alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json /app/package.json
COPY package-lock.json /app/package-lock.json
RUN npm install
COPY . /app
RUN npm run export

# production environment
FROM nginx:1.17-alpine
COPY --from=build /app/__sapper__/export /usr/share/nginx/html
COPY docker-nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
