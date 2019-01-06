#!/usr/bin/env bash
docker build . -t wds-ansible-docker
docker run --rm --net host -v $(realpath ../../wds-event-tool):/opt/wds-event-tool -v $(realpath .):/opt/infra wds-ansible-docker bash -c "ansible-galaxy install -r requirements.yml && ansible-playbook -v --extra-vars \"$1\" -i hosts playbook.yml"
