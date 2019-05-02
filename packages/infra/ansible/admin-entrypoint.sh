#!/usr/bin/env bash

eval "$(ssh-agent -s)"
ssh-add /root/.ssh/id_rsa

ansible-galaxy install andrewvaughan.prompt
env ANSIBLE_HOST_KEY_CHECKING=False ANSIBLE_STDOUT_CALLBACK=debug ansible-playbook -i "$1" admin-playbook.yml --tags "$2"
