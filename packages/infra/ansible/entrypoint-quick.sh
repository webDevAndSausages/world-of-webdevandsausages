#!/usr/bin/env bash

eval "$(ssh-agent -s)"
ssh-add /root/.ssh/id_rsa
env ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -v -i hosts playbook.yml --skip-tags "firewall,certbot,docker-ce,hardening"
