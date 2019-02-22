# Deploy with ansible
To test this deploy script locally, run some test container with ubuntu:
```bash
$ docker run --rm --privileged -d -P -p 80:80 -p 443:443 --name test_sshd rastasheep/ubuntu-sshd:14.04
```
Then get the ssh port by typing:
```bash
$ docker port test_sshd 22
```

Make sure that the file `hosts` has correct port and ip address for ansible ssh connection (You probably have to change that)
To deploy, run:

```bash
$ ./deploy.sh [quick] # quick keyword only deploys the app and essentials. It can be used if the app is deployed at least once without quick modifier
```
