# Introduction
You find here an example Containerfile to build an OCI compliant container image that you can run on any container infrastructure that you have.

This is an example and you need to secure it according to your needs. 

Additionally you need to regularly rebuild the container image and redeploy it to include the latest security fixes and latest version (e.g. latest JDK LTS).

# Base image
Every container has a base image. We use here the OpenSuSE base container image (see also [SLE BCI](https://opensource.suse.com/bci/), which are conceptually similar) that includes JDK LTS. 

# Build
We recommend to use the podman ecosystem to build and run the container. Podman is supported by virtually all Linux distributions out of the box. Podman is by default [rootless](https://rootlesscontaine.rs/) which offers significant security benefits. You can build container images using [buildah](https://buildah.io/).

Example how you can build it:
```
buildah build  -f ./Containerfile -t springbootweb .
```

# Deploy

You can run the container image using [podman run](https://docs.podman.io/en/latest/markdown/podman-run.1.html).

Example (run as non-root!):
```
podman run -p 8443:8443 springbootweb 
```

A more advanced version would be to use an immutable Linux Operating System (e.g. [OpenSuSE MicroOS](https://microos.opensuse.org/)) and use [podman-systemd](https://docs.podman.io/en/latest/markdown/podman-systemd.unit.5.html) (also known as [Quadlet](https://www.redhat.com/sysadmin/quadlet-podman)) to run the web application as a service so it is run after each restart of the operating system.