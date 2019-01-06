# Deploy with ansible
To test this deploy script locally, run some test container with ubuntu:
```bash
$ docker run --rm --privileged -d -P -p 80:80 -p 443:443 --name test_sshd rastasheep/ubuntu-sshd:14.04
```
Then get the ssh port by typing:
```bash
$ docker port test_sshd 22
```

Make sure that the file `hosts` has correct port for ansible ssh connection (You probably have to change that)
To deploy, run:

```bash
$ ./deploy.sh "ansible_user=<SSH_USERNAME> ansible_ssh_pass=<SSH_PASSWORD> ansible_port=<SSH_PORT>"
```
