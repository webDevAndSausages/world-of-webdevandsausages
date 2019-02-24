#!/usr/bin/env bash

eval "$(ssh-agent -s)"
ssh-add /root/.ssh/id_rsa

if [ ! -z "$2" ]; then
  TAG_FILTER=--tags\ "$2"
fi

if [ "$1" == "quick" ]; then
  # Quick deploy, just deploy the app, no firewall, certbot, docker and hardening stuff
  env ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -v -i hosts playbook.yml --skip-tags "firewall,certbot,docker-ce,hardening" $TAG_FILTER
  exit 0
fi

# Otherwise do a full deploy on a freshly created server
ansible-galaxy install -r requirements.yml
env ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -v -i hosts playbook.yml
