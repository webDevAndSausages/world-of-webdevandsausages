#!/usr/bin/env bash

# Otherwise do a full deploy on a freshly created server
env ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -vvvv -i "kube-staging" kube-playbook.yml $@
