FROM postgres:10.6-alpine
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
ARG WORKSPACE
ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
ENV AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY

ENV AWSCDK_VERSION=2.61.1
ENV GLIBC_VER=2.34-r0
# override aws-cli v2 default pager
ENV AWS_PAGER=""
ENV PGPASSWORD="password"
ENV WORKSPACE=$WORKSPACE

# RUN apk update && apk upgrade
RUN apk add --no-cache --update python3 python3-dev git jq

#pip3 needs to be run initialy to upgrade pip
RUN python3 -m ensurepip
RUN pip3 install --upgrade pip
RUN pip3 install boto3 \
  json-spec \
  yamllint

# https://github.com/aws/aws-cli/issues/4685#issuecomment-615872019
# install glibc compatibility for alpine
RUN apk --no-cache add \
        binutils \
        curl \
    && curl -sL https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub -o /etc/apk/keys/sgerrand.rsa.pub \
    && curl -sLO https://github.com/sgerrand/alpine-pkg-glibc/releases/download/${GLIBC_VER}/glibc-${GLIBC_VER}.apk \
    && curl -sLO https://github.com/sgerrand/alpine-pkg-glibc/releases/download/${GLIBC_VER}/glibc-bin-${GLIBC_VER}.apk \
    && apk add --force-overwrite --no-cache \
        glibc-${GLIBC_VER}.apk \
        glibc-bin-${GLIBC_VER}.apk \
    && curl -sL https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o awscliv2.zip \
    && unzip awscliv2.zip \
    && aws/install \
    && rm -rf \
        awscliv2.zip \
        aws \
        /usr/local/aws-cli/v2/*/dist/aws_completer \
        /usr/local/aws-cli/v2/*/dist/awscli/data/ac.index \
        /usr/local/aws-cli/v2/*/dist/awscli/examples \
    && apk --no-cache del \
        binutils \
        curl \
    && rm glibc-${GLIBC_VER}.apk \
    && rm glibc-bin-${GLIBC_VER}.apk \
    && rm -rf /var/cache/apk/*

CMD ["/bin/sh", "-c", "pg_dump -h wds-db-$WORKSPACE.internal -p 5432 -d wds_db -U wds > /opt/dump.sql;aws s3 cp /opt/dump.sql s3://wds-production/wds_db_dump_$WORKSPACE.sql"]
