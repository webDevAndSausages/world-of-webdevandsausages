#!/usr/bin/env bash

docker build . --build-arg ssh_prv_key="$(cat ~/.ssh/id_rsa_old)" --build-arg ssh_pub_key="$(cat ~/.ssh/id_rsa_old.pub)" -t wds-ansible-docker
docker run --rm -v $(npx realpath ../../wds-event-tool):/opt/wds-event-tool -v $(npx realpath ../../wds-site-v4):/opt/wds-site-v4 -v $(npx realpath .):/opt/infra -it -w /opt/infra wds-ansible-docker bash
