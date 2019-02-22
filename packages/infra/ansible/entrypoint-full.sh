#!/usr/bin/env bash

eval "$(ssh-agent -s)"
ssh-add /root/.ssh/id_rsa
ansible-galaxy install -r requirements.yml
env ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -v -i hosts playbook.yml
