#!/usr/bin/env bash

eval "$(ssh-agent -s)"
ssh-add /root/.ssh/id_rsa
ansible-galaxy install -r requirements.yml
ansible-playbook -v -i hosts playbook.yml
