#!/usr/bin/env bash

MODE=full
if [ "$2" == "quick" ]; then
  MODE=quick
fi

docker build . --build-arg ssh_prv_key="$(cat ~/.ssh/id_rsa)" --build-arg ssh_pub_key="$(cat ~/.ssh/id_rsa.pub)" -t wds-ansible-docker
docker run --rm -v $(realpath ../../wds-event-tool):/opt/wds-event-tool -v $(realpath ../../wds-client):/opt/wds-client  -v $(realpath .):/opt/infra -it -w /opt/infra wds-ansible-docker ./entrypoint.sh $1 $2
