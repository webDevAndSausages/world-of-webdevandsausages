FROM williamyeh/ansible:ubuntu18.04

RUN apt update -y && apt install software-properties-common -y && apt-add-repository ppa:ansible/ansible -y && apt update -y && apt install ansible -y

ARG ssh_prv_key
ARG ssh_pub_key

# Authorize SSH Host
RUN mkdir -p /root/.ssh && \
    chmod 0700 /root/.ssh

# Add the keys and set permissions
RUN echo "$ssh_prv_key" > /root/.ssh/id_rsa && \
    echo "$ssh_pub_key" > /root/.ssh/id_rsa.pub && \
    chmod 600 /root/.ssh/id_rsa && \
    chmod 600 /root/.ssh/id_rsa.pub

WORKDIR /opt/infra

RUN apt-get update && apt-get -y install rsync && apt-get -y install python-simplejson && apt-get install -y python && apt-get install -y python3-pip && apt-get install -y nodejs && apt-get install -y npm
