#!/usr/bin/env bash

eval "$(ssh-agent -s)"
ssh-add /root/.ssh/id_rsa
ansible-galaxy collection install -r requirements-kube.yml
ansible-galaxy install -r requirements-kube.yml

bash

