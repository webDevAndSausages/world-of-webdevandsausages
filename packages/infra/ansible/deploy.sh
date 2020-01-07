#!/usr/bin/env bash

MODE=full
if [ "$2" == "quick" ]; then
  MODE=quick
fi

docker build . --build-arg ssh_prv_key="$(cat ~/.ssh/id_rsa_old)" --build-arg ssh_pub_key="$(cat ~/.ssh/id_rsa_old.pub)" -t wds-ansible-docker
docker run --rm -v $(realpath ../../wds-event-tool):/opt/wds-event-tool -v $(realpath ../../wds-site-v4):/opt/wds-site-v4 -v $(realpath .):/opt/infra -it -w /opt/infra wds-ansible-docker ./entrypoint.sh $1 $2
