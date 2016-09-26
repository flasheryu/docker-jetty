# docker-jetty

Packages embedded jetty in a docker container.

## Example usage

```bash
docker run -d -p 8080:8080 -v /root/.m2/repository:/root/.m2/repository --name jettytest flasheryu/jetty
```

jenkins user:
flasheryu
stupidhackeryou