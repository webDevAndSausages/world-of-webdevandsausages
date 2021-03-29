#!/usr/bin/env bash

eval "$(ssh-agent -s)"
ssh-add /root/.ssh/id_rsa

# Otherwise do a full deploy on a freshly created server
ansible-galaxy collection install community.kubernetes
ansible-galaxy install -r requirements-kube.yml
env ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -v -i "kube-staging" kube-playbook.yml
