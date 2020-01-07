#!/usr/bin/env bash

COMMAND=$2

docker build . --build-arg ssh_prv_key="$(cat ~/.ssh/id_rsa_old)" --build-arg ssh_pub_key="$(cat ~/.ssh/id_rsa_old.pub)" -t wds-ansible-docker
docker run --rm -v $(realpath ../../wds-event-tool):/opt/wds-event-tool -v $(realpath .):/opt/infra -it -w /opt/infra wds-ansible-docker ./admin-entrypoint.sh $1 $COMMAND
