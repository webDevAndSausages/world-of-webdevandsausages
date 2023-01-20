FROM node:18.13.0 AS build
COPY . /opt/app
ARG WDS_API_KEY
WORKDIR /opt/app
RUN touch .env
RUN echo 'PUBLIC_WDS_API_URL=""' >> .env
RUN echo "PUBLIC_WDS_API_KEY=$WDS_API_KEY" >> .env
RUN npm ci
RUN npm run build

FROM nginx:1.23.3
ARG WORKSPACE
RUN mkdir /opt/frontend
COPY --from=build /opt/app/build /opt/frontend
RUN rm /etc/nginx/conf.d/default.conf
COPY --from=build /opt/app/wds-nginx.conf /etc/nginx/conf.d/default.conf
RUN sed -i "s/\$WORKSPACE/$WORKSPACE/g" /etc/nginx/conf.d/default.conf
RUN sed -i "s/\$WDS_KEY/$WDS_API_KEY/g" /etc/nginx/conf.d/default.conf
